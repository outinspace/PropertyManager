package com.wilsongateway.Framework;

import java.util.Collection;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.StrongTextEncryptor;
import org.javalite.activejdbc.CallbackAdapter;
import org.javalite.activejdbc.ColumnMetadata;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.wilsongateway.Exceptions.CannotEncryptNonStringException;

public abstract class EncryptedModel extends Model{

	private static final String GLOBALKEY = System.getProperty("global_key") == null ? "devKey" : System.getProperty("global_key");
	private String[] encryptedAttributes;
	
	public EncryptedModel(String ... attributes){
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
	
	public Collection<ColumnMetadata> getColumns(){
		return this.getMetaModel().getColumnMetadata().values();
	}
	
//	@Override
//	public LazyList<EncryptedModel> findAll(){
//		return super.findAll();
//		
//	}
	
	@Override
	public boolean equals(Object obj){
		SessionManager.openBase();
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
