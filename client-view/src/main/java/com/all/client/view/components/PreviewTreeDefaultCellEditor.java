package com.all.client.view.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class PreviewTreeDefaultCellEditor implements ActionListener, TreeCellEditor, TreeSelectionListener {
	protected TreeCellEditor realEditor;

	protected PreviewTreeCellRenderer renderer;

	protected Container editingContainer;

	transient protected Component editingComponent;

	protected boolean canEdit;

	protected transient int offset;

	protected transient JTree tree;

	protected transient TreePath lastPath;

	protected transient Timer timer;

	protected transient int lastRow;

	protected Color borderSelectionColor;

	protected transient Icon editingIcon;

	protected Font font;

	public PreviewTreeDefaultCellEditor(JTree tree, PreviewTreeCellRenderer renderer) {
		this(tree, renderer, null);
	}

	/**
	 * Constructs a <code>DefaultTreeCellEditor</code> object for a
	 * <code>JTree</code> using the specified renderer and the specified editor.
	 * (Use this constructor for specialized editing.)
	 * 
	 * @param tree
	 *          a <code>JTree</code> object
	 * @param renderer
	 *          a <code>DefaultTreeCellRenderer</code> object
	 * @param editor
	 *          a <code>TreeCellEditor</code> object
	 */
	public PreviewTreeDefaultCellEditor(JTree tree, PreviewTreeCellRenderer renderer, TreeCellEditor editor) {
		this.renderer = renderer;
		realEditor = editor;
		if (realEditor == null) {
			realEditor = createTreeCellEditor();
		}
		editingContainer = createContainer();
		setTree(tree);
		setBorderSelectionColor(UIManager.getColor("Tree.editorBorderSelectionColor"));
	}

	/**
	 * Sets the color to use for the border.
	 * 
	 * @param newColor
	 *          the new border color
	 */
	public void setBorderSelectionColor(Color newColor) {
		borderSelectionColor = newColor;
	}

	/**
	 * Returns the color the border is drawn.
	 * 
	 * @return the border selection color
	 */
	public Color getBorderSelectionColor() {
		return borderSelectionColor;
	}

	/**
	 * Sets the font to edit with. <code>null</code> indicates the renderers font
	 * should be used. This will NOT override any font you have set in the editor
	 * the receiver was instantied with. If <code>null</code> for an editor was
	 * passed in a default editor will be created that will pick up this font.
	 * 
	 * @param font
	 *          the editing <code>Font</code>
	 * @see #getFont
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Gets the font used for editing.
	 * 
	 * @return the editing <code>Font</code>
	 * @see #setFont
	 */
	public Font getFont() {
		return font;
	}

	//
	// TreeCellEditor
	//

	/**
	 * Configures the editor. Passed onto the <code>realEditor</code>.
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row) {
		setTree(tree);
		lastRow = row;
		determineOffset(tree, value, isSelected, expanded, leaf, row);

		if (editingComponent != null) {
			editingContainer.remove(editingComponent);
		}
		editingComponent = realEditor.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);

		// this is kept for backwards compatability but isn't really needed
		// with the current BasicTreeUI implementation.
		TreePath newPath = tree.getPathForRow(row);

		canEdit = (lastPath != null && newPath != null && lastPath.equals(newPath));

		Font font = getFont();

		if (font == null) {
			if (renderer != null) {
				font = renderer.getFont();
			}
			if (font == null) {
				font = tree.getFont();
			}
		}
		editingContainer.setFont(font);
		prepareForEditing();
		return editingContainer;
	}

	/**
	 * Returns the value currently being edited.
	 * 
	 * @return the value currently being edited
	 */
	public Object getCellEditorValue() {
		return realEditor.getCellEditorValue();
	}

	/**
	 * If the <code>realEditor</code> returns true to this message,
	 * <code>prepareForEditing</code> is messaged and true is returned.
	 */
	public boolean isCellEditable(EventObject event) {
		boolean retValue = false;
		boolean editable = false;

		if (event != null) {
			if (event.getSource() instanceof JTree) {
				setTree((JTree) event.getSource());
				if (event instanceof MouseEvent) {
					TreePath path = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
					editable = (lastPath != null && path != null && lastPath.equals(path));
					if (path != null) {
						lastRow = tree.getRowForPath(path);
						Object value = path.getLastPathComponent();
						boolean isSelected = tree.isRowSelected(lastRow);
						boolean expanded = tree.isExpanded(path);
						TreeModel treeModel = tree.getModel();
						boolean leaf = treeModel.isLeaf(value);
						determineOffset(tree, value, isSelected, expanded, leaf, lastRow);
					}
				}
			}
		}
		if (!realEditor.isCellEditable(event)) {
			return false;
		}
		if (canEditImmediately(event)) {
			retValue = true;
		} else if (editable && shouldStartEditingTimer(event)) {
			startEditingTimer();
		} else if (timer != null && timer.isRunning()) {
			timer.stop();
		}
		if (retValue) {
			prepareForEditing();
		}
		return retValue;
	}

	/**
	 * Messages the <code>realEditor</code> for the return value.
	 */
	public boolean shouldSelectCell(EventObject event) {
		return realEditor.shouldSelectCell(event);
	}

	/**
	 * If the <code>realEditor</code> will allow editing to stop, the
	 * <code>realEditor</code> is removed and true is returned, otherwise false is
	 * returned.
	 */
	public boolean stopCellEditing() {
		if (realEditor.stopCellEditing()) {
			cleanupAfterEditing();
			return true;
		}
		return false;
	}

	/**
	 * Messages <code>cancelCellEditing</code> to the <code>realEditor</code> and
	 * removes it from this instance.
	 */
	public void cancelCellEditing() {
		realEditor.cancelCellEditing();
		cleanupAfterEditing();
	}

	/**
	 * Adds the <code>CellEditorListener</code>.
	 * 
	 * @param l
	 *          the listener to be added
	 */
	public void addCellEditorListener(CellEditorListener l) {
		realEditor.addCellEditorListener(l);
	}

	/**
	 * Removes the previously added <code>CellEditorListener</code>.
	 * 
	 * @param l
	 *          the listener to be removed
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		realEditor.removeCellEditorListener(l);
	}

	/**
	 * Returns an array of all the <code>CellEditorListener</code>s added to this
	 * DefaultTreeCellEditor with addCellEditorListener().
	 * 
	 * @return all of the <code>CellEditorListener</code>s added or an empty array
	 *         if no listeners have been added
	 * @since 1.4
	 */
	public CellEditorListener[] getCellEditorListeners() {
		return ((DefaultCellEditor) realEditor).getCellEditorListeners();
	}

	//
	// TreeSelectionListener
	//

	/**
	 * Resets <code>lastPath</code>.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if (tree != null) {
			if (tree.getSelectionCount() == 1) {
				lastPath = tree.getSelectionPath();
			} else {
				lastPath = null;
			}
		}
		if (timer != null) {
			timer.stop();
		}
	}

	//
	// ActionListener (for Timer).
	//

	/**
	 * Messaged when the timer fires, this will start the editing session.
	 */
	public void actionPerformed(ActionEvent e) {
		if (tree != null && lastPath != null) {
			tree.startEditingAtPath(lastPath);
		}
	}

	//
	// Local methods
	//

	/**
	 * Sets the tree currently editing for. This is needed to add a selection
	 * listener.
	 * 
	 * @param newTree
	 *          the new tree to be edited
	 */
	protected void setTree(JTree newTree) {
		if (tree != newTree) {
			if (tree != null) {
				tree.removeTreeSelectionListener(this);
			}
			tree = newTree;
			if (tree != null) {
				tree.addTreeSelectionListener(this);
			}
			if (timer != null) {
				timer.stop();
			}
		}
	}

	/**
	 * Returns true if <code>event</code> is a <code>MouseEvent</code> and the
	 * click count is 1.
	 * 
	 * @param event
	 *          the event being studied
	 */
	protected boolean shouldStartEditingTimer(EventObject event) {
		if ((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
			MouseEvent me = (MouseEvent) event;

			return (me.getClickCount() == 1 && inHitRegion(me.getX(), me.getY()));
		}
		return false;
	}

	/**
	 * Starts the editing timer.
	 */
	protected void startEditingTimer() {
		if (timer == null) {
			timer = new Timer(1200, this);
			timer.setRepeats(false);
		}
		timer.start();
	}

	/**
	 * Returns true if <code>event</code> is <code>null</code>, or it is a
	 * <code>MouseEvent</code> with a click count > 2 and <code>inHitRegion</code>
	 * returns true.
	 * 
	 * @param event
	 *          the event being studied
	 */
	protected boolean canEditImmediately(EventObject event) {
		if ((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent) event)) {
			MouseEvent me = (MouseEvent) event;

			return ((me.getClickCount() > 2) && inHitRegion(me.getX(), me.getY()));
		}
		return (event == null);
	}

	/**
	 * Returns true if the passed in location is a valid mouse location to start
	 * editing from. This is implemented to return false if <code>x</code> is <=
	 * the width of the icon and icon gap displayed by the renderer. In other
	 * words this returns true if the user clicks over the text part displayed by
	 * the renderer, and false otherwise.
	 * 
	 * @param x
	 *          the x-coordinate of the point
	 * @param y
	 *          the y-coordinate of the point
	 * @return true if the passed in location is a valid mouse location
	 */
	protected boolean inHitRegion(int x, int y) {
		if (lastRow != -1 && tree != null) {
			Rectangle bounds = tree.getRowBounds(lastRow);
			ComponentOrientation treeOrientation = tree.getComponentOrientation();

			if (treeOrientation.isLeftToRight()) {
				if (bounds != null && x <= (bounds.x + offset) && offset < (bounds.width - 5)) {
					return false;
				}
			} else if (bounds != null && (x >= (bounds.x + bounds.width - offset + 5) || x <= (bounds.x + 5))
					&& offset < (bounds.width - 5)) {
				return false;
			}
		}
		return true;
	}

	protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (renderer != null) {
			if (leaf) {
				editingIcon = renderer.getLeafIcon();
			} else if (expanded) {
				editingIcon = renderer.getOpenIcon();
			} else {
				editingIcon = renderer.getClosedIcon();
			}
			if (editingIcon != null) {
				offset = renderer.getIconTextGap() + editingIcon.getIconWidth();
			} else {
				offset = renderer.getIconTextGap();
			}
		} else {
			editingIcon = null;
			offset = 0;
		}
	}

	/**
	 * Invoked just before editing is to start. Will add the
	 * <code>editingComponent</code> to the <code>editingContainer</code>.
	 */
	protected void prepareForEditing() {
		if (editingComponent != null) {
			editingContainer.add(editingComponent);
		}
	}

	/**
	 * Creates the container to manage placement of <code>editingComponent</code>.
	 */
	protected Container createContainer() {
		return new EditorContainer();
	}

	/**
	 * This is invoked if a <code>TreeCellEditor</code> is not supplied in the
	 * constructor. It returns a <code>TextField</code> editor.
	 * 
	 * @return a new <code>TextField</code> editor
	 */
	@SuppressWarnings("serial")
	protected TreeCellEditor createTreeCellEditor() {
		Border aBorder = UIManager.getBorder("Tree.editorBorder");
		DefaultCellEditor editor = new DefaultCellEditor(new DefaultTextField(aBorder)) {
			public boolean shouldSelectCell(EventObject event) {
				boolean retValue = super.shouldSelectCell(event);
				return retValue;
			}
		};

		// One click to edit.
		editor.setClickCountToStart(1);
		return editor;
	}

	/**
	 * Cleans up any state after editing has completed. Removes the
	 * <code>editingComponent</code> the <code>editingContainer</code>.
	 */
	private void cleanupAfterEditing() {
		if (editingComponent != null) {
			editingContainer.remove(editingComponent);
		}
		editingComponent = null;
	}

	// Serialization support.
	@SuppressWarnings("unchecked")
	private void writeObject(ObjectOutputStream s) throws IOException {
		Vector values = new Vector();

		s.defaultWriteObject();
		// Save the realEditor, if its Serializable.
		if (realEditor != null && realEditor instanceof Serializable) {
			values.addElement("realEditor");
			values.addElement(realEditor);
		}
		s.writeObject(values);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		Vector values = (Vector) s.readObject();
		int indexCounter = 0;
		int maxCounter = values.size();

		if (indexCounter < maxCounter && values.elementAt(indexCounter).equals("realEditor")) {
			realEditor = (TreeCellEditor) values.elementAt(++indexCounter);
			indexCounter++;
		}
	}

	/**
	 * <code>TextField</code> used when no editor is supplied. This textfield
	 * locks into the border it is constructed with. It also prefers its parents
	 * font over its font. And if the renderer is not <code>null</code> and no
	 * font has been specified the preferred height is that of the renderer.
	 */
	@SuppressWarnings("serial")
	public class DefaultTextField extends JTextField {
		/** Border to use. */
		protected Border border;

		/**
		 * Constructs a <code>DefaultTreeCellEditor.DefaultTextField</code> object.
		 * 
		 * @param border
		 *          a <code>Border</code> object
		 * @since 1.4
		 */
		public DefaultTextField(Border border) {
			setBorder(border);
		}

		/**
		 * Sets the border of this component.
		 * <p>
		 * This is a bound property.
		 * 
		 * @param border
		 *          the border to be rendered for this component
		 * @see Border
		 * @see CompoundBorder
		 * @beaninfo bound: true preferred: true attribute: visualUpdate true
		 *           description: The component's border.
		 */
		public void setBorder(Border border) {
			super.setBorder(border);
			this.border = border;
		}

		/**
		 * Overrides <code>JComponent.getBorder</code> to returns the current
		 * border.
		 */
		public Border getBorder() {
			return border;
		}

		// implements java.awt.MenuContainer
		public Font getFont() {
			Font font = super.getFont();

			// Prefer the parent containers font if our font is a
			// FontUIResource
			if (font instanceof FontUIResource) {
				Container parent = getParent();

				if (parent != null && parent.getFont() != null) {
					font = parent.getFont();
				}
			}
			return font;
		}

		/**
		 * Overrides <code>JTextField.getPreferredSize</code> to return the
		 * preferred size based on current font, if set, or else use renderer's
		 * font.
		 * 
		 * @return a <code>Dimension</code> object containing the preferred size
		 */
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();

			// If not font has been set, prefer the renderers height.
			if (renderer != null && PreviewTreeDefaultCellEditor.this.getFont() == null) {
				Dimension rSize = renderer.getPreferredSize();

				size.height = rSize.height;
			}
			return size;
		}
	}

	/**
	 * Container responsible for placing the <code>editingComponent</code>.
	 */
	@SuppressWarnings("serial")
	public class EditorContainer extends Container {
		/**
		 * Constructs an <code>EditorContainer</code> object.
		 */
		public EditorContainer() {
			setLayout(null);
		}

		/**
		 * Overrides <code>Container.paint</code> to paint the node's icon and use
		 * the selection color for the background.
		 */
		public void paint(Graphics g) {
			Dimension size = getSize();

			// Then the icon.
			if (editingIcon != null) {
				int yLoc = Math.max(0, (getSize().height - editingIcon.getIconHeight()) / 2);

				editingIcon.paintIcon(this, g, 0, yLoc);
			}

			// Border selection color
			Color background = getBorderSelectionColor();
			if (background != null) {
				g.setColor(background);
				g.drawRect(0, 0, size.width - 1, size.height - 1);
			}
			super.paint(g);
		}

		/**
		 * Lays out this <code>Container</code>. If editing, the editor will be
		 * placed at <code>offset</code> in the x direction and 0 for y.
		 */
		public void doLayout() {
			if (editingComponent != null) {
				Dimension cSize = getSize();

				editingComponent.getPreferredSize();
				editingComponent.setLocation(offset, 0);
				editingComponent.setBounds(offset, 0, cSize.width - offset, cSize.height);
			}
		}

		/**
		 * Returns the preferred size for the <code>Container</code>. This will be
		 * at least preferred size of the editor plus <code>offset</code>.
		 * 
		 * @return a <code>Dimension</code> containing the preferred size for the
		 *         <code>Container</code>; if <code>editingComponent</code> is
		 *         <code>null</code> the <code>Dimension</code> returned is 0, 0
		 */
		public Dimension getPreferredSize() {
			if (editingComponent != null) {
				Dimension pSize = editingComponent.getPreferredSize();

				pSize.width += offset + 5;

				Dimension rSize = (renderer != null) ? renderer.getPreferredSize() : null;

				if (rSize != null) {
					pSize.height = Math.max(pSize.height, rSize.height);
				}
				if (editingIcon != null) {
					pSize.height = Math.max(pSize.height, editingIcon.getIconHeight());
				}

				// Make sure width is at least 100.
				pSize.width = Math.max(pSize.width, 100);
				return pSize;
			}
			return new Dimension(0, 0);
		}
	}
}
