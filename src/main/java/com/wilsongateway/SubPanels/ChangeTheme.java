package com.wilsongateway.SubPanels;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.wilsongateway.Framework.SessionManager;

public class ChangeTheme extends Window{
	
	private static final long serialVersionUID = 4344580000358216014L;

	public ChangeTheme(SessionManager manager){
		VerticalLayout content = new VerticalLayout();
		content.setSpacing(true);
		content.setMargin(true);
		setContent(content);
		center();
		
		OptionGroup themeSelect = new OptionGroup("Select Theme");
		themeSelect.addItem("Light");
		themeSelect.addItem("Runo");
		themeSelect.addItem("Reindeer");
		themeSelect.select("Light");
		content.addComponent(themeSelect);
		
		themeSelect.addValueChangeListener(new ValueChangeListener(){

			private static final long serialVersionUID = 2670243335407825053L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				switch(themeSelect.getValue().toString()){
				case "Light":
					UI.getCurrent().setTheme("pmTheme");
					break;
				case "Runo":
					UI.getCurrent().setTheme("runo");
					break;
				case "Reindeer":
					UI.getCurrent().setTheme("reindeer");
					break;
				}
			}
			
		});
	}
}
