package com.wilsongateway.Framework;

import java.util.ArrayList;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.wilsongateway.Exceptions.InvalidPasswordException;
import com.wilsongateway.Exceptions.NameUnavailableException;
import com.wilsongateway.Framework.Tab.TabType;
import com.wilsongateway.Framework.Tables.Group;
import com.wilsongateway.Framework.Tables.User;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
public class Tables {

	public static void isUniqueName(String columnName, EncryptedModel item, String comparing) throws NameUnavailableException{
		for(Model m : item.findAll()){
			EncryptedModel em = (EncryptedModel)m;
			if(em.getAsString(columnName) != null && em.getAsString(columnName).equals(comparing)){
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
			super("password", "first_name", "last_name", "position", "work_phone", "cell_phone");
		}
		
		public User find(String username){return USER.findFirst("username = (?)", username);}
		
		public String getUsername(){
			return this.getAsString("username");
		}
		public String getPassword(){
			return this.getAsString("password");
		}
		
		public void addGroup(Group newGroup){
			if(!this.getAll(Group.class).contains(newGroup)){
				this.add(newGroup);
			}
		}
		
		@Deprecated
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
					this.setEncrypted("username", username);
				}else{
					throw new NameUnavailableException();
				}
			}
		}
		
		public void checkAndSetPassword(String password) throws InvalidPasswordException{
			if(User.isAcceptablePassword(password)){
				System.out.println("calling set password");
				this.setEncrypted("password", password);
				this.save();
			}else{
				throw new InvalidPasswordException();
			}
		}
		
		public boolean checkPassword(String testPassword){
			BasicPasswordEncryptor encryptor = new BasicPasswordEncryptor();
			return encryptor.checkPassword(testPassword, (String) this.get(PASSWORDATTRIBUTE));
		}

		@Override
		public String toString() {
			return this.getAsString("first_name") + " " + this.getAsString("last_name");
		}
	}
	public static final User USER = new User();
	
	
	/**
	 * Client Model
	 * Columns: [id, first_name, last_name, unit, property_id, created_at]
	 * Encrypted: []TODO
	 * 
	 */
	@Table("clients")
	public static class Client extends EncryptedModel {

		@Override
		public String toString() {
			return this.getAsString("first_name") + " " + this.getAsString("last_name");
		}
	}
	public static final Client CLIENT = new Client();
	
	
	/**
	 * Property Model
	 * Columns: [id, name, street_address, city, state, zip]
	 *
	 */
	@Table("properties")
	public static class Property extends EncryptedModel {
		
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
			return this.getAsString("name");
		}
		
	}
	public static final Property PROPERTY = new Property();
	
	
	/**
	 * Properties to Users Join Model
	 * Columns: [id, property_id, user_id]
	 *
	 */
	@Table("properties_users")
	public static class PropertiesUsers extends EncryptedModel {

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "";
		}}
	public static final PropertiesUsers PROPERTIESUSERS = new PropertiesUsers();
	
	
	/**
	 * Group Model
	 * Columns: [id, name, tabs]
	 * 
	 *
	 */
	@Table("groups")
	public static class Group extends EncryptedModel {
		
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
			this.setEncrypted("name", name.replace(",", ""));
		}
		
		public void setTabs(ArrayList<TabType> tabs) {
			this.setEncrypted("tabs", tabs.toString());
		}
		
		public void addTab(TabType tab) {
			ArrayList<TabType> tabs = this.getTabs();
			if(!tabs.contains(tab)){
				tabs.add(tab);
				this.setEncrypted("tabs", tabs.toString());
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
			return this.getAsString("name");
		}
	}
	public static final Group GROUP = new Group();
	
	
	/**
	 * Groups to Users Join Model
	 * Columns: [id, group_id, user_id]
	 *
	 */
	@Table("groups_users")
	public static class GroupsUsers extends EncryptedModel {

		@Override
		public String toString() {
			return "";
		}}
	public static final GroupsUsers GROUPSUSERS = new GroupsUsers();
	
	
	/**
	 * Ticket Model
	 * Columns: [id, date, property_id, description, status, desired_date, estimated_cost, actual_cost, created_at]
	 *
	 */
	@Table("tickets")
	public static class Ticket extends EncryptedModel {

		public enum Status {PENDING, COMPLETED};
		@Override
		public String toString() {
			return this.getId().toString();
		}
		
	}
	public static final Ticket TICKET = new Ticket();
	
	
	/**
	 * Users to Tickets Join Model
	 * Columns: [id, user_id, ticket_id]
	 */
	@Table("users_tickets")
	public static class UsersTickets extends EncryptedModel {

		@Override
		public String toString() {
			return "";
		}
		
	}
	public static final UsersTickets USERSTICKETS = new UsersTickets();
	
	
	/**
	 * Clients to Tickets Join Model
	 * Columns: [id, client_id, ticket_id]
	 */
	@Table("clients_tickets")
	public static class ClientsTickets extends EncryptedModel {

		@Override
		public String toString() {
			return "";
		}
		
	}
	public static final ClientsTickets CLIENTSTICKETS = new ClientsTickets();
}

