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
package de.dogla.toaster.ui.impl;

import de.dogla.toaster.Toast;
import de.dogla.toaster.ToastAction;

/**
 * A simple dialog that shows the possible settings for the toaster.
 *
 * @author Dominik
 */
public class ToasterMain {
	
	private static final String IMG_DEMO = "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";

	public static void main(String[] args) throws InterruptedException {
		Toast.builder()
			.title("Updates available")
			.message("There are 5 updates available.")
			.details("Size: 125 MB")
			.icon(IMG_DEMO)
			.sticky(true)
			.action(ToastAction.builder()
						.text("Update now")
						.executable(t -> System.err.println("Update started..."))
						.build())
			.action(ToastAction.builder()
						.text("Remind me later")
						.executable(t -> System.err.println("Remind me later."))
						.build())
			.build()
			.toast();
		
		// keep the program alive for 10 seconds
		Thread.sleep(10000);
	}
	
}
