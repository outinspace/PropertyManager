package com.wilsongateway.CustomComponents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.User;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class EndlessComboBox<T> extends CustomComponent{
	
	private VerticalLayout content;
	private VerticalLayout boxLayout;
	
	private CopyOnWriteArrayList<ComboBox> boxes;
	private List<T> options;
	private List<T> values;
	
	private boolean isEnabled = true;
	
	public EndlessComboBox(List<T> options, List<T> values){
		this.options = options;
		this.values = values;
		
		boxes = new CopyOnWriteArrayList<ComboBox>();
		
		content = new VerticalLayout();
		content.setSpacing(true);
		
		boxLayout = new VerticalLayout();
		boxLayout.setSpacing(true);
		content.addComponent(boxLayout);
		
		//Pre-load values
		if(this.values == null || this.values.size() == 0){
			addComboBox(null);	
		}else{
			for(T value : values){
				addComboBox(value);
			}
		}
		
		
		Button plus = new Button();
		plus.setIcon(FontAwesome.PLUS);
		plus.setStyleName("tiny");
		content.addComponent(plus);
		
		plus.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				if(isEnabled){
					addComboBox(null);
				}
			}
			
		});
		
		this.setCompositionRoot(content);
	}

	private void addComboBox(T value) {
		HorizontalLayout rowLayout = new HorizontalLayout();
		rowLayout.setSpacing(true);
		boxLayout.addComponent(rowLayout);
		
		ComboBox box = new ComboBox();
		box.addItems(options);
		
		//TODO Clean
		for(T option : options){
			if(value != null && value.equals(option)){
				value = option;
			}
		}
		
		box.setValue(value);
		boxes.add(box);
		rowLayout.addComponent(box);
		
		box.setCaption("Selection " + boxes.size());
		
		Button minus = new Button();
		minus.setIcon(FontAwesome.MINUS);
		rowLayout.addComponent(minus);
		rowLayout.setComponentAlignment(minus, Alignment.BOTTOM_RIGHT);
		
		minus.addClickListener(new ClickListener(){

			@Override
			public void buttonClick(ClickEvent event) {
				if(isEnabled){
					UI.getCurrent().access(new Runnable(){

						@Override
						public void run() {
							boxes.remove(box);
							boxLayout.removeComponent(rowLayout);
							for(int i = 0; i < boxes.size(); i++){
								boxes.get(i).setCaption("Selection " + (i+1));
							}
						}
						
					});
				}
			}
			
		});
	}
	
	public void setOptions(List<T> lazyList){
		for(ComboBox cb : boxes){
			cb.removeAllItems();
			cb.addItems(lazyList);
		}
		this.options = lazyList;
	}
	
	public ArrayList<T> getValues(){
		ArrayList<T> output = new ArrayList<T>();
		for(ComboBox cb : boxes){
			if(cb.getValue() != null){
				output.add((T) cb.getValue());
			}
		}
		
		return output;
	}

	public void clear() {
		UI.getCurrent().access(new Runnable(){

			@Override
			public void run() {
				for(ComboBox cb : boxes){
					boxes.remove(cb);
				}
				boxLayout.removeAllComponents();
				addComboBox(null);
			}
			
		});
	}
	
	public <K extends EncryptedModel> void setManyToMany(K item, Model staticRef) {
		//Remove relationships
		for(Model instance : item.getAll(staticRef.getClass())){
			if(!this.getValues().contains(instance)){
				item.remove(instance);
			}
		}
		
		//Add new Relationships
		for(T instance : this.getValues()){
			if(instance != null && instance instanceof Model){
				if(!item.getAll(staticRef.getClass()).contains(instance)){
					item.add((Model)instance);
				}
			}
		}
	}
	
	public void setEnabled(boolean value){
		for(ComboBox cb: boxes){
			cb.setEnabled(value);
		}
		isEnabled = value;
	}
}
