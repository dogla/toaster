/**
 * Copyright (C) 2020-2022 Dominik Glaser
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import io.github.dogla.toaster.Toast;
import io.github.dogla.toaster.ui.ToastPopup;
import io.github.dogla.toaster.ui.ToastToolkit;

/**
 * The default toolkit implementation.
 *
 * @author Dominik
 */
public class ToastToolkitImpl implements ToastToolkit {

	private Shell mainShell;
	private ToastUIThread toastThread;
	private Rectangle mainShellClientArea;
	
	/**
	 * Constructor.
	 */
	public ToastToolkitImpl() {
		this(null);
	}

	/**
	 * Constructor.
	 *
	 * @param mainShell the main shell where the popup should be displayed
	 */
	public ToastToolkitImpl(Shell mainShell) {
		this.mainShell = mainShell;
		if (this.mainShell != null) {
			this.mainShell.addControlListener(new ControlListener() {
				@Override
				public void controlResized(ControlEvent e) {
					mainShellClientArea = ToastToolkitImpl.this.mainShell.getMonitor().getClientArea();
				}
				@Override
				public void controlMoved(ControlEvent e) {
					mainShellClientArea = ToastToolkitImpl.this.mainShell.getMonitor().getClientArea();
				}
			});
			this.mainShellClientArea = this.mainShell.getMonitor().getClientArea();
		}
		this.toastThread = new ToastUIThread();
		this.toastThread.setDaemon(true);
		this.toastThread.start();
	}
	
	@Override
	public Display getPopupDisplay() {
		return toastThread.display;
	}

	@Override
	public ToastPopup createPopup(Toast toast, Rectangle popupArea) {
		return new ToastPopupImpl(toastThread.parentShell, toast, popupArea);
	}

	@Override
	public Rectangle getPopupArea() {
		Rectangle[] result = new Rectangle[1];
		if (mainShell != null) {
			result[0] = mainShellClientArea;
		} else {
			while (getPopupDisplay() == null) {
				LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(100));
			}
			getPopupDisplay().syncExec(() -> {
				result[0] = getPopupDisplay().getPrimaryMonitor().getClientArea();
			});
		}
		return result[0];
	}

}
