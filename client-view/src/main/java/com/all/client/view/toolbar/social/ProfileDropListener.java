package com.all.client.view.toolbar.social;

import java.awt.Point;

import com.all.appControl.control.ViewEngine;
import com.all.client.view.dnd.DraggedObject;
import com.all.client.view.dnd.DropListener;
import com.all.core.actions.Actions;
import com.all.core.actions.ComposeView;
import com.all.core.actions.LoadContactProfileAction;
import com.all.core.model.ContactCollection;
import com.all.core.model.SubViews;
import com.all.core.model.Views;

public class ProfileDropListener implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { ContactCollection.class };

	private final ViewEngine viewEngine;

	public ProfileDropListener(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		final ContactCollection contacts = draggedObject.get(ContactCollection.class);
		viewEngine.send(Actions.Social.LOAD_USER_PROFILE, new LoadContactProfileAction(contacts.getContacts().get(0),
				new ComposeView(Views.PROFILE, SubViews.ALL)));
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		ContactCollection contacts = draggedObject.get(ContactCollection.class);
		return (contacts != null && contacts.getContacts().size() == 1);
	}

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

}
