package com.wilsongateway.Validators;

import com.vaadin.data.Validator;
import com.wilsongateway.Framework.EncryptedModel;

@SuppressWarnings("serial")//TODO add serial to all classes
public class PasswordValidator implements Validator{

	@Override
	public void validate(Object value) throws InvalidValueException {
		String password = value.toString();
		
		if(password.equals(EncryptedModel.PASSWORDFILLER)){
			return;
		}
		
		if(password.length() < 8 || password.length() >= 45){
			throw new InvalidValueException("Invalid Length");
		}
		
		boolean uppercase = false;
		boolean lowercase = false;
		boolean numeric = false;
		
		for(char c : password.toCharArray()){
			if(Character.isUpperCase(c)){
				uppercase = true;
			}
			if(Character.isLowerCase(c)){
				lowercase = true;
			}
			if(Character.isDigit(c)){
				numeric = true;
			}
		}
		
		if(!uppercase){
			throw new InvalidValueException("Missing Uppercase Character");
		}else if(!lowercase){
			throw new InvalidValueException("Missing Lowercase Character");
		}else if(!numeric){
			throw new InvalidValueException("Missing Numeric Character");
		}
	}

}
