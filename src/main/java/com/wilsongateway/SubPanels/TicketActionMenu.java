package com.wilsongateway.SubPanels;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables.Ticket;
import com.wilsongateway.Tabs.EditTicket;

public class TicketActionMenu extends VerticalLayout{

	private static final long serialVersionUID = 3825966431210760077L;

	public TicketActionMenu(SessionManager manager, Ticket ticket) {
		Button details = new Button("Details", e -> {
			manager.getDash().getTabNav().addView("TICKETDETAILS", new EditTicket(manager, ticket, true));
			manager.getDash().getTabNav().navigateTo("TICKETDETAILS");
		});
		details.setStyleName("link small");
		details.setIcon(FontAwesome.ALIGN_JUSTIFY);
		addComponent(details);
		
		Button repair = new Button("Repair");
		repair.setStyleName("link small");
		repair.setIcon(FontAwesome.WRENCH);
		addComponent(repair);
		
		Button reimburse = new Button("Reimburse");
		reimburse.setStyleName("link small");
		reimburse.setIcon(FontAwesome.MONEY);
		addComponent(reimburse);
	}
}
