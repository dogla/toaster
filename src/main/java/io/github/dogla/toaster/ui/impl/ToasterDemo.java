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

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import io.github.dogla.toaster.Toast;
import io.github.dogla.toaster.Toast.ToastBuilder;
import io.github.dogla.toaster.ToastAction;
import io.github.dogla.toaster.ToastColor;
import io.github.dogla.toaster.ToastPosition;
import io.github.dogla.toaster.Toaster;
import io.github.dogla.toaster.ToasterUtils;

/**
 * A simple dialog that shows the possible settings for the toaster.
 *
 * @author Dominik
 */
@SuppressWarnings("nls")
public class ToasterDemo extends Shell {
	
	private static final String IMG_TOASTER = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAClUlEQVR4nO2bvWsUQRjGV/xAOwXFRizFkEY4b2YP/AjYiTZCKkGsLC1sLIKooI2CiBYSj+zMndgcWmiRIPgH+AGKYCkqxG/udsZ4CZ5JXJ/ZHKLImXP39t5NfF94uN0tdp/nN7MzswPneVxcXL2uIfVqrRizO30dHpaBOSu1uSFVeE8q8xR6AzWgJq7P4ncuPlamjvNJEZgnMrDjOB+DTvnKHCqp+vahM9Eq6lwdq1SbXCeC8IBQ5goMP4Lxb1DUY03j3vfxjNOlwOwdrkUrSUMXRqPVrnXQwndgbiaDwIupgWdXfBUe7GvvKIxObRSBPQcDHwlCd9I7X9nzstzYklnwHcqslzq82H5fqQN3UgsgrhWq4dbeJY+iFejqR3PW4otpBgPwyGAtWpMquyw3NwtlJ3IQKKmeF3VjMFH4orb7MOJ+ykGItJoW2hz5t5bX9lh7jqY23zO56bOr8BhETlKbzUq+Npf+Hl6b49Qms4cQjnR85+XCspTcZMb6jmX5/t/Cx3O8Mh9yYK5fqrsF3a9d/2oOTPVVmBnKcfjdwZdNGCG/Uhsi0Fy8YsQq70QOzJDIfT94S3yll0pu78HDwVtqI4SadwBaOTBCJgcgpDZBCuB/HgOgl57bUsqBERL9XBbjpEpthkAP3e51DMBtcOLCZWg+B8YyFxZ+d3fdtBv++CBy++/4ULgAPZAL+/fLBIh9j99nyHXdbal3tS/QfjVe05tPr64DMwAGwAAYAANgAAyAATAABsAAGAADYAAMgAEwAAbAABgAtXkGwAAYAANgAAyACIBQ5gW1+dTSZjZND7hFHiA9gMeJAfjV+gBuYslDJFerqM2exABcicrUNtzo9hID0XT/UyxVPotU4bm4uJZ9/QBAGdZlswvPSwAAAABJRU5ErkJggg==";
	private static final String IMG_TOASTS = "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAABS0lEQVR4nO3aP07DMBTH8XADunEMFpoOHXqUdwS4BYegdRY4DgdhqLMxhkRiQu6LnT/Ffv3+pLdFTn4fRbZapaoIIYRMy5P7etg5fyhhHp2/XxygX1hq57sSZkAAAAAAALgKwOI3SgwAAAAAQLYAw4MsuLMLAAAAAEBRAL//IYg225N/qV37bRJgLPv3dtMDfJp9A7SklDcHkFreFMBo+cafzALElK+67s4kQGx5pZSE1i0CIKW8UkpCa2cPkFpeKSWh9bMGmFJeKSWhe2QLMLW8UkoSrj1EF7v4gGuOUt4+wEh52wAR5Yf0BZq/Ux/P+7IBmvYjpnxqigG49ApnCbB159ex3/HaFA8w9xwFAAAAAAis+TZnY03ccPMDuOYAAAAAAGQHsNYxODsAAAAAAIF53s34Xt8CwBpHk/x312AAAACA2wYghJBbyQ+jmezIsyUGbQAAAABJRU5ErkJggg==";
	private static final String IMG_COLORS = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAMAAAAoLQ9TAAADAFBMVEV4gICQkHD42IjgwCjwcGjYQEiAuOjIaBgAeLjomKAAkNBoeIh4uJj4+Ig4eDjI0PDo4MgwmGi4uLhoeJCIiHhocJCYkHCAiHhweIigmGj///8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC+up50AAAAG3RSTlP//////////////////////////////////wAnNAs1AAAAeUlEQVR4nG3PSRKEIBBE0UIBmZSptMD7H7S7CBdE23+Ri7dLuH+CP9DnBux7X5a+rn3bXnAxXHMMIiVhrZBSKCUYMCW0FqVEpZCBiBCJ++6AGElrco6MGQAxgtbgHBgDDG2OwZfSQmg5t+PwDLWUGkLN2T9wzr3ffgCjHBOh3miaeAAAAABJRU5ErkJggg==";
	private static final String IMG_DEMO = "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";
	
