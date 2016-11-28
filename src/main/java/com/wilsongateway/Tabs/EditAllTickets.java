package com.wilsongateway.Tabs;

import java.util.List;

import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;

import com.vaadin.ui.Layout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.wilsongateway.Forms.ViewAllForm;
import com.wilsongateway.Framework.EncryptedModel;
import com.wilsongateway.Framework.SessionManager;
import com.wilsongateway.Framework.Tables;
import com.wilsongateway.Framework.Tables.Property;
import com.wilsongateway.Framework.Tables.Ticket;
import com.wilsongateway.SubPanels.TicketActionMenu;

public class EditAllTickets extends ViewAllForm{

	private static final long serialVersionUID = -6233620116682885643L;

	public EditAllTickets(SessionManager manager) {
		super(manager, "Global Tickets", true);
		
		this.addRefreshButton();
		this.addReportBtn(Tables.TICKET);
	}

	@Override
	protected void navToEdit(EncryptedModel usr) {
		
	}

	@Override
	protected void setContainerProperties(Table t) {
		addTableColumn("id", String.class, "ID", 0);
		addTableColumn("date", String.class, "Date", 0);
		addTableColumn("status", String.class, "Status", 0);
		addRelationshipColumn("property", Property.class, "Property", 0);
		addTableColumn("description", String.class, "Description", 1);
	}

	@Override
	protected LazyList<? extends Model> getModels() {
		return Tables.TICKET.findAll();
	}

	@Override
	protected void fillActionLayout(Layout actionLayout, EncryptedModel em){
		PopupView actionMenu = new PopupView("More", new TicketActionMenu(manager, (Ticket)em));
		actionLayout.addComponent(actionMenu);
	}
}
