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

import java.util.function.Consumer;

import org.eclipse.swt.graphics.Image;

import lombok.Builder;
import lombok.Getter;

/**
 * A toast action.
 */
@Builder @Getter
public class ToastAction {
	
	private String text;
	private String tooltip;
	private Image image;
	private Consumer<Toast> executable;
	private ToastColor backgroundColor;
	private ToastColor foregroundColor;
	private ToastColor backgroundColorHovered;
	private ToastColor foregroundColorHovered;
	
	/**
	 * @param defaultColor
	 * 
	 * @return the corresponding background color or the given default color if no background color was explicitely specified for this action
	 */
	public ToastColor getBackgroundColor(ToastColor defaultColor) {
		if (backgroundColor != null) {
			return backgroundColor;
		}
		return defaultColor;
	}
	
	/**
	 * @param defaultColor
	 * 
	 * @return the corresponding hovered background color or the given default color if no hovered background color was explicitely specified for this action
	 */
	public ToastColor getBackgroundColorHovered(ToastColor defaultColor) {
		if (backgroundColorHovered != null) {
			return backgroundColorHovered;
		}
		return defaultColor;
	}
	
	/**
	 * @param defaultColor
	 * 
	 * @return the corresponding foreground color or the given default color if no foreground color was explicitely specified for this action
	 */
	public ToastColor getForegroundColor(ToastColor defaultColor) {
		if (foregroundColor != null) {
			return foregroundColor;
		}
		return defaultColor;
	}
	
	/**
	 * @param defaultColor
	 * 
	 * @return the corresponding hovered foreground color or the given default color if no hovered foreground color was explicitely specified for this action
	 */
	public ToastColor getForegroundColorHovered(ToastColor defaultColor) {
		if (foregroundColorHovered != null) {
			return foregroundColorHovered;
		}
		return defaultColor;
	}
	
}
