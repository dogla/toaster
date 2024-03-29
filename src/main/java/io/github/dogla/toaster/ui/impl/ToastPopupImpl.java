/**
 * Copyright (C) 2020-2024 Dominik Glaser
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
package io.github.dogla.toaster.ui.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.dogla.toaster.Toast;
import io.github.dogla.toaster.ToastAction;
import io.github.dogla.toaster.ToastColor;
import io.github.dogla.toaster.ToasterUtils;
import io.github.dogla.toaster.ui.ToastPopup;
import io.github.dogla.toaster.ui.ToastPopupClosedCallback;

/**
 * The default implementation of the {@link ToastPopup}.
 *
 * @author Dominik
 */
public class ToastPopupImpl extends Shell implements ToastPopup {
	
	private static Logger logger = LoggerFactory.getLogger(ToastPopupImpl.class);
	
	private Toast toast;
	private Rectangle popupArea;
	private Image oldImage;
	private List<Image> imagesToDispose = new ArrayList<>();

	private Composite textContainer;
	private Label lblTitle;
	private Label lblMessage;
	private Label lblDetails;
	private Composite iconContainer;
	private Canvas canvasIcon;
	private FadeInHandler fadeInHandler;

	// internal flag used in development
	private boolean highlightContainers = false;
	
	// close button settings
	private int closeButtonMargin = 5;
	private int closeButtonIconSize = 10;
	private int closeButtonContainerSize = closeButtonIconSize+2*closeButtonMargin;

	/**
	 * Constructor.
	 *
	 * @param shell the shell
	 * @param toast the toast
	 * @param popupArea the popup area
	 */
	public ToastPopupImpl(Shell shell, Toast toast, Rectangle popupArea) {
		super(shell, (SWT.NO_FOCUS | SWT.NO_TRIM | SWT.ON_TOP) & ~SWT.APPLICATION_MODAL);
		this.toast = toast;
		this.popupArea = popupArea;
		setBackgroundMode(SWT.INHERIT_FORCE);
		createContent(toast);
	}
	
