package com.all.client.data;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.client.components.MCMediaWrapper;
import com.all.client.model.InvalidFileException;
import com.all.client.model.LocalTrack;
import com.all.client.model.TrackFile;
import com.all.shared.model.Track;
import com.sun.media.jmc.MediaProvider;

public class BasicStrategy implements MetadataStrategy {

	private Log log = LogFactory.getLog(this.getClass());
	private MediaProvider player;

	@Override
	public Track createTrack(TrackFile trackFile) throws InvalidFileException {
		String absolutePath = trackFile.getFile().getAbsolutePath();
		File file = new File(absolutePath);
		try {
			LocalTrack track = new LocalTrack(trackFile.getFileName(), trackFile.getHashcode());
			track.setSize(file.length());
			track.setFileFormat(file.getName().substring(file.getName().lastIndexOf('.') + 1, file.getName().length())
					.toUpperCase());
			track.setName(file.getName().substring(0, file.getName().lastIndexOf('.')));
			MCMediaWrapper metadata = new MCMediaWrapper(trackFile.getFile());
			if (metadata.isError()) {
				throw new InvalidFileException(file, metadata.getError(), null);
			}
			// TODO: analize this because we have to create a player to read the
			// duration and also wait 400 milliseconds until we have the result
			// also we need to test with files over 72Mb
			player = new MediaProvider(file.toURI());
			Thread.sleep(750);
			track.setDuration((int) player.getDuration());
			track.setFileName(trackFile.getFile().getName());
			return track;
		} catch (Exception e) {
			log.error(e, e);
			throw new InvalidFileException(file, e);
		}
	}
}