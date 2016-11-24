package com.wilsongateway.Validators;

import com.vaadin.data.Validator;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.User;

public class UniqueUsernameValidator implements Validator{

	private static final long serialVersionUID = -249293550935225514L;

	private String currentUsername;
	
	public UniqueUsernameValidator(String currentUsername){
		this.currentUsername = currentUsername;
	}
	
	public UniqueUsernameValidator(){}
	
	@Override
	public void validate(Object value) throws InvalidValueException {
		String username = value.toString();
		
		if(currentUsername != null && username.equals(currentUsername)){
			return;
		}
		
		if(username.contains(" ")){
			throw new InvalidValueException("Cannot contains spaces");
		}else{
			SessionManager.openBase();
			User u = Tables.USER.findFirst("username = (?)", username);
			SessionManager.closeBase();
			
			if(u != null){
				throw new InvalidValueException("Username taken");
			}
			
		}
	}

}
