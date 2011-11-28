package com.all.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SoundPlayer {

	private SoundPlayer() {
	}

	public enum Sound {
		// The FileOutputStream is case sensitive, so be careful with the file
		// names!!!
		ALERT_RECEIVED("/sounds/pulse-sound.wav"), CONTACT_DELETE("/sounds/eliminar.wav"), CONTACT_FOLDER_CLOSE(
				"/sounds/downloadFinished.wav"), CONTACT_FOLDER_OPEN("/sounds/downloadFinished.wav"), CONTACT_OFFLINE(
				"/sounds/amigo-offline.wav"), CONTACT_ONLINE("/sounds/amigo-online.wav"), DOWNLOAD_FINISHED(
				"/sounds/downloadFinished.wav"), DROP("/sounds/refresh.wav"), ITUNES_IMPORT("/sounds/refresh.wav"), LIBRARY_CLOSE_NODE(
				"/sounds/downloadFinished.wav"), LIBRARY_COLLAPSE("/sounds/collapsed-library.wav"), LIBRARY_DELETE_NODE(
				"/sounds/eliminar.wav"), LIBRARY_EXPAND("/sounds/expand-library.wav"), LIBRARY_NAME_TOO_LONG(
				"/sounds/error.wav"), LIBRARY_OPEN_NODE("/sounds/downloadFinished.wav"), LIBRARY_REMOTE_OPEN(
				"/sounds/cargar-libreria.wav"), LIBRARY_REMOTE_REFRESH("/sounds/refresh.wav"), LIBRARY_SWITCH(
				"/sounds/switch-library.wav"), LOGIN_GOODBYE("/sounds/sonido-logout.wav"), LOGIN_WELCOME("/sounds/login.wav"), LOGIN_WRONG_PASSWORD(
				"/sounds/sonido-login-error.wav"), TRACK_DELETE("/sounds/eliminar.wav"), CHAT_MESSAGE_RECEIVED(
				"/sounds/chat.wav"), OVER_9000("/sounds/over9000.wav");

		byte[] data;
		private File file;
		private final String path;

		private Sound(String path) {
			this.path = path;
		}

		@SuppressWarnings("unused")
		private File getFile() {
			if (file == null) {
				synchronized (this) {
					if (file == null) {
						try {
							File temp = File.createTempFile("sound", name());
							OutputStream output = new FileOutputStream(temp);
							InputStream resourceAsStream = SoundPlayer.class.getResourceAsStream(path);
							byte[] buffer = new byte[1024 * 100];
							int read = -1;
							while ((read = resourceAsStream.read(buffer)) > 0) {
								output.write(buffer, 0, read);
							}
							output.close();
							resourceAsStream.close();
							this.file = temp;
						} catch (IOException e) {
							log.debug(e, e);
						}
					}
				}
			}
			return file;
		}

		private InputStream getInputStream() {
			if (data == null) {
				synchronized (this) {
					if (data == null) {
						try {
							ByteArrayOutputStream output = new ByteArrayOutputStream();
							InputStream resourceAsStream = SoundPlayer.class.getResourceAsStream(path);
							byte[] buffer = new byte[1024 * 100];
							int read = -1;
							while ((read = resourceAsStream.read(buffer)) > 0) {
								output.write(buffer, 0, read);
							}
							output.close();
							resourceAsStream.close();
							this.data = output.toByteArray();
						} catch (IOException e) {
							log.debug(e, e);
						}
					}
				}
			}
			return new ByteArrayInputStream(data);
		}

		public void play() {
			SoundPlayer.playSoundClip(this);
		}
	}

	private static Object lock = new Object();
	private static Sound soundToBePlayed;
	private static Thread SoundWorker;
	private static Log log = LogFactory.getLog(SoundPlayer.class);

	static {
		SoundWorker = new Thread(new Runnable() {
			@Override
			public void run() {
				while (SoundWorker == Thread.currentThread()) {
					Sound sound = soundToBePlayed;
					if (sound != null) {
						Clip clip = null;
						try {
							AudioInputStream stream = AudioSystem.getAudioInputStream(sound.getInputStream());
							clip = AudioSystem.getClip();
							clip.open(stream);
							clip.start();
							Thread.sleep(100);
							while (clip.isRunning()) {
								Thread.sleep(100);
							}
						} catch (Exception e) {
							log.debug(e, e);
							java.awt.Toolkit.getDefaultToolkit().beep();
						} finally {
							if (clip != null) {
								clip.close();
							}
						}
						soundToBePlayed = null;
					}
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
						}
					}
				}
			}
		});
		SoundWorker.setDaemon(true);
		SoundWorker.setName("SoundPlayerWorker");
		SoundWorker.start();
	}

	private static void playSoundClip(final Sound sound) {
		synchronized (lock) {
			soundToBePlayed = sound;
			lock.notifyAll();
		}
	}
}