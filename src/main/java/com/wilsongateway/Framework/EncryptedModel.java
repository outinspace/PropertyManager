package com.wilsongateway.Framework;

import org.javalite.activejdbc.Model;

public abstract class EncryptedModel extends Model{

	//TODO
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
