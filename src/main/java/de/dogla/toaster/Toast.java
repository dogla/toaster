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

import java.util.List;
import java.util.UUID;

import de.dogla.toaster.ui.ToastPopup;
import de.dogla.toaster.ui.ToastToolkit;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Singular;

/**
 * A simple toast.
 */
@Builder(toBuilder = true) @Getter
public class Toast {
	
	@Default
	private String id = UUID.randomUUID().toString();
	
	// notification attributes
	private String title;
	private String message;
	private String details;
	private boolean sticky;
	private Object icon;
	private Object data;
	@Singular
	private List<ToastAction> actions;
	
	// layout attributes
	@Default
	private ToastPosition position = ToastPosition.BOTTOM_RIGHT;
	@Default
	private int minWidth = 350;
	@Default
	private int minHeight = -1;
	@Default
	private int maxWidth = 350;
	@Default
	private int maxHeight = -1;
	
	// icon dimension attributes
	@Default
	private int minIconWidth = 64;
	@Default
	private int minIconHeight = 64;
	@Default
	private int maxIconWidth = 64;
	@Default
	private int maxIconHeight = 64;
	@Default
	private boolean allowIconUpscaling = false;
	
	// style attributes
	@Default
	private ToastColor backgroundColor = new ToastColor(30, 30, 30);
	@Default
	private ToastColor iconBackgroundColor = new ToastColor(30, 30, 30);
	@Default
	private ToastColor borderColor = new ToastColor(20, 20, 20);
	@Default
	private ToastColor titleForegroundColor = new ToastColor(255, 255, 255);
	@Default
	private ToastColor messageForegroundColor = new ToastColor(148, 148, 148);
	@Default
	private ToastColor detailsForegroundColor = new ToastColor(148, 148, 148);
	@Default
	private ToastColor actionsBackgroundColor = new ToastColor(55, 55, 55);
	@Default
	private ToastColor actionsForegroundColor = new ToastColor(255, 255, 255);
	@Default
	private ToastColor actionsBackgroundColorHovered = null;
	@Default
	private ToastColor actionsForegroundColorHovered = null;
	@Default
	private int transparency = 255;

	// animation attributes
	@Default
	private int displayTime = 5000;
	@Default
	private int fadeInTime = 200;
	@Default
	private int fadeOutTime = 600;
	@Default
	private int fadeInSteps = 8;
	@Default
	private int fadeOutSteps = 24;
	
	/**
	 * @return the builder
	 */
	public ToastBuilder copy() {
		return toBuilder().id(UUID.randomUUID().toString());
	}
	
	/**
	 * Shows the toast with the default toolkit.
	 * 
	 * @see Toaster#toast(Toast)
	 * 
	 * @return the corresponding toast popup
	 */
	public ToastPopup toast() {
		return Toaster.toast(this);
	}
	
	/**
	 * Shows the toast with the given toolkit.
	 * 
	 * @param toolkit the toolkit
	 */
	public void toast(ToastToolkit toolkit) {
		Toaster.toast(toolkit, this);
	}

	/* (non-javadoc)
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("nls")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Toast [id=").append(id)
			.append(", title=").append(title)
			.append(", message=").append(message)
		.append("]");
		return builder.toString();
	}

	/* (non-javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Toast other = (Toast) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
}
