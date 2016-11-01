package com.wilsongateway.Framework;

import java.util.ArrayList;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.wilsongateway.Exceptions.InvalidPasswordException;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Framework.Tab.TabType;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;

public class Tables {

	public static void isUniqueName(String columnName, Model item, String comparing) throws NameUnavailableException{
		for(Model m : item.findAll()){
			if(m.getString(columnName) != null && m.getString(columnName).equals(comparing)){
				throw new NameUnavailableException();
			}
		}
	}
	
	
	/**
	 * User Model
	 * Columns: [id, username, password, first_name, last_name, position, apt_phone, cell_phone, created_at]
	 * Encrypted: [password, first_name, last_name, position, apt_phone, cell_phone]
	 *
	 */
	@Table("users")
	public static class User extends EncryptedModel {
		
		public User(){
			super();
		}
		
		public User find(String username){return USER.findFirst("username = (?)", username);}
		
		public String getUsername(){
			return this.getString("username");
		}
		public String getPassword(){
			return this.getString("password");
		}
		
		public void addGroup(Group newGroup){
			if(!this.getAll(Group.class).contains(newGroup)){
				this.add(newGroup);
			}
		}
		
		public static boolean isAcceptablePassword(String password){
			if(password.length() < 8 || password.length() >= 45){
				return false;
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
			
			return uppercase && lowercase && numeric;
		}
		
		public void checkAndSetUsername(String username) throws NameUnavailableException{
			if(username.contains(" ") || username.equals("")){
				throw new NameUnavailableException();
			}else{
				User u = Tables.USER.findFirst("username = (?)", username);
				if(u == null){
					this.set("username", username);
				}else{
					throw new NameUnavailableException();
				}
			}
		}
		
		public void checkAndSetPassword(String password) throws InvalidPasswordException{
			if(User.isAcceptablePassword(password)){
				this.set("password", password);
			}else{
				throw new InvalidPasswordException();
			}
		}

		@Override
		public String toString() {
			return this.getString("first_name") + " " + this.getString("last_name");
		}
	}
	public static final User USER = new User();
	
	
	/**
	 * Client Model
	 * Columns: [id, first_name, last_name, unit, property_id, created_at]
	 * Encrypted: [first_name, last_name]
	 * 
	 */
	@Table("clients")
	public static class Client extends Model {

		@Override
		public String toString() {
			return this.getString("first_name") + " " + this.getString("last_name");
		}
	}
	public static final Client CLIENT = new Client();
	
	
	/**
	 * Property Model
	 * Columns: [id, name, street_address, city, state, zip]
	 *
	 */
	@Table("properties")
	public static class Property extends Model {
		
		@Override
		public boolean equals(Object obj){
			if(obj instanceof Property){
				if(this.getId() == ((Property)obj).getId()){
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString(){
			return this.getString("name");
		}
		
	}
	public static final Property PROPERTY = new Property();
	
	
	/**
	 * Properties to Users Join Model
	 * Columns: [id, property_id, user_id]
	 *
	 */
	@Table("properties_users")
	public static class PropertiesUsers extends Model {}
	public static final PropertiesUsers PROPERTIESUSERS = new PropertiesUsers();
	
	
	/**
	 * Group Model
	 * Columns: [id, name, tabs]
	 *
	 */
	@Table("groups")
	public static class Group extends Model {
		
		public ArrayList<TabType> getTabs() {
			ArrayList<TabType> output = new ArrayList<TabType>();
			String query = this.getString("tabs");
			
			query = query.replace("[", "");
			query = query.replace("]", "");
			query += ",";
			
			String[] arr = query.split(",");
			for(String s : arr){
				try{
					TabType t = TabType.valueOf(s.replace(",", "").trim());
					output.add(t);
				}catch(IllegalArgumentException e){
					System.err.println(e.getMessage());
				}
				
			}
			return output;
		}
		
		public void setName(String name){
			this.set("name", name.replace(",", ""));
		}
		
		public void setTabs(ArrayList<TabType> tabs) {
			this.set("tabs", tabs.toString());
		}
		
		public void addTab(TabType tab) {
			ArrayList<TabType> tabs = this.getTabs();
			if(!tabs.contains(tab)){
				tabs.add(tab);
				this.set("tabs", tabs.toString());
			}
		}
		
		public void removeTab(TabType tab) {
			ArrayList<TabType> tabs = this.getTabs();
			tabs.remove(tab);
			this.setTabs(tabs);
		}
		
		@Override
		public boolean equals(Object obj){
			if(obj instanceof Group){
				if(this.getId() == ((Group)obj).getId()){
					return true;
				}
			}
			return false;
		}
		
		@Override
		public String toString(){
			return this.getString("name");
		}
	}
	public static final Group GROUP = new Group();
	
	
	/**
	 * Groups to Users Join Model
	 * Columns: [id, group_id, user_id]
	 *
	 */
	@Table("groups_users")
	public static class GroupsUsers extends Model {}
	public static final GroupsUsers GROUPSUSERS = new GroupsUsers();
}

