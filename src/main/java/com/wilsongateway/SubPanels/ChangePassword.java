package com.wilsongateway.SubPanels;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables.User;
import com.wilsongateway.Validators.PasswordValidator;

public class ChangePassword extends Window{

	private static final long serialVersionUID = -168889524760672340L;

	private User user;
	
	private VerticalLayout content;
	private PasswordField oldPass;
	private PasswordField newPass;
	private Label error;
	
	public ChangePassword(User user){
		super("Change Password");
		this.user = user;
		
		content = new VerticalLayout();
		content.setSpacing(true);
		content.setMargin(true);
		setContent(content);
		
		initGui();
		this.center();
		this.setResizable(false);
	}

	private void initGui() {
		oldPass = new PasswordField("Old Password");
		oldPass.setRequired(true);
		oldPass.addValidator(new Validator(){

			private static final long serialVersionUID = 2427906456930342997L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				try{
					SessionManager.openBase();
					
					if(value == null || !user.checkPassword((String)value)){
						throw new InvalidValueException("Incorrect Password");
					}
				}finally{
					SessionManager.closeBase();
				}
			}
			
		});
		content.addComponent(oldPass);
		
		newPass = new PasswordField("New Password");
		newPass.setRequired(true);
		newPass.addValidator(new PasswordValidator());
		newPass.addValidator(new Validator(){

			private static final long serialVersionUID = 4188385725188971113L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if(user.getAsString("username").equals("demo")){
					throw new InvalidValueException("Cannot Change Password On Demo Account");
				}
			}
			
		});
		content.addComponent(newPass);
		
		error = new Label();
		error.setVisible(false);
		content.addComponent(error);
		
		Button submit = new Button("Change", e -> executeChange());
		content.addComponent(submit);
	}

	private void executeChange() {
		if(validateFields()){
			user.setEncrypted("password", newPass.getValue());
			this.close();
			Notification.show("Password Changed", Notification.Type.TRAY_NOTIFICATION);
		}
	}

	private boolean validateFields() {
		try{
			oldPass.validate();
			newPass.validate();
			return true;
		}catch(InvalidValueException e){
			error.setValue(e.getMessage());
			error.setVisible(true);
			return false;
		}
	}
}