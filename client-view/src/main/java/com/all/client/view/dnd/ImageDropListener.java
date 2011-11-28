package com.all.client.view.dnd;

import java.awt.Image;
import java.awt.Point;

import com.all.client.model.Picture;
import com.all.client.model.ResizeImageType;
import com.all.client.view.components.ImagePanel;
import com.all.client.view.dialog.DialogFactory;
import com.all.core.actions.FileSystemValidatorLight;
import com.all.core.model.ContactCollection;
import com.all.observ.ObservValue;
import com.all.observ.Observable;
import com.all.observ.ObserverCollection;

public class ImageDropListener implements DropListener {
	private static final Class<?>[] classes = new Class<?>[] { Picture.class, ContactCollection.class };

	@Override
	public Class<?>[] handledTypes() {
		return classes;
	}

	private final ImagePanel imagePanel;
	private final DialogFactory dialogFactory;
	private final Observable<ObservValue<ImagePanel>> nombre = new Observable<ObservValue<ImagePanel>>();

	private final ResizeImageType resizeImageType;

	public ImageDropListener(ImagePanel imagePanel, DialogFactory dialogFactory, ResizeImageType resizeImageType) {
		this.imagePanel = imagePanel;
		this.dialogFactory = dialogFactory;
		this.resizeImageType = resizeImageType;
	}

	@Override
	public void doDrop(DraggedObject draggedObject, Point location) {
		Picture picture = draggedObject.get(Picture.class);
		if (picture != null) {
			doDrop(picture, location);
		}
		ContactCollection contactCollection = draggedObject.get(ContactCollection.class);
		if (contactCollection != null) {
			doDrop(picture, location);
		}
	}

	public void doDrop(Picture pic, Point location) {
		if (!pic.isValidImageSize()) {
			dialogFactory.showErrorDialog("editContact.portrait.dnd.error.size");
			return;
		}

		if (!pic.isProportionedImage()) {
			dialogFactory.showErrorDialog("editContact.portrait.dnd.error.proportion");
			return;
		}

		Image image = dialogFactory.showEditPhotoDialog(pic, resizeImageType);
		if (image != null) {
			imagePanel.setImage(image, .17, .17);
			this.nombre.fire(new ObservValue<ImagePanel>(imagePanel));
		}
	}

	public void doDrop(FileSystemValidatorLight validator, Point location) {
		dialogFactory.showErrorDialog("editContact.portrait.dnd.error.file");
	}

	@Override
	public boolean validateDrop(DraggedObject draggedObject, Point location) {
		return draggedObject.is(Picture.class, FileSystemValidatorLight.class);
	}

	public ObserverCollection<ObservValue<ImagePanel>> onDropped() {
		return nombre;
	}
}
