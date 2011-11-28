package com.all.client.view;

import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

public final class EditingComponent<V extends JComponent, E extends JComponent> extends JPanel {
	private static final long serialVersionUID = 1L;
	private V visualComponent;
	private E editorComponent;
	private JComponent visualContainer;
	private JComponent editorContainer;
	private boolean editing;
	private CardLayout cardLayout = new CardLayout();

	public EditingComponent(V visualComponent, E editorComponent, JPanel visualContainer, JPanel editorContainer) {
		this.editorComponent = editorComponent;
		this.visualComponent = visualComponent;
		this.visualContainer = visualContainer == null ? visualComponent : visualContainer;
		this.editorContainer = editorContainer == null ? editorComponent : editorContainer;

		this.setLayout(cardLayout);

		this.add(this.visualContainer, "visual");
		this.add(this.editorContainer, "editor");
		endEdit();
	}

	public void startEdit() {
		editing = true;
		cardLayout.show(this, "editor");
	}

	public void endEdit() {
		editing = false;
		cardLayout.show(this, "visual");
	}

	public boolean isEditing() {
		return editing;
	}

	public V getVisualComponent() {
		return visualComponent;
	}

	public E getEditorComponent() {
		return editorComponent;
	}

}
