package com.wilsongateway.Framework;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.javalite.activejdbc.Base;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;

/**
 * 
 * @author Nick Wilson
 *
 */
@SuppressWarnings("serial")
@Deprecated
public class SessionCleanup implements DetachListener{

	private SessionManager manager;
	
	public SessionCleanup(SessionManager manager){
		this.manager = manager;
	}
	
	@Override
	public void detach(DetachEvent event)throws NullPointerException{
//		for(Connection c : manager.getConnections()){
//			try {
//				c.close();
//			} catch (SQLException e) {}
//		}
//		
//		Base.close(true);
		
		VaadinSession.getCurrent().close();
		SessionManager.getCurrent().getPage().setLocation("");
	}
}
