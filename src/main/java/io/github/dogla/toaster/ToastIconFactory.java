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
package io.github.dogla.toaster;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.eclipse.swt.graphics.Image;

/**
 * The factory interface for a A simple toast.
 */
public interface ToastIconFactory {
	
	/**
	 * @param value the value
	 * 
	 * @return <code>true</code> if this factory can convert the given value to an {@link Image} instance
	 */
	public boolean canHandle(Object value);
	
	/**
	 * @param value the value
	 * @return the corresponding {@link Image} instance
	 */
	public Image toImage(Object value);
	
	/**
	 * @return all registered {@link ToastIconFactory} instanes
	 */
	public static ToastIconFactory[] getToastIconFactories() {
		List<ToastIconFactory> result = new ArrayList<>();
		ServiceLoader<ToastIconFactory> providers = ServiceLoader.load(ToastIconFactory.class);
		providers.forEach(provider -> {
			result.add(provider);
		});
		return result.toArray(new ToastIconFactory[result.size()]);
	}	
	
}