	private int index = 0;
	private Toast.ToastBuilder toastBuilder = Toast.builder();
	private Toast defaultToast = toastBuilder.build();

	/**
	 * The main method showing the toaster settings demo dialog.
	 * 
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// warm up
		UUID.randomUUID();

		Display display = new Display();
		ToasterDemo shell = new ToasterDemo(display);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	/**
	 * Constructor.
	 *
	 * @param display the display
	 */
	public ToasterDemo(Display display) {
		super(display);
		setImage(ToasterUtils.fromBase64(IMG_TOASTER));
		createContent();
	}
	
	@Override
	protected void checkSubclass() {
		// allow subclassing
	}
	
	private void createContent() {
		int width = 1024;
		int height = 800;
		
		setText("Toaster");
		setSize(width, height);
		setLayout(new GridLayout());

		Toaster.setDefaultToolkit(new ToastToolkitImpl(this));
		
		Monitor primaryMonitor = getDisplay().getPrimaryMonitor();
		Rectangle clientArea = primaryMonitor.getClientArea();
		setLocation((clientArea.width-width)/2, (clientArea.height-height)/2);
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout rootLayout = new GridLayout(3, true);
		rootLayout.horizontalSpacing = 15;
		rootLayout.verticalSpacing = 15;
		composite.setLayout(rootLayout);
		
		ToolBar toolBar = new ToolBar(composite, SWT.FLAT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 5, 1));
		fillToolbar(toolBar);

		fillCustomMessageGroup(createGroup(composite, "Content"));

		fillPopupPositionGroup(createGroup(composite, "Popup Position"));
		fillPopupSizeGroup(createGroup(composite, "Popup Size"));
		fillIconSizeGroup(createGroup(composite, "Icon Size"));

		fillStyleGroup(createGroup(composite, "Style"));
		fillAnimactionGroup(createGroup(composite, "Animation"));
		fillOptionsGroup(createGroup(composite, "Options"));
		
