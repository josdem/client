package com.all.client.view.model;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.model.LocalFolder;
import com.all.client.model.LocalPlaylist;
import com.all.shared.model.MusicEntity;

public class MusicEntityTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private static final Log LOG = LogFactory.getLog(MusicEntityTreeNode.class);

	public MusicEntityTreeNode(MusicEntity userObject) {
		super(userObject);
	}

	public void setUserObject(MusicEntity entity) {
		this.userObject = entity;
	}

	@Override
	public void setUserObject(Object newValue) {

		if (newValue instanceof String) {
			try {
				if (userObject instanceof LocalPlaylist) {
					((LocalPlaylist) userObject).setName((String) newValue);
				} else if (userObject instanceof LocalFolder) {
					((LocalFolder) userObject).setName((String) newValue);
				}
			} catch (IllegalArgumentException e) {
				LOG.error(e, e);
			}
		} else if (newValue instanceof MusicEntity) {
			super.setUserObject(newValue);
		} else {
			throw new IllegalArgumentException("Object " + newValue.getClass().getName() + " Not expected");
		}
	}

}