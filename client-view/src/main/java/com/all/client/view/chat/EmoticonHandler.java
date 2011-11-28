package com.all.client.view.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.chat.ChatType;
import com.all.client.model.Emoticon;
import com.all.shared.model.ChatMessage;

public final class EmoticonHandler {

	private static final String PROBLEMS_TO_EXTRACT = "Problems to extract:";
	public static List<Emoticon> allEmoticonsList = new ArrayList<Emoticon>();
	public static List<Emoticon> facebookEmoticonsList = new ArrayList<Emoticon>();
	public static List<String> resourcesChat = new ArrayList<String>();

	private static final String USER_DIR = "user.dir";

	private static Log log = LogFactory.getLog(EmoticonHandler.class);

	private static final String RESOLUTION_75x75 = "_75x75";
	private static final String RESOLUTION_34x34 = "_34x34";
	private static final String GIF_EXTENSION = ".gif";
	private static final String PNG_EXTENSION = ".png";
	private static final String EMOTICONS_IMGS_PATH = "/emoticons/";

	private static final String IMG_TAG = "<img src='";
	private static final String IMG_SCALED_TO_34_TAG = "<img width='32' height='32' src='";
	private static final String IMG_END_TAG = "'></img>";
	private static final String IMG_END_TAG_LEFT = "' align='absmiddle'></img>";

	private static final String EXTENSION_34X34 = "_34x34";
	private static final String EXTENSION_75X75 = "_75x75";

	static {
		allEmoticonsList.add(0, new Emoticon(":d", "superHappyEmoticon"));
		allEmoticonsList.add(1, new Emoticon(":)", "happyEmoticon"));
		allEmoticonsList.add(2, new Emoticon(";(", "superSadEmoticon"));
		allEmoticonsList.add(3, new Emoticon(";[", "middleSadEmoticon"));
		allEmoticonsList.add(4, new Emoticon(":(", "sadEmoticon"));
		allEmoticonsList.add(5, new Emoticon("(cry)", "cryEmoticon"));
		allEmoticonsList.add(6, new Emoticon(":s", "unconfortableEmoticon"));
		allEmoticonsList.add(7, new Emoticon("zzz", "sleepEmoticon"));
		allEmoticonsList.add(8, new Emoticon("(sick)", "sickEmoticon"));
		allEmoticonsList.add(9, new Emoticon("(cold)", "coldEmoticon"));
		allEmoticonsList.add(10, new Emoticon("(ice)", "iceEmoticon"));
		allEmoticonsList.add(11, new Emoticon("(doh)", "dohEmoticon"));
		allEmoticonsList.add(12, new Emoticon("(evil)", "evilEmoticon"));
		allEmoticonsList.add(13, new Emoticon("(fire)", "fireEmoticon"));
		allEmoticonsList.add(14, new Emoticon(":@", "angryEmoticon"));
		allEmoticonsList.add(15, new Emoticon("(puke)", "pukeEmoticon"));
		allEmoticonsList.add(16, new Emoticon("(n)", "badEmoticon"));
		allEmoticonsList.add(17, new Emoticon("(hungry)", "hungryEmoticon"));
		allEmoticonsList.add(18, new Emoticon("(blush)", "blushEmoticon"));
		allEmoticonsList.add(19, new Emoticon("(love)", "loveEmoticon"));
		allEmoticonsList.add(20, new Emoticon("(kiss)", "kissEmoticon"));
		allEmoticonsList.add(21, new Emoticon("(pls)", "pleaseEmoticon"));
		allEmoticonsList.add(22, new Emoticon("(nod)", "nodEmoticon"));
		allEmoticonsList.add(23, new Emoticon("(shake)", "shakeEmoticon"));
		allEmoticonsList.add(24, new Emoticon("(bow)", "bowEmoticon"));
		allEmoticonsList.add(25, new Emoticon("(luck)", "luckEmoticon"));
		allEmoticonsList.add(26, new Emoticon("(idea)", "ideaEmoticon"));
		allEmoticonsList.add(27, new Emoticon("(call)", "callEmoticon"));
		allEmoticonsList.add(28, new Emoticon("(beer)", "beerEmoticon"));
		allEmoticonsList.add(29, new Emoticon("(cake)", "cakeEmoticon"));
		allEmoticonsList.add(30, new Emoticon("(music)", "musicEmoticon"));
		allEmoticonsList.add(31, new Emoticon("(dance)", "danceEmoticon"));
		allEmoticonsList.add(32, new Emoticon("(whip)", "whipEmoticon"));
		allEmoticonsList.add(33, new Emoticon("(emo)", "emoEmoticon"));
		allEmoticonsList.add(34, new Emoticon("(rock)", "rockEmoticon"));
		allEmoticonsList.add(35, new Emoticon("(rofl)", "roflEmoticon"));

		facebookEmoticonsList.add(new Emoticon(":-D", "superHappyEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":-)", "happyEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":-(", "sadEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":'(", "cryEmoticon"));
		facebookEmoticonsList.add(new Emoticon("3:)", "evilEmoticon"));
		facebookEmoticonsList.add(new Emoticon(">:-(", "angryEmoticon"));
		facebookEmoticonsList.add(new Emoticon("<3", "loveEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":-*", "kissEmoticon"));
		// keep the list in this order so it paints correctly the UI
		// these are the same emoticons with different key
		facebookEmoticonsList.add(new Emoticon(":D", "superHappyEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":)", "happyEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":(", "sadEmoticon"));
		facebookEmoticonsList.add(new Emoticon(">:(", "angryEmoticon"));
		facebookEmoticonsList.add(new Emoticon(":*", "kissEmoticon"));
		
		resourcesChat.add(0, "mail.gif");
		resourcesChat.add(1, "spacer.gif");
	}

	private EmoticonHandler() {
	}

	private static void extractResource(String path, String towrite) throws IOException {
		byte[] buffer = new byte[1024 * 25]; // 25Kb
		InputStream is = EmoticonHandler.class.getResourceAsStream(path);
		if (is != null) {
			File fileToWrite = new File(towrite);
			FileOutputStream fos = new FileOutputStream(fileToWrite);
			int r;
			while ((r = is.read(buffer, 0, buffer.length)) != -1) {
				fos.write(buffer, 0, r);
			}
			fos.close();
		}
	}

	public static void extractEmoticonsToLocalFileSystem() {
		File fileEmoticonsDir = new File(System.getProperty(USER_DIR) + EMOTICONS_IMGS_PATH);
		boolean resultCreateDirs = fileEmoticonsDir.mkdirs();
		log.debug("-->> Ok, try to create imgs Emoticons dir :" + resultCreateDirs);
		String pathname = null;
		for (Emoticon e : allEmoticonsList) {
			try {
				pathname = EMOTICONS_IMGS_PATH + e.getPath() + RESOLUTION_34x34 + GIF_EXTENSION;
				extractResource(pathname, System.getProperty(USER_DIR) + pathname);
			} catch (IOException ioex) {
				log.error(PROBLEMS_TO_EXTRACT + pathname);
			}

			try {
				pathname = EMOTICONS_IMGS_PATH + e.getPath() + RESOLUTION_34x34 + PNG_EXTENSION;
				extractResource(pathname, System.getProperty(USER_DIR) + pathname);
			} catch (IOException ioex) {
				log.error(PROBLEMS_TO_EXTRACT + pathname);
			}

			try {
				pathname = EMOTICONS_IMGS_PATH + e.getPath() + RESOLUTION_75x75 + GIF_EXTENSION;
				extractResource(pathname, System.getProperty(USER_DIR) + pathname);
			} catch (IOException ioex) {
				log.error(PROBLEMS_TO_EXTRACT + pathname);
			}
		}
		for (String resource : resourcesChat) {
			try {
				pathname = EMOTICONS_IMGS_PATH + resource;
				extractResource(pathname, System.getProperty(USER_DIR) + pathname);
			} catch (IOException ioex) {
				log.error(PROBLEMS_TO_EXTRACT + pathname);
			}
		}
	}

	public static void processMessage(ChatMessage message) {
		List<Emoticon> emoticonsList = message.getChatType() == ChatType.FACEBOOK ? facebookEmoticonsList
				: allEmoticonsList;
		String texto = message.getMessage();
		String[] words = texto.split(" ");
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			result.append(getEmoticon(word, RESOLUTION_75x75, GIF_EXTENSION, emoticonsList) + " ");
		}
		message.setMessage(result.toString());
	}

	public static String getEmoticon(String word, String resolution, String extension, List<Emoticon> emoticonsList) {

		verifyResourceDirectory();

		Emoticon emoticon = new Emoticon(word.toLowerCase(), "");
		int indexEmoticon = emoticonsList.indexOf(emoticon);
		if (indexEmoticon != -1) {

			emoticon = emoticonsList.get(indexEmoticon);

			String pathname = System.getProperty(USER_DIR) + EMOTICONS_IMGS_PATH + emoticon.getPath();

			if ((extension != null && extension.equals(".png")) || (resolution != null && resolution.equals(EXTENSION_34X34))) {
				pathname = pathname + EXTENSION_34X34 + extension;
			} else {
				pathname = pathname + EXTENSION_75X75 + extension;
			}
			String absolutePathTofile = null;
			try {
				absolutePathTofile = new File(pathname).toURI().toURL().toString();
			} catch (MalformedURLException e) {
				log.error("can't create URL to emoticon");
				return word;
			}
			StringBuilder htmlTag = new StringBuilder();

			if (resolution != null && resolution.equals(EXTENSION_34X34)) {
				htmlTag.append(IMG_SCALED_TO_34_TAG).append(absolutePathTofile).append(IMG_END_TAG);
			} else {
				htmlTag.append(IMG_TAG).append(absolutePathTofile).append(IMG_END_TAG);
			}
			log.debug("-->> tag to find : " + htmlTag);

			return htmlTag.toString();

		}
		return word;
	}

	private static void verifyResourceDirectory() {
		File fileEmoticonsDir = new File(System.getProperty(USER_DIR) + EMOTICONS_IMGS_PATH);
		if (!fileEmoticonsDir.exists()) {
			log.debug("-->> We don't have the imgs Emoticons dir.");
			extractEmoticonsToLocalFileSystem();
		}
	}

	public static String getResourceAsImageTag(String resource) {
		verifyResourceDirectory();
		int indexOf = resourcesChat.indexOf(resource);

		if (indexOf != -1) {

			String nameResource = resourcesChat.get(indexOf);
			String pathname = System.getProperty(USER_DIR) + EMOTICONS_IMGS_PATH + nameResource;
			String absolutePathTofile = null;
			try {
				absolutePathTofile = new File(pathname).toURI().toURL().toString();
			} catch (MalformedURLException e) {
				log.error("can't create URL to resource");
				return resource;
			}
			String htmlTag = IMG_TAG + absolutePathTofile + IMG_END_TAG_LEFT;
			log.debug("-->> tag to find : " + htmlTag);

			return htmlTag;
		}
		return resource;
	}
}
