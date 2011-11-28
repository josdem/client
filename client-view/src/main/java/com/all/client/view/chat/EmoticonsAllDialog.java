package com.all.client.view.chat;

import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.all.client.model.Emoticon;
import com.all.core.common.view.transparency.TransparencyManagerFactory;

public final class EmoticonsAllDialog extends EmoticonsDialog {

	private static final long serialVersionUID = 4204387139147765722L;

	private static final int SIZE_EMOTICON_GRID = 246;
	private static final int SIZE_GRID = 6;

	public EmoticonsAllDialog(JFrame parent, JTextArea sendArea) {
		super(parent, sendArea);
		initiliaze(parent);
		TransparencyManagerFactory.getManager().setWindowOpaque(this, false);
	}

	private void initiliaze(JFrame parent) {
		Rectangle boundsParent = parent.getBounds();
		int x = (int) (boundsParent.getX() + parent.getWidth() - SIZE_EMOTICON_GRID + 9);
		int y = (int) (boundsParent.getY() + parent.getHeight() - SIZE_EMOTICON_GRID - 19);
		this.setBounds(new Rectangle(x, y, SIZE_EMOTICON_GRID, SIZE_EMOTICON_GRID));

		getEmoticonPanel().setName("emoticonPanel");
	}

	@Override
	protected void addEmoticonToGrid() {
		for (int y = 0; y < SIZE_GRID; y++) {
			for (int x = 0; x < SIZE_GRID; x++) {
				int index = (SIZE_GRID * y) + x;

				Emoticon emoticonBean = EmoticonHandler.allEmoticonsList.get(index);
				String emoticonKey = emoticonBean.getKey();
				String strEmoticonPng = EmoticonHandler.getEmoticon(emoticonKey, EXTENSION_34X34, ".png",
						EmoticonHandler.allEmoticonsList);
				String strEmoticonGif = EmoticonHandler.getEmoticon(emoticonKey, EXTENSION_34X34, ".gif",
						EmoticonHandler.allEmoticonsList);

				final EmoticonButton emoticonButton = createEmoticonButton(emoticonKey, strEmoticonPng, strEmoticonGif);

				emoticonButton.setBounds(new Rectangle(MARGIN_GRID_HORIZONTAL + (x * EMOTICON_LABEL_SIZE)
						+ (x * SIZE_GRAY_LINE), MARGIN_GRID_VERTICAL + (y * EMOTICON_LABEL_SIZE) + (y * SIZE_GRAY_LINE),
						EMOTICON_LABEL_SIZE, EMOTICON_LABEL_SIZE));

				getEmoticonPanel().add(emoticonButton);
			}
		}
	}

}
