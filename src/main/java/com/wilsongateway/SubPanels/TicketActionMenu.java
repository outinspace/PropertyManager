package com.wilsongateway.SubPanels;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.wilsongateway.Framework.EncryptedModel;

public class TicketActionMenu extends VerticalLayout{

	private static final long serialVersionUID = 3825966431210760077L;

	public TicketActionMenu(EncryptedModel em) {
		Button details = new Button("Details");
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
