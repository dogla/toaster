/**
 * Copyright (C) 2020-2021 Dominik Glaser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dogla.toaster;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dogla.toaster.ui.ToastPopup;
import de.dogla.toaster.ui.ToastToolkit;

@SuppressWarnings("nls")
/*package*/ class ToastManager {
	
	private static Logger logger = LoggerFactory.getLogger(ToastManager.class);
	
	private Map<ToastPosition, Set<Rectangle>> visibleToasts = new HashMap<>();
	private List<ToastPopup> visiblePopups = new ArrayList<>();
	private Deque<ToastRequest> pendingToasts = new ArrayDeque<>(); 
	
	private static ToastManager INSTANCE = new ToastManager();
	
	protected static ToastManager getInstance() {
		return INSTANCE;
	}
	
	protected void toast(ToastToolkit toolkit, Toast toast) {
		// get popup area in current display thread
		Rectangle monitorClientArea = toolkit.getPopupArea();
		toolkit.getPopupDisplay().syncExec(() -> {
			// create/show in popup display thread
			toast(new ToastRequest(toolkit, toast, monitorClientArea), false);
		});
	}
	
	protected void toast(ToastRequest toastRequest, boolean isPendingToast) {
		synchronized (visibleToasts) {
			Toast toast = toastRequest.getToast();
			// previous toasts already pending > add the new toast directly to the queue
			if (!isPendingToast && !pendingToasts.isEmpty()) {
				logger.debug("Other toasts already pending. Added toast to the queue: {}", toast);
				pendingToasts.add(toastRequest);
				return;
			}
			
			// compute location for new UI 
			ToastPopup toastPopup = toastRequest.getOrCreatePopup();
			ToastPosition toastPosition = toast.getPosition();
			Point position = computeLocation(toastPosition, toastPopup);
			if (position != null) {
				// relocate UI
				toastPopup.setLocation(position.x, position.y);
				
				// remember height
				Rectangle rectangle = new Rectangle(position.x, position.y, toastPopup.getWidth(), toastPopup.getHeight());
				visibleToasts.computeIfAbsent(toastPosition, k -> new HashSet<Rectangle>()).add(rectangle);
				
				// unregister pending toast
				if (isPendingToast) {
					logger.debug("Unregistering pending toast: {}", toast);
					pendingToasts.remove(toastRequest);
				}
				
				// show UI
				logger.debug("Showing toast: {}", toast);
				visiblePopups.add(toastPopup);
				toastPopup.show(() -> {
					logger.debug("Toast closed: {}", toast);
					synchronized (visibleToasts) {
						visiblePopups.remove(toastPopup);
						Set<Rectangle> set = visibleToasts.get(toastPosition);
						if (set != null) {
							if (!set.remove(rectangle)) {
								logger.error("Toast closed but corresponding rectangle was not found.");
							}
							// check for pending toasts
							if (!pendingToasts.isEmpty()) {
								ToastRequest pendingToastRequest = pendingToasts.peek();
								Toast pendingToast = pendingToastRequest.getToast();
								logger.debug("Pending toasts detected. Picking toast: {}", pendingToast);
								logger.debug("{} more pending tasks.", pendingToasts.size());
								
								ToastPosition pendingToastPosition = pendingToast.getPosition();
								ToastPopup pendingToastPopup = pendingToastRequest.getOrCreatePopup();
								Point pendingPosition = computeLocation(pendingToastPosition, pendingToastPopup);
								if (pendingPosition != null) {
									// show pending toast
									pendingToasts.remove(pendingToastRequest);
									toast(pendingToastRequest, true);
								} else {
									// no valid position found
									logger.debug("No free area found for Pending toast: {}", pendingToast);
								}
							}
						}
					}
				});
			} else if (!isPendingToast) {
				// no free area available -> queue until some toasts were closed
				logger.debug("No free area found for the toast. Added toast to the queue: {}", toast);
				pendingToasts.add(toastRequest);
			} else {
				// no free area available for already pended toast > requeue at the start
				logger.debug("No free area found for already pending toast. Readded toast to the queue: {}", toast);
				pendingToasts.addFirst(toastRequest);
			}
		}
	}

	private Point computeLocation(ToastPosition toastPosition, ToastPopup popup) {
		Rectangle popupArea = popup.getPopupArea();
		int toastWidth = popup.getWidth();
		int toastHeight = popup.getHeight();
		
		int minX = popupArea.x;
		int minY = popupArea.y;
		int maxX = popupArea.x + popupArea.width - toastWidth;
		int maxY = popupArea.y + popupArea.height - toastHeight;
		switch (toastPosition) {
			case BOTTOM_LEFT:
				return search(new Point(minX, maxY), toastWidth, toastHeight, minY, maxY, toastPosition, popup);
			case BOTTOM_RIGHT:
				return search(new Point(maxX, maxY), toastWidth, toastHeight, minY, maxY, toastPosition, popup);
			case TOP_LEFT:
				return search(new Point(minX, minY), toastWidth, toastHeight, minY, maxY, toastPosition, popup);
			case TOP_RIGHT:
				return search(new Point(maxX, minY), toastWidth, toastHeight, minY, maxY, toastPosition, popup);
			default:
				throw new IllegalStateException("Unhandled position detected");
		}
	}
	
	private Point search(Point location, int width, int height, int minY, int maxY, ToastPosition toastPosition, ToastPopup popup) {
		Point result = location;
		while (result != null && isForbidden(toastPosition, result, width, height)) {
			switch (toastPosition) {
				case BOTTOM_LEFT:
				case BOTTOM_RIGHT: {
					// search position further up
					int newY = result.y - 1;
					result = newY < minY ? null : new Point(result.x, newY);
					break;
				}
				default: {
					// search position further down
					int newY = result.y + 1;
					result = newY > maxY ? null : new Point(result.x, newY);
					break;
				}
			}
		}
		//inspectPopups(toastPosition, popup, result);
		return result;
	}

	@SuppressWarnings("unused")
	private void inspectPopups(ToastPosition toastPosition, ToastPopup popup, Point result) {
		Rectangle popupArea = popup.getPopupArea();
		Image image = new Image(popup.getDisplay(), popupArea.width, popupArea.height);
		GC gc = new GC(image);
		// draw invisible area
		Set<Rectangle> forbiddenAreas = visibleToasts.get(toastPosition);
		if (forbiddenAreas != null && !forbiddenAreas.isEmpty()) {
			gc.setBackground(new Color(popup.getDisplay(), 255, 0, 0));
			for (Rectangle rectangle : forbiddenAreas) {
				gc.fillRectangle(rectangle);
			}
		}
		// draw computed area
		gc.setForeground(new Color(popup.getDisplay(), 0, 0, 0));
		if (result != null) {
			// free area found
			gc.drawRectangle(result.x, result.y, popup.getWidth(), popup.getHeight());
		} else {
			// no area found
			gc.drawRectangle(popupArea.width-2*popup.getWidth(), popupArea.height-popup.getHeight(), popup.getWidth(), popup.getHeight());
		}
		
		gc.dispose();
		
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { image.getImageData() };
		imageLoader.save("D:\\toasts\\" + popup.getToast().getTitle() + ".png", SWT.IMAGE_PNG);
	}
	
	private boolean isForbidden(ToastPosition toastPosition, Point p, int width, int height) {
		Set<Rectangle> forbiddenAreas = visibleToasts.get(toastPosition);
		if (forbiddenAreas != null && !forbiddenAreas.isEmpty()) {
			for (Rectangle rectangle : forbiddenAreas) {
				if (rectangle.intersects(p.x, p.y, width, height)) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean isEmpty() {
		if (!pendingToasts.isEmpty()) {
			return false;
		}
		Set<Entry<ToastPosition, Set<Rectangle>>> entrySet = visibleToasts.entrySet();
		for (Entry<ToastPosition,Set<Rectangle>> entry : entrySet) {
			Set<Rectangle> value = entry.getValue();
			if (value != null && value.size() > 0) {
				return false;
			}
		}
		return true;
	}
	
	protected ToastPopup[] getVisiblePopups() {
		synchronized (visibleToasts) {
			return visiblePopups.toArray(new ToastPopup[visiblePopups.size()]);
		}
	}
	
	private static class ToastRequest {
		private ToastToolkit toolkit;
		private Toast toast;
		private Rectangle monitorClientArea;
		private ToastPopup popup;
		private ToastRequest(ToastToolkit toolkit, Toast toast, Rectangle monitorClientArea) {
			this.toolkit = toolkit;
			this.toast = toast;
			this.monitorClientArea = monitorClientArea;
		}
		ToastPopup getOrCreatePopup() {
			if (popup == null) {
				popup = toolkit.createPopup(toast, monitorClientArea);
			}
			return popup;
		}
		public Toast getToast() {
			return toast;
		}
	}
	
}
