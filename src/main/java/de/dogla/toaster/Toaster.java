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

import de.dogla.toaster.ui.ToastToolkit;
import de.dogla.toaster.ui.impl.ToastToolkitImpl;

/**
 * The entry class to show a toast.
 */
public class Toaster {
	
	private static ToastToolkit DEFAULT_TOOLKIT;
	
	private Toaster() {
		// utility class
	}
	
	/**
	 * Sets the default toolkit.
	 * 
	 * @param toolkit the default toolkit
	 */
	public static void setDefaultToolkit(ToastToolkit toolkit) {
		DEFAULT_TOOLKIT = toolkit;
	}
	
	/**
	 * Shows the toast with the default toolkit.
	 * If no default toolkit was specified {@link IllegalStateException} will be thrown.
	 * 
	 * @param toast the toast
	 */
	public static synchronized void toast(Toast toast) {
		if (DEFAULT_TOOLKIT == null) {
			DEFAULT_TOOLKIT = new ToastToolkitImpl();
		}
		toast(DEFAULT_TOOLKIT, toast);
	}
	
	/**
	 * Shows the toast with the given toolkit.
	 * @param toolkit the toolkit
	 * @param toast the toast
	 */
	public static synchronized void toast(ToastToolkit toolkit, Toast toast) {
		if (toolkit == null) {
			throw new IllegalStateException("Toast toolkit was null."); //$NON-NLS-1$
		}
		if (toast == null) {
			throw new IllegalStateException("Toast was null."); //$NON-NLS-1$
		}
		ToastManager.getInstance().toast(toolkit, toast);
	}
	
}
