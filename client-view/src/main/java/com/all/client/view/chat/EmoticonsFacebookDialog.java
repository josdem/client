package com.all.client.view.chat;

import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.all.client.model.Emoticon;
import com.all.core.common.view.transparency.TransparencyManagerFactory;

public class EmoticonsFacebookDialog extends EmoticonsDialog {

	private static final long serialVersionUID = 4204387139147765722L;

	private static final int SIZE_EMOTICON_GRID_WIDTH = 175;
	private static final int SIZE_EMOTICON_GRID_HEIGHT = 105;
	private static final int SIZE_GRID_HEIGHT = 2;
	private static final int SIZE_GRID_WIDTH = 4;

	public EmoticonsFacebookDialog(JFrame parent, JTextArea sendArea) {
		super(parent, sendArea);
		initiliaze(parent);
		TransparencyManagerFactory.getManager().setWindowOpaque(this, false);
	}

	private void initiliaze(JFrame parent) {
		Rectangle boundsParent = parent.getBounds();
		int x = (int) (boundsParent.getX() + parent.getWidth() - SIZE_EMOTICON_GRID_WIDTH + 9);
		int y = (int) (boundsParent.getY() + parent.getHeight() - SIZE_EMOTICON_GRID_HEIGHT - 19);
		this.setBounds(new Rectangle(x, y, SIZE_EMOTICON_GRID_WIDTH, SIZE_EMOTICON_GRID_HEIGHT));

		getEmoticonPanel().setName("emoticonFacebookPanel");
	}

	@Override
	protected void addEmoticonToGrid() {
		int index = 0;
		for (int y = 0; y < SIZE_GRID_HEIGHT; y++) {
			for (int x = 0; x < SIZE_GRID_WIDTH; x++) {
				Emoticon emoticonBean = EmoticonHandler.facebookEmoticonsList.get(index++);
				String emoticonKey = emoticonBean.getKey();
				String strEmoticonPng = EmoticonHandler.getEmoticon(emoticonKey, EXTENSION_34X34, ".png",
						EmoticonHandler.facebookEmoticonsList);
				String strEmoticonGif = EmoticonHandler.getEmoticon(emoticonKey, EXTENSION_34X34, ".gif",
						EmoticonHandler.facebookEmoticonsList);

				EmoticonButton emoticonButton = createEmoticonButton(emoticonKey, strEmoticonPng, strEmoticonGif);

				emoticonButton.setBounds(new Rectangle(MARGIN_GRID_HORIZONTAL + (x * EMOTICON_LABEL_SIZE)
						+ (x * SIZE_GRAY_LINE), MARGIN_GRID_VERTICAL + (y * EMOTICON_LABEL_SIZE) + (y * SIZE_GRAY_LINE),
						EMOTICON_LABEL_SIZE, EMOTICON_LABEL_SIZE));

				getEmoticonPanel().add(emoticonButton);
			}
		}
	}

}
