package com.ganesha.desktop.component;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JFormattedTextField;

public class XJTextField extends JFormattedTextField implements
		XComponentConstants {
	private static final long serialVersionUID = 8731044804764016513L;
	private boolean upperCaseOnFocusLost;

	public XJTextField() {
		setFont(new Font("Tahoma", Font.PLAIN, FONT_SIZE_NORMAL));
		setUpperCaseOnFocusLost(true);

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (isUpperCaseOnFocusLost()) {
					String text = getText();
					String upperCaseText = text.toUpperCase();
					setText(upperCaseText);
				}
			}
		});
	}

	public boolean isUpperCaseOnFocusLost() {
		return upperCaseOnFocusLost;
	}

	public void setUpperCaseOnFocusLost(boolean upperCaseOnFocusLost) {
		this.upperCaseOnFocusLost = upperCaseOnFocusLost;
	}
}
