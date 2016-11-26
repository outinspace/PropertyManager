package com.wilsongateway.Framework;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;

/**
 * 
 * @author Nicholas Wilson
 *         www.outin.space
 *
 */
@Deprecated
public class SessionCleanup implements DetachListener{

	private static final long serialVersionUID = -4348117613345786778L;
	
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
