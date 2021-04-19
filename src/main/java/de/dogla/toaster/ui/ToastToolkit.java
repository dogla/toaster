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
package de.dogla.toaster.ui;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import de.dogla.toaster.Toast;

/**
 * The toast UI toolkit.
 *
 * @author Dominik
 */
public interface ToastToolkit {
	
	/**
	 * Subclasses have to return the corresponding {@link ToastPopup} instance for the given toast.
	 * 
	 * @param toast the toat
	 * @param popupArea the area where the popup can appear
	 * 
	 * @return the corresponding toast popup
	 */
	public ToastPopup createPopup(Toast toast, Rectangle popupArea);

	/**
	 * Returns the area where the popup can be displayed.
	 * For example this could be the client area of the primary monitor or the client area of the monitor where the current application is displayed.
	 * This method is called from the main display thread. 
	 * 
	 * @return the popup area 
	 */
	public Rectangle getPopupArea();
	
	/**
	 * @return the display for the popups
	 */
	public Display getPopupDisplay();

}
