package com.all.client.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.view.alerts.DrawerDialog;
import com.all.client.view.dialog.DialogFactory;

@Component
public class DisplayBackPanel extends JPanel {
	private static final long serialVersionUID = -2396290674021354409L;

	private static final int X_INSET = 73;

	private static final int WIDTH_INSET = 138;

	private static final int Y_INSET = 7;

	@Autowired
	DialogFactory dialogFactory;

	private DisplayContainerPanel displayContainerPanel;

	 private Log log = LogFactory.getLog(this.getClass());

	public DisplayBackPanel() {
	}

	public void setDisplayContainerPanel(DisplayContainerPanel displayContainerPanel) {
		this.displayContainerPanel = displayContainerPanel;
		initilize();
	}

	public void recalculateBubbleBounds() {
		if (displayContainerPanel != null) {
			Rectangle bounds = new Rectangle(this.getBounds());
			bounds.height = displayContainerPanel.getHeight();
			int maxWidth = displayContainerPanel.getCenterPanel().getMaximumSize().width;
			if (bounds.width > maxWidth) {
				bounds.x += ((bounds.width - maxWidth) / 2);
				bounds.width = maxWidth;
			}
			displayContainerPanel.setBounds(bounds);
			displayContainerPanel.revalidate();
		}
	}

	public void recalculateDrawerBounds() {
		DrawerDialog drawerDialog = dialogFactory.getDrawerDialog();
		try {
			if(displayContainerPanel.isShowing()){
				Point locationContainer = displayContainerPanel.getLocationOnScreen();
				Point locationThis = this.getLocationOnScreen();
				Rectangle rectangle = new Rectangle(this.getBounds());
				rectangle.y = locationThis.y + this.getHeight() - Y_INSET;
				rectangle.x = locationContainer.x + X_INSET;
				rectangle.width = displayContainerPanel.getWidth() - WIDTH_INSET;
				if (drawerDialog.boundsWillTakeAction(rectangle)) {
					drawerDialog.setBounds(rectangle);
					drawerDialog.managePanelsSizes();
				}
			}
		} catch (Exception shitTheBed) {
			log.error("Could not calculate bounds for Drawer >>> ");
		}
	}

	private void initilize() {
		// TODO create an adapter and replace all anonymous classes
		this.addHierarchyBoundsListener(new HierarchyBoundsListener() {
			@Override
			public void ancestorMoved(HierarchyEvent e) {
				recalculateDrawerBounds();
			}

			@Override
			public void ancestorResized(HierarchyEvent e) {
				if (e.getChangedParent() != null) {
					recalculateBubbleBounds();
				}
				recalculateDrawerBounds();
			}
		});
	}
}
