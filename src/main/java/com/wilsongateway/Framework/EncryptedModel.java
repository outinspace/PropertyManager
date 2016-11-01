package com.wilsongateway.Framework;

import org.jasypt.util.text.StrongTextEncryptor;
import org.javalite.activejdbc.CallbackAdapter;
import org.javalite.activejdbc.Model;

import com.wilsongateway.Exceptions.CannotEncryptNonStringException;

public abstract class EncryptedModel extends Model{

	private static final String GLOBALKEY = System.getProperty("global_key") == null ? "devKey" : System.getProperty("global_key");
	private StrongTextEncryptor encryptor;
	private String[] encryptedAttributes;
	
	public EncryptedModel(String ... attributes){
		encryptor = new StrongTextEncryptor();
		encryptor.setPassword(GLOBALKEY);
		this.encryptedAttributes = attributes;
	}
	
	//Encrypt all attributes specified in encryptedAttributes
//	@Override
//	public void beforeSave(){
//		for(String attribute : encryptedAttributes){
//			this.set(attribute, encryptor.encrypt(this.getString(attribute)));
//		}
//		System.out.println("Before save called!!!");
//	}
	
	@Override
	public Object get(String attributeName){
		Object obj = super.get(attributeName);
		if(isEncryptedAttribute(attributeName) && obj instanceof String){
			return encryptor.decrypt((String)obj);
		}
		return obj;
	}
	
	@Override
	public String getString(String attributeName){
		String s = super.getString(attributeName);
		if(isEncryptedAttribute(attributeName)){
			return encryptor.decrypt(s);
		}
		return s;
	}
	
	@Override
	public <T extends Model> T set(String attributeName, Object value){
		if(isEncryptedAttribute(attributeName) && value instanceof String){
			String encrypted = encryptor.encrypt((String)value);
			return super.set(attributeName, encrypted);
		}else{
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
