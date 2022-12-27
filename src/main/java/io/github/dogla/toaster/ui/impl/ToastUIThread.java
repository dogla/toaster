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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Separate UI thread for the toast popup handling.
 *
 * @author Dominik
 */
public class ToastUIThread extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(ToastUIThread.class);
	
	Display display;
	Shell parentShell;
	
	/**
	 * Constructor.
	 */
	public ToastUIThread() {
		super("Toaster UI Thread"); //$NON-NLS-1$
	}

	@Override
	public void run() {
		display = new Display();
		try {
			parentShell = new Shell(display);
			
			while (!parentShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			display.dispose();
		}
	}
	
}
