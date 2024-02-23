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
package io.github.dogla.toaster.ui;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import io.github.dogla.toaster.Toast;

/**
 * The toast popup interface.
 *
 * @author Dominik
 */
public interface ToastPopup {
	
	/**
	 * Returns the width of the popup.
	 * 
	 * @return the width of the popup 
	 */
	public int getWidth();
	
	/**
	 * Returns the height of the popup.
	 * 
	 * @return the height of the popup 
	 */
	public int getHeight();

	/**
	 * Sets the location of the popup.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void setLocation(int x, int y);
	
	/**
	 * This method is called if the corresponding toast popup has to be displayed.
	 * If the popup is closed the implementor of this interface has to call the given callback.
	 * 
	 * @param callback the callback
	 */
	public void show(ToastPopupClosedCallback callback);
	
	/**
	 * @return the display of the popup
	 */
	public Display getDisplay();
	
	/**
	 * @return the underlying toast
	 */
	public Toast getToast();
	
	/**
	 * @return the popup area 
	 */
	public Rectangle getPopupArea();
	
	/**
	 * Closes the popup programmatically.
	 */
	public void close();
	
}
