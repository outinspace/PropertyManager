package com.wilsongateway.Framework;

import java.sql.Connection;
import java.sql.SQLException;

import org.javalite.activejdbc.Base;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;

/**
 * 
 * @author Nick Wilson
 *
 */
public class SessionCleanup implements DetachListener{

	@Override
	public void detach(DetachEvent event)throws NullPointerException{
		System.out.println("Cleaning up session");
		
		Base.close(true);
		
		VaadinSession.getCurrent().close();
		SessionManager.getCurrent().getPage().setLocation("");
	}
}
