package com.wilsongateway.Framework;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.javalite.activejdbc.CallbackAdapter;
import org.javalite.activejdbc.Model;

import com.wilsongateway.Exceptions.CannotEncryptNonStringException;

public abstract class EncryptedModel extends Model{

	private static final String GLOBALKEY = System.getProperty("global_key") == null ? "devKey" : System.getProperty("global_key");
	//private BasicTextEncryptor encryptor;
	private String[] encryptedAttributes;
	
	public EncryptedModel(String ... attributes){
//		encryptor = new BasicTextEncryptor();
//		encryptor.setPassword(GLOBALKEY);
		this.encryptedAttributes = attributes;
	}
	
	public Object getDecrypted(String attributeName){
		Object obj = super.get(attributeName);
		if(isEncryptedAttribute(attributeName) && obj instanceof String){
			String s = (String)obj;
			if(s != null && !s.equals("")){
				try{
				BasicTextEncryptor encryptor = new BasicTextEncryptor();
				encryptor.setPassword(GLOBALKEY);
				s = encryptor.decrypt(s);
				}catch(EncryptionOperationNotPossibleException e){
					System.out.println("Could not decrypt text: " + s);
				}
			}
			return s;
		}
		return obj;
	}
	
	public String getAsString(String attributeName){
		Object obj = getDecrypted(attributeName);
		if(obj instanceof String){
			return (String)obj;
		}
		return null;
	}
	
//	@Override
//	public String getString(String attributeName){
//		String s = super.getString(attributeName);
//		if(s == null || s.equals("")){
//			return s;
//		}else if(isEncryptedAttribute(attributeName)){//TODO check for inconsistencies, maybe set salt to 0.
//			BasicTextEncryptor encryptor = new BasicTextEncryptor();
//			encryptor.setPassword(GLOBALKEY);
//			return encryptor.decrypt(s);
//		}
//		return s;
//	}
	
	public <T extends Model> T setEncrypted(String attributeName, Object value){
		System.out.println("Setting attribute: " + attributeName);
		if(isEncryptedAttribute(attributeName) && value instanceof String){
			BasicTextEncryptor encryptor = new BasicTextEncryptor();
			encryptor.setPassword(GLOBALKEY);
			String encrypted = encryptor.encrypt((String)value);
			System.out.println("Using encryption: " + encrypted + "\n");
			return super.set(attributeName, encrypted);
		}else{
			System.out.println("Using plaintext: " + value + "\n");
			return super.set(attributeName, value);
		}
	}
	
	public boolean isEncryptedAttribute(String attribute){
		for(String entry : encryptedAttributes){
			if(attribute.equals(entry)){
				return true;
			}
		}
		return false;
	}
	
//	public <T extends Model> T setEncrypted(String column, String value){
//		return super.set(column, encryptor.encrypt(value));
//	}
//	
//	public String getDecrypted(String column){
//		String encrypted = super.getString(column);
//		return encryptor.decrypt(encrypted);
//	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof EncryptedModel){
			if(this.getTableName().equals(((EncryptedModel) obj).getTableName()) 
					&& this.getId() == ((EncryptedModel)obj).getId()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public abstract String toString();
}