	/**
	 * Creates the content for the given toast.
	 * 
	 * @param toast the toast
	 */
	protected void createContent(Toast toast) {
		setLayout(new GridLayout(1, false));
		if (highlightContainers) {
			setBackground(this.getDisplay().getSystemColor(SWT.COLOR_CYAN));
		}

		Color titleForeground = toSWTColor(toast.getTitleForegroundColor());
		Color background = toSWTColor(toast.getBackgroundColor());
		Color backgroundTop = toSWTColor(toast.getBackgroundColor());//ToasterUtils.brighter(ToasterUtils.brighter(toast.getBackgroundColor()));

		Image image = toImage(toast);
		if (image != null) {
			setLayout(new GridLayout(2, false));
			iconContainer = new Composite(this, SWT.NONE);
			iconContainer.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, false, true));
			iconContainer.setLayout(new GridLayout(1, false));
			if (highlightContainers) {
				iconContainer.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			}
			updateIcon(image);			
		}
		
		textContainer = new Composite(this, SWT.NONE);
		textContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout textContainerLayout = new GridLayout(2, false);
		textContainerLayout.marginHeight = 0;
		textContainerLayout.marginWidth = 0;
		textContainer.setLayout(textContainerLayout);
		if (highlightContainers) {
			textContainer.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		}
		
		boolean sticky = isSticky();
		
		lblTitle = new Label(textContainer, SWT.WRAP);
		updateTitle();
		
		if (sticky) {
			Canvas lblClose = new Canvas(textContainer, SWT.NONE) {
				@Override
				public Point computeSize(int wHint, int hHint, boolean changed) {
					return new Point(closeButtonContainerSize, closeButtonContainerSize);
				}
			};
			GridData layoutData = new GridData(SWT.END, SWT.BEGINNING, true, false);
			layoutData.minimumWidth = closeButtonContainerSize;
			lblClose.setLayoutData(layoutData);
			boolean[] mouseIn = new boolean[1];
			lblClose.addPaintListener(e -> {
				e.gc.setAntialias(SWT.ON);
				if (mouseIn[0]) {
					Color backgroundColor = toSWTColor(toast.getActionsBackgroundColor().brighter());
					e.gc.setBackground(backgroundColor);
					e.gc.fillRectangle(0, 0, closeButtonContainerSize, closeButtonContainerSize);
				}
	            e.gc.drawLine(closeButtonMargin, closeButtonMargin, closeButtonMargin+closeButtonIconSize, closeButtonMargin+closeButtonIconSize);
	            e.gc.drawLine(closeButtonMargin, closeButtonMargin+closeButtonIconSize, closeButtonMargin+closeButtonIconSize, closeButtonMargin);
			});
			lblClose.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseEnter(MouseEvent e) {
					mouseIn[0] = true;
					lblClose.redraw();
				}
				@Override
				public void mouseExit(MouseEvent e) {
					mouseIn[0] = false;
					lblClose.redraw();
				}
			});
			lblClose.addMouseListener(MouseListener.mouseUpAdapter(e -> close()));
			lblClose.setForeground(titleForeground);
			if (highlightContainers) {
				lblClose.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			}
		}
		
		lblMessage = new Label(textContainer, SWT.WRAP);
		updateMessage();

		String details = toast.getDetails();
		if (details != null && !details.trim().isEmpty()) {
			updateDetails();
		}
		
		// if we are not sticky every click should close the toast
		if (!sticky) {
			List<Control> controls = getControls(this);
			for (Control control : controls) {
				control.addMouseListener(MouseListener.mouseUpAdapter(e -> {
					if (!ToastPopupImpl.this.isDisposed()) {
						close();
					}
				}));
			}
		}
		
		List<ToastAction> actions = toast.getActions();
		for (ToastAction action : actions) {
			
			ToastColor backgroundColor = action.getBackgroundColor(toast.getActionsBackgroundColor());
			ToastColor backgroundColorHovered = action.getBackgroundColorHovered(toast.getActionsBackgroundColorHovered());
			ToastColor foregroundColor = action.getForegroundColor(toast.getActionsForegroundColor());
			ToastColor foregroundColorHovered = action.getForegroundColorHovered(toast.getActionsForegroundColorHovered());
			
			CLabel lblAction = new CLabel(this, SWT.CENTER);
			lblAction.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			lblAction.setText(action.getText());
			lblAction.setToolTipText(action.getTooltip());
			lblAction.setImage(action.getImage());
			lblAction.setBackground(toSWTColor(backgroundColor));
			lblAction.setForeground(toSWTColor(foregroundColor));
			FontDescriptor actionFontDescriptor = FontDescriptor.createFrom(getFont()).increaseHeight(1);
			lblAction.setFont(actionFontDescriptor.createFont(getDisplay()));
			lblAction.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseEnter(MouseEvent e) {
					if (backgroundColorHovered != null) {
						lblAction.setBackground(toSWTColor(backgroundColorHovered));
					} else {
						lblAction.setBackground(toSWTColor(backgroundColor.brighter()));
					}
					if (foregroundColorHovered != null) {
						lblAction.setForeground(toSWTColor(foregroundColorHovered));
					}
				}
				@Override
				public void mouseExit(MouseEvent e) {
					lblAction.setBackground(toSWTColor(backgroundColor));
					lblAction.setForeground(toSWTColor(foregroundColor));
				}
			});
			lblAction.addMouseListener(MouseListener.mouseUpAdapter(e -> {
				if (action.isCloseToastOnExecute()) {
					close();
				}
				Consumer<Toast> executable = action.getExecutable();
				if (executable != null) {
					Thread thread = new Thread(() -> executable.accept(toast));
					thread.setUncaughtExceptionHandler((t, ex) -> logger.error(ex.getMessage(), ex));
					thread.start();
				}
			}));
		}
		
		addListener(SWT.Resize, event -> {
			Display display = getDisplay();
			Rectangle rect = getClientArea();
			Image newImage = new Image(display, Math.max(1, rect.width), Math.max(1, rect.height));
			GC gc = new GC(newImage);
			gc.setForeground(backgroundTop);
			gc.setBackground(background);
			gc.fillRectangle(0, 0, rect.width, rect.height);
			gc.fillGradientRectangle(0, 0, rect.width, 40, true);
			gc.setForeground(toSWTColor(toast.getBorderColor()));
			gc.drawRectangle(0, 0, rect.width-1, rect.height-1);
			gc.dispose();
			setBackgroundImage(newImage);
			if (oldImage != null) {
				oldImage.dispose();
			}
			oldImage = newImage;
		});
		addDisposeListener(e -> {
			if (oldImage != null) {
				oldImage.dispose();
			}
			for (Image img : imagesToDispose) {
				img.dispose();
			}
		});

		setSize(getInitialSize());
		
		// register update listener
		toast.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				getDisplay().asyncExec(() -> {
					String propertyName = evt.getPropertyName();
					if ("title".equals(propertyName)) { //$NON-NLS-1$
						updateTitle();
					} else if ("message".equals(propertyName)) { //$NON-NLS-1$
						updateMessage();
					} else if ("details".equals(propertyName)) { //$NON-NLS-1$
						updateDetails();
					} else if ("icon".equals(propertyName) //$NON-NLS-1$
							|| "displayTime".equals(propertyName)) { //$NON-NLS-1$
						Image image = toImage(toast);
						if (image != null) {
							updateIcon(image);
						}
					} 
					if (fadeInHandler != null) {
						fadeInHandler.restartFadeOutProcess();
					}
				});
			}
		});
	}

	private void updateTitle() {
		if (lblTitle != null && !lblTitle.isDisposed()) {
			lblTitle.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false, isSticky() ? 1 : 2, 1));
			if (toast.getTitle() != null) {
				lblTitle.setText(toast.getTitle());
			}
			Color titleForeground = toSWTColor(toast.getTitleForegroundColor());		
			lblTitle.setForeground(titleForeground);
			FontDescriptor titleFontDescriptor = FontDescriptor.createFrom(getFont()).increaseHeight(2).setStyle(SWT.BOLD);
			lblTitle.setFont(titleFontDescriptor.createFont(getDisplay()));
			if (highlightContainers) {
				lblTitle.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			}
			lblTitle.getParent().layout();
		}
	}
	
	private void updateMessage() {
		if (lblMessage != null && !lblMessage.isDisposed()) {
			lblMessage.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false, 2, 1));
			if (toast.getMessage() != null) {
				lblMessage.setText(toast.getMessage());
			}
			lblMessage.setForeground(toSWTColor(toast.getMessageForegroundColor()));
			FontDescriptor messageFontDescriptor = FontDescriptor.createFrom(getFont()).increaseHeight(1);
			lblMessage.setFont(messageFontDescriptor.createFont(getDisplay()));
			if (highlightContainers) {
				lblMessage.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			}
			lblMessage.getParent().layout();
		}
	}
	
	private void updateDetails() {
		if (lblDetails != null && !lblDetails.isDisposed()) {
			lblDetails = new Label(textContainer, SWT.WRAP);
			lblDetails.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, false, 2, 1));
			lblDetails.setText(toast.getDetails());
			lblDetails.setForeground(toSWTColor(toast.getDetailsForegroundColor()));
			FontDescriptor detailsFontDescriptor = FontDescriptor.createFrom(getFont()).increaseHeight(-1);
			lblDetails.setFont(detailsFontDescriptor.createFont(getDisplay()));
			if (highlightContainers) {
				lblDetails.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_GREEN));
			}
			lblDetails.getParent().layout();
		}
	}
	
	private void updateIcon(Image image) {
		if (iconContainer != null && !iconContainer.isDisposed()) {
			if (canvasIcon != null) {
				canvasIcon.dispose();
			}
			
			// image
			Point canvasSize = new Point(0, 0);
			double scale = 1;
			Rectangle origBounds = image.getBounds();
			Point imageSize = new Point(origBounds.width, origBounds.height);
			canvasSize.x = imageSize.x;
			canvasSize.y = imageSize.y;
			
			int maxIconWidth = toast.getMaxIconWidth();
			// adjust max icon width to fit into container/margins
			if (maxIconWidth > toast.getMaxWidth() - 20) {
				maxIconWidth = toast.getMaxWidth() - 20;
			}
			// adjust max icon width if we have to show the close button
			int margins = closeButtonIconSize + 8*5;
			if (isSticky() && maxIconWidth > toast.getMaxWidth() - margins) {
				maxIconWidth = toast.getMaxWidth() - margins;
			}
			int minIconWidth = toast.getMinIconWidth();
			// check min icon width
			if (minIconWidth > maxIconWidth) {
				minIconWidth = maxIconWidth;
			}
			
			// check min width
			if (toast.isAllowIconUpscaling() && imageSize.x < minIconWidth) {
				// scale up
				double aScale = minIconWidth * 1.0 / imageSize.x;
				imageSize.x = minIconWidth;
				imageSize.y *= aScale;
				canvasSize.x = imageSize.x;
				canvasSize.y = imageSize.y;
				scale = minIconWidth * 1.0 / origBounds.width;
			}
			
			// check min height
			if (toast.isAllowIconUpscaling() && imageSize.y < toast.getMinIconHeight()) {
				// scale up
				double aScale = toast.getMinIconHeight() * 1.0 / imageSize.y;
				imageSize.x *= aScale;
				imageSize.y = toast.getMinIconHeight();
				canvasSize.x = imageSize.x;
				canvasSize.y = imageSize.y;
				scale = toast.getMinIconHeight() * 1.0 / origBounds.height;
			}
			
			// check max width
			if (maxIconWidth >= 0 && imageSize.x > maxIconWidth) {
				// scale down
				double aScale = maxIconWidth * 1.0 / imageSize.x;
				imageSize.x = maxIconWidth;
				imageSize.y *= aScale;
				canvasSize.x = imageSize.x;
				canvasSize.y = imageSize.y;
				scale = maxIconWidth * 1.0 / origBounds.width;
			}
			
			// check max height
			if (toast.getMaxIconHeight() >= 0 && imageSize.y > toast.getMaxIconHeight()) {
				// scale down
				double aScale = toast.getMaxIconHeight() * 1.0 / imageSize.y;
				imageSize.x *= aScale;
				imageSize.y = toast.getMaxIconHeight();
				canvasSize.x = imageSize.x;
				canvasSize.y = imageSize.y;
				scale = toast.getMaxIconHeight() * 1.0 / origBounds.height;
			}
			
			if (canvasSize.x < minIconWidth) {
				canvasSize.x = minIconWidth;
			}
			if (canvasSize.y < toast.getMinIconHeight()) {
				canvasSize.y = toast.getMinIconHeight();
			}
			canvasIcon = new Canvas(iconContainer, SWT.NONE) {
				@Override
				public Point computeSize(int wHint, int hHint, boolean changed) {
					return canvasSize;
				}
			};
			canvasIcon.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true));
			if (highlightContainers) {
				canvasIcon.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_RED));
			}
			double imageScale = scale;
			canvasIcon.addPaintListener(e -> {
				e.gc.setAntialias(SWT.ON);
				Color backgroundColor = toSWTColor(toast.getIconBackgroundColor());
				e.gc.setBackground(backgroundColor);
				e.gc.fillRectangle(0, 0, canvasSize.x, canvasSize.y);
				if (!image.isDisposed()) {
					drawCentered(e.gc, image, imageScale, canvasSize.x, canvasSize.y);
				}
			});
			canvasIcon.getParent().layout();
		}
	}

	private Image toImage(Toast toast) {
		Object value = toast.getIcon();
		if (value instanceof Image) {
			return (Image)value;
		}
		if (value instanceof ImageDescriptor) {
			ImageDescriptor imageDescriptor = (ImageDescriptor)value;
			return imageDescriptor.createImage();
		}
		if (value != null) {
			Image image = ToasterUtils.toImage(getDisplay(), value);
			if (image != null) {
				imagesToDispose.add(image);
				return image; 
			}
		}
		return null;
	}

	private static void drawCentered(GC gc, Image img, double scale, int maxWidth, int maxHeight) {
		int srcWidth = img.getBounds().width;
		int srcHeight = img.getBounds().height;
		int destWidth = (int)Math.min(srcWidth*scale, maxWidth);
		int destHeight = (int)Math.min(srcHeight*scale, maxHeight);
		int destX = (maxWidth - destWidth) / 2;
		int destY = (maxHeight - destHeight) / 2;
		gc.drawImage(img, 0, 0, srcWidth, srcHeight, destX, destY, destWidth, destHeight);
	}
	
	private Color toSWTColor(ToastColor rgb) {
		return new Color(getShell().getDisplay(), rgb.getRed(), rgb.getGreen(), rgb.getBlue());
	}

	@Override
	protected void checkSubclass() {
		// nothing
	}
	
	/**
	 * @return the initial size
	 */
	protected Point getInitialSize() {
		Point preferredSize = this.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		
		Point constrainedSize = computeConstrainedSize(preferredSize, toast.getMinWidth(), toast.getMinHeight(), toast.getMaxWidth(), toast.getMaxHeight());
		if (constrainedSize.x != preferredSize.x) {
			// width changed > recompute height
			preferredSize.y = this.computeSize(constrainedSize.x, SWT.DEFAULT, true).y;
			return computeConstrainedSize(preferredSize, toast.getMinWidth(), toast.getMinHeight(), toast.getMaxWidth(), toast.getMaxHeight());
		}
		if (constrainedSize.y != preferredSize.y) {
			// height changed > recompute width
			preferredSize.x = this.computeSize(SWT.DEFAULT, constrainedSize.y, true).x;
			return computeConstrainedSize(preferredSize, toast.getMinWidth(), toast.getMinHeight(), toast.getMaxWidth(), toast.getMaxHeight());
		}
		return constrainedSize;
	}
	
	/**
	 * Computes the constrained size of the notification popup.
	 * 
	 * @param initialSize the initial size
	 * @param minWidth the min width
	 * @param minHeight the min height
	 * @param maxWidth the max width
	 * @param maxHeight the max height
	 * @return the constrained size
	 */
	protected Point computeConstrainedSize(Point initialSize, int minWidth, int minHeight, int maxWidth, int maxHeight) {
		Point size = new Point(initialSize.x, initialSize.y);
		// consider min size
		size.x = Math.max(size.x, minWidth >= 0 ? minWidth : size.x);
		size.y = Math.max(size.y, minHeight >= 0  ? minHeight : size.y);
		// consider max size
		size.x = Math.min(size.x, maxWidth >= 0 ? maxWidth : size.x);
		size.y = Math.min(size.y, maxHeight >= 0  ? maxHeight : size.y);
    	return size;
	}
	
	@Override
	public Toast getToast() {
		return toast;
	}
	
	@Override
	public Rectangle getPopupArea() {
		return popupArea;
	}

	@Override
	public int getWidth() {
		return getSize().x;
	}

	@Override
	public int getHeight() {
		return getSize().y;
	}

	@Override
	public void show(ToastPopupClosedCallback callback) {
		fadeInHandler = new FadeInHandler();
		fadeInHandler.run();
		addDisposeListener(e -> callback.onClosed());
	}
	
	@Override
	public void close() {
		if (Display.getCurrent() == null || (Display.findDisplay(Thread.currentThread()) != getDisplay())) {
			getDisplay().syncExec(super::close);
		} else {
			super.close();
		}
	}
	
	/**
	 * @return <code>true</code> if the popup should be sticky
	 */
	protected boolean isSticky() {
		return toast.isSticky()/* || !toast.getActions().isEmpty()*/;
	}
	
    /**
     * @param control the control
     * 
     * @return the control and all child controls
     */
    protected List<Control> getControls(Control control) {
        List<Control> result = new ArrayList<>();
        result.add(control);
        if (control instanceof Composite) {
            Composite c = (Composite) control;
            Control[] children = c.getChildren();
            for (Control child : children) {
                result.addAll(getControls(child));
            }
        }
        return result;
    }	

    private class FadeInHandler implements Runnable {
    	
    	private boolean starting = true;
		private FadeOutHandler fadeOutHandler;
    	
    	private FadeInHandler() {
			if (!isSticky() && toast.getDisplayTime() > 0) {
				this.fadeOutHandler = new FadeOutHandler();
			}
    	}
    	
    	@Override
    	public void run() {
            try {
            	Shell shell = ToastPopupImpl.this;
                if (shell.isDisposed()) { return; }

                if (starting) {
                	starting = false; 
                	
                	shell.setAlpha(0);
                	shell.setVisible(true);
                }

                int transparency = toast.getTransparency();
                int displayTime = toast.getDisplayTime();
                int fadeInSteps = Math.max(toast.getFadeInSteps(), 1);
                int fadeInTime = toast.getFadeInTime();
                
                List<Control> controls = getControls(shell);
				for (Control control : controls) {
                    control.addListener(SWT.MouseEnter, event -> {
						// stop fadeout
						if (fadeOutHandler != null) {
							if (shell.getAlpha() != transparency) {
								shell.setAlpha(transparency);
							}
							shell.getDisplay().timerExec(-1, fadeOutHandler);
						}
					});
                    
                    control.addListener(SWT.MouseExit, event -> {
						// start fade out process again
						restartFadeOutProcess();
					});
        		}
                
                int cur = shell.getAlpha();
				cur += Math.ceil(transparency * 1.0 / fadeInSteps);

                if (cur > transparency) {
                    shell.setAlpha(transparency);
                    if (fadeOutHandler != null) {
                    	shell.getDisplay().timerExec(displayTime, fadeOutHandler);
                    }
                    return;
                }

                shell.setAlpha(cur);
				shell.getDisplay().timerExec(fadeInTime / fadeInSteps, this);
            } catch (Exception e) {
            	logger.error(e.getMessage(), e);
            }
        }
    	
    	public void restartFadeOutProcess() {
        	Shell shell = ToastPopupImpl.this;
            if (shell.isDisposed()) { return; }
    		if (fadeOutHandler != null) {
    			shell.getDisplay().timerExec(toast.getDisplayTime(), fadeOutHandler);
			}
    	}
    }
    
    private class FadeOutHandler implements Runnable {
    	
    	@Override
    	public void run() {
            try {
            	Shell shell = ToastPopupImpl.this;
                if (shell.isDisposed()) { return; }

                int fadeOutSteps = Math.max(toast.getFadeOutSteps(), 1);
                
                int cur = shell.getAlpha();
				cur -= Math.ceil(toast.getTransparency() * 1.0 / fadeOutSteps);

                if (cur <= 0) {
                    shell.setAlpha(0);
                    shell.dispose();
                    return;
                }

                shell.setAlpha(cur);
                shell.getDisplay().timerExec(toast.getFadeOutTime() / fadeOutSteps, this);
            } catch (Exception e) {
            	logger.error(e.getMessage(), e);
            }
    	}
    	
    }

	
}