		initShortcuts();
	}

	private void fillOptionsGroup(Group parent) {
		createCheckbox(parent, "Sticky", defaultToast.isSticky(), toastBuilder::sticky);
		createCheckbox(parent, "Allow icon upscaling", defaultToast.isAllowIconUpscaling(), toastBuilder::allowIconUpscaling);
		createCheckbox(parent, "Include pseudo actions", false, c -> {
			toastBuilder.clearActions();
			if (c != null && c.booleanValue()) {
				ToastAction ac1 = ToastAction.builder()
						.text("Action 1")
						.executable(toast -> getDisplay().syncExec(() -> MessageDialog.openInformation(getShell(), "Toaster", "Called: Action 1")))
						.build();

				ToastAction ac2 = ToastAction.builder()
						.text("Action 2")
						.executable(toast -> getDisplay().syncExec(() -> MessageDialog.openInformation(getShell(), "Toaster", "Called: Action 2")))
						.build();
				
				toastBuilder.action(ac1).action(ac2);
			}
		});
	}

	private void fillAnimactionGroup(Group parent) {
		parent.setLayout(new GridLayout(2, false));
		
		createLabel(parent, "Display Time:");
		createIntegerText(parent, defaultToast.getDisplayTime(), toastBuilder::displayTime);
		
		createLabel(parent, "FadeIn Time:");
		createIntegerText(parent, defaultToast.getFadeInTime(), toastBuilder::fadeInTime);
		
		createLabel(parent, "FadeIn Steps:");
		createIntegerText(parent, defaultToast.getFadeInSteps(), toastBuilder::fadeInSteps);
		
		createLabel(parent, "FadeOut Time:");
		createIntegerText(parent, defaultToast.getFadeOutTime(), toastBuilder::fadeOutTime);
		
		createLabel(parent, "FadeOut Steps:");
		createIntegerText(parent, defaultToast.getFadeOutSteps(), toastBuilder::fadeOutSteps);
	}

	private void fillStyleGroup(Group parent) {
		parent.setLayout(new GridLayout(2, false));
		
		createLabel(parent, "Transparency:");
		createIntegerText(parent, defaultToast.getTransparency(), toastBuilder::transparency);
		
		createLabel(parent, "Background color:");
		createRGBText(parent, defaultToast.getBackgroundColor(), toastBuilder::backgroundColor);

		createLabel(parent, "Border color:");
		createRGBText(parent, defaultToast.getBorderColor(), toastBuilder::borderColor);
		
		createLabel(parent, "Icon background color:");
		createRGBText(parent, defaultToast.getIconBackgroundColor(), toastBuilder::iconBackgroundColor);

		createLabel(parent, "Title foreground color:");
		createRGBText(parent, defaultToast.getTitleForegroundColor(), toastBuilder::titleForegroundColor);
		
		createLabel(parent, "Message foreground color:");
		createRGBText(parent, defaultToast.getMessageForegroundColor(), toastBuilder::messageForegroundColor);
		
		createLabel(parent, "Details foreground color:");
		createRGBText(parent, defaultToast.getDetailsForegroundColor(), toastBuilder::detailsForegroundColor);
		
		createLabel(parent, "Actions background color:");
		createRGBText(parent, defaultToast.getActionsBackgroundColor(), toastBuilder::actionsBackgroundColor);
		
		createLabel(parent, "Actions foreground color:");
		createRGBText(parent, defaultToast.getActionsForegroundColor(), toastBuilder::actionsForegroundColor);
	}

	private void fillPopupSizeGroup(Group parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parent.setLayout(new GridLayout(4, false));

		// min size
		createLabel(parent, "Min size:");
		createIntegerText(parent, defaultToast.getMinWidth(), toastBuilder::minWidth);
		createLabel(parent, "/");
		createIntegerText(parent, defaultToast.getMinHeight(), toastBuilder::minHeight);
		
		// max size
		createLabel(parent, "Max size:");
		createIntegerText(parent, defaultToast.getMaxWidth(), toastBuilder::maxWidth);
		createLabel(parent, "/");
		createIntegerText(parent, defaultToast.getMaxHeight(), toastBuilder::maxHeight);
	}

	private void fillIconSizeGroup(Group parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parent.setLayout(new GridLayout(4, false));

		// min size
		createLabel(parent, "Min size:");
		createIntegerText(parent, defaultToast.getMinIconWidth(), toastBuilder::minIconWidth);
		createLabel(parent, "/");
		createIntegerText(parent, defaultToast.getMinIconHeight(), toastBuilder::minIconHeight);
		
		// max size
		createLabel(parent, "Max size:");
		createIntegerText(parent, defaultToast.getMaxIconWidth(), toastBuilder::maxIconWidth);
		createLabel(parent, "/");
		createIntegerText(parent, defaultToast.getMaxIconHeight(), toastBuilder::maxIconHeight);
	}

	private void initShortcuts() {
		getDisplay().addFilter(SWT.KeyDown, e -> {
		    if( e.character == 't' ) {
		        toast(null);
		    }
		});		
	}

	private void fillToolbar(ToolBar toolBar) {
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText("Shows a toast with the given settings");
		toolItem.setImage(ToasterUtils.fromBase64(IMG_TOASTER));
		toolItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::toast));
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText("Shows multiple toasts with different icon sizes");
		toolItem.setImage(ToasterUtils.fromBase64(IMG_TOASTS));
		toolItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::showTestMessages));
		
		toolItem = new ToolItem(toolBar, SWT.SEPARATOR);
		
		toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setToolTipText("Shows a toast with customized action colors");
		toolItem.setImage(ToasterUtils.fromBase64(IMG_DEMO));
		toolItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(this::showDemoMessage));
	}

	private void fillPopupPositionGroup(Group parent) {
		parent.setLayout(new GridLayout(2, false));
		
		createRadio(parent, "Top left", () -> toastBuilder.position(ToastPosition.TOP_LEFT)).setSelection(ToastPosition.TOP_LEFT.equals(defaultToast.getPosition()));
		createRadio(parent, "Top right", () -> toastBuilder.position(ToastPosition.TOP_RIGHT)).setSelection(ToastPosition.TOP_RIGHT.equals(defaultToast.getPosition()));
		createRadio(parent, "Bottom left", () -> toastBuilder.position(ToastPosition.BOTTOM_LEFT)).setSelection(ToastPosition.BOTTOM_LEFT.equals(defaultToast.getPosition()));
		createRadio(parent, "Bottom right", () -> toastBuilder.position(ToastPosition.BOTTOM_RIGHT)).setSelection(ToastPosition.BOTTOM_RIGHT.equals(defaultToast.getPosition()));
	}
	
	private void fillCustomMessageGroup(Group parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		parent.setLayout(new GridLayout(4, true));
		
		createLabel(parent, "Title");
		createLabel(parent, "Message");
		createLabel(parent, "Details");
		createLabel(parent, "Image");
		createText(parent, "Lorem ipsum title", toastBuilder::title);
		createText(parent, "Lorem ipsum message. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero ", toastBuilder::message);
		createText(parent, "Lorem ipsum details", toastBuilder::details);
		createText(parent, IMG_TOASTER, toastBuilder::icon);
	}

	private static Button createRadio(Composite parent, String text, Runnable runnable) {
		Button button= new Button(parent, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText(text);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runnable.run();
			}
		});
		return button;
	}
	
	private static Text createText(Composite parent, String initialText, Consumer<String> consumer) {
		Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setText(initialText);
		if (consumer != null) {
			text.addModifyListener(e -> consumer.accept(text.getText()));
			consumer.accept(text.getText());
		}
		return text;
	}

	private static Text createIntegerText(Composite parent, int initialValue, IntConsumer consumer) {
		Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		layoutData.widthHint = 50;
		text.setLayoutData(layoutData);
		text.setText(Integer.toString(initialValue));
		if (consumer != null) {
			text.addModifyListener(e -> consumer.accept(toInteger(text.getText(), initialValue)));
			consumer.accept(toInteger(text.getText(), initialValue));
		}
		return text;
	}
	
	private static Button createCheckbox(Composite parent, String text, boolean initialValue, Consumer<Boolean> consumer) {
		Button button = new Button(parent, SWT.CHECK);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText(text);
		button.setSelection(initialValue);
		if (consumer != null) {
			button.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> consumer.accept(button.getSelection())));
		}
		return button;
	}
	
	private Text createRGBText(Composite parent, ToastColor initialValue, Consumer<ToastColor> consumer) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		container.setLayout(new GridLayout(2, false));
		
		Text text = new Text(container, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		layoutData.widthHint = 100;
		text.setLayoutData(layoutData);
		if (initialValue != null) {
			text.setText(initialValue.toHEX());
		}
		if (consumer != null) {
			text.addModifyListener(e -> {
				ToastColor rgb = toRGB(text.getText(), initialValue);
				consumer.accept(rgb);
				container.setBackground(new Color(getDisplay(), rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
			});
			ToastColor rgb = toRGB(text.getText(), initialValue);
			if (rgb != null) {
				container.setBackground(new Color(getDisplay(), rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
				consumer.accept(rgb);
			}
			
			ToolBar toolBar = new ToolBar(container, SWT.FLAT);
			toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
			ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
			toolItem.setImage(ToasterUtils.fromBase64(IMG_COLORS));
			toolItem.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> {
				ColorDialog dialog = new ColorDialog(getShell());
				RGB value = dialog.open();
				if (value != null) {
					text.setText(toString(value));
				}
			}));
		}
		
		return text;
	}

	private static ToastColor toRGB(String text, ToastColor defaultValue) {
		try {
			java.awt.Color c = java.awt.Color.decode(text);
			return new ToastColor(c.getRed(), c.getGreen(), c.getBlue());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	private static String toString(RGB color) {
		if (color != null) {
			return ("#" + String.format("%02X", color.red) + String.format("%02X", color.green) + String.format("%02X", color.blue)).toUpperCase(); 
		}
		return "";
	}
	
	private static int toInteger(String text, int defaultValue) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	private static Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label.setText(text);
		return label;
	}

	private static Group createGroup(Composite composite, String text) {
		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(new GridLayout(1, true));
		group.setText(text);
		return group;
	}
	
	private void toast(SelectionEvent event) {
		Toast toast = toastBuilder.build().toast();
		getDisplay().timerExec(5000, () -> {
			toast.updateTitle(toast.getTitle() + " [update]");
			toast.updateMessage(toast.getMessage() + " [update]");
			toast.updateDetails(toast.getDetails() + " [update]");
			toast.updateIcon(IMG_DEMO);
		});
	}
	
	private void showTestMessages(SelectionEvent event) {
		String[] dimensions = new String[] {
				"10x20",
				"20x10",
				"20x20",
				"100x200",
				"200x100",
				"200x200",
		};
		ToastBuilder copy = toastBuilder.build().copy(); // do not override the title, message and icon of the default builder
		for (String dimension : dimensions) {
			String[] parts = dimension.split("x");
			int width = Integer.parseInt(parts[0]);
			int height = Integer.parseInt(parts[1]);
			Image icon = new Image(null, width, height);
			GC gc = new GC(icon);
			gc.setBackground(new Color(0, 149, 255));
			gc.fillRectangle(0, 0, width, height);
			gc.dispose();
			copy.title((++index) + ". " + dimension).message("Image with " + dimension).icon(icon).build().toast();
		}
	}

	private void showDemoMessage(SelectionEvent event) {
		ToastAction ac1 = ToastAction.builder()
				.text("Accept all")
				.backgroundColor(new ToastColor(0, 149, 255))
				.foregroundColor(new ToastColor(255, 255, 255))
				.backgroundColorHovered(new ToastColor(0, 119, 204))
				.executable(toast -> getDisplay().syncExec(() -> MessageDialog.openInformation(getShell(), "Toaster", "Called: Accept all")))
				.build();

		ToastAction ac2 = ToastAction.builder()
				.text("Customize settings")
				.backgroundColor(new ToastColor(225, 236, 244))
				.foregroundColor(new ToastColor(57, 115, 157))
				.backgroundColorHovered(new ToastColor(179, 211, 234))
				.foregroundColorHovered(new ToastColor(44, 87, 119))
				.executable(toast -> getDisplay().syncExec(() -> MessageDialog.openInformation(getShell(), "Toaster", "Called: Customize settings")))
				.build();
		
		toastBuilder.build().copy()
			.clearActions() // clear standard actions if available
			.action(ac1)
			.action(ac2)
			.icon(IMG_DEMO)
			.sticky(true)
			.build()
			.toast();
	}
	
}
