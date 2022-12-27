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
package io.github.dogla.toaster;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class.
 *
 * @author Dominik
 */
public class ToasterUtils {
	
	private static Logger logger = LoggerFactory.getLogger(ToasterUtils.class);
	
	private ToasterUtils() {
		// utility class
	}
	
	/**
	 * @param base64EncodedImage the base64 encoded image
	 *
	 * @return the corresponding image
	 * @throws SWTException
	 */
	public static Image fromBase64(String base64EncodedImage) throws SWTException {
		byte[] imageContent = Base64.getDecoder().decode(base64EncodedImage.getBytes());
		return new Image(null, new ByteArrayInputStream(imageContent));
	}

	/**
	 * @param value the value
	 * 
	 * @return the corresponding {@link Image}
	 */
	public static Image toImage(Object value) {
		if (value instanceof Image) {
			return (Image)value;
		}
		if (value instanceof ImageDescriptor) {
			ImageDescriptor imageDescriptor = (ImageDescriptor)value;
			return imageDescriptor.createImage();
		}
		if (value instanceof String) {
			String sValue = (String)value;
			
			// handle base64
			if (sValue.matches("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$")) { //$NON-NLS-1$
				try {
					return ToasterUtils.fromBase64(sValue);
				} catch (SWTException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		if (value != null) {
			logger.info("Unhandled image detected:\n- Class: {}\n- toString(): {}", value.getClass().getName(), value); //$NON-NLS-1$
		}
		return null;
	}	
}
