package com.all.client.view.music;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.all.appControl.control.ViewEngine;
import com.all.client.util.TrackRepository;
import com.all.client.view.components.TableStyle;
import com.all.core.common.view.SynthColors;
import com.all.core.model.Model;
import com.all.shared.model.Track;

public class DescriptionTableStyle implements TableStyle {
	private boolean showCheckboxes = true;
	private boolean hideCheckboxes = false;
	private static final String FONT11_GRAY170_170_170 = "Font11Gray170_170_170";
	private static final String FONT11_PURPLE120_40_140 = "Font11Purple120_40_140";
	private static final String FONT11_GRAY77_77_77 = "Font11Gray77_77_77";
	private static final String PLAIN_PREFIX = "plain";
	private static final String BOLD_PREFIX = "bold";

	private Map<Track, Integer> tracks;

	private boolean isLocalLibrary;

	private ViewEngine viewEngine;

	public DescriptionTableStyle() {
	}

	@Override
	public Color getEvenRowColor() {
		return SynthColors.CLEAR_GRAY245_245_245;
	}

	@Override
	public Color getOddRowColor() {
		return SynthColors.WHITE255_255_255;
	}

	@Override
	public Color getSelectedRowColor() {
		return SynthColors.BLUE175_205_225;
	}

	@Override
	public Color getSelectedSeparatorColor() {
		return SynthColors.WHITE255_255_255;
	}

	public int getIndexForTrack(Track t1) {
		if (tracks == null) {
			return 0;
		}
		Integer index = tracks.get(t1);
		return index == null ? -1 : index;
	}

	public void setTracks(Iterable<Track> tracks) {
		Map<Track, Integer> mapTrack = new HashMap<Track, Integer>();
		int i = 0;
		for (Track track : tracks) {
			mapTrack.put(track, i);
			i++;
		}
		this.tracks = mapTrack;
	}

	public boolean isTrackAvailable(Track track) {
		if (viewEngine == null) {
			return true;
		}
		TrackRepository trackRepository = viewEngine.get(Model.TRACK_REPOSITORY);
		return trackRepository == null ? false : trackRepository.isLocallyAvailable(track.getHashcode());
	}

	public boolean isRemoteTrackAvailable(Track track) {
		if (viewEngine == null) {
			return false;
		}
		TrackRepository trackRepository = viewEngine.get(Model.TRACK_REPOSITORY);
		return trackRepository == null ? false : trackRepository.isRemotelyAvailable(track.getHashcode());
	}
	
	public boolean isTrackInMyLibrary(Track track) {
		if (viewEngine == null) {
			return true;
		}
		TrackRepository trackRepository = viewEngine.get(Model.TRACK_REPOSITORY);
		return trackRepository == null ? false : trackRepository.getFile(track.getHashcode()) != null;
	}

	@Override
	public Color getGridColor() {
		return SynthColors.GRAY150_150_150;
	}

	public String getAppropiateColorForTrack(Track track, boolean isSelected) {
		String name = isSelected ? BOLD_PREFIX : PLAIN_PREFIX;
		if (isTrackAvailable(track)) {
			return name += FONT11_GRAY77_77_77;
		}
		if (isRemoteTrackAvailable(track) && !isLocalLibrary) {
			return name += FONT11_PURPLE120_40_140;
		}
		return name += FONT11_GRAY170_170_170;
	}

	public void setLocalLibrary(boolean b) {
		this.isLocalLibrary = b;

	}

	public boolean getShowCheckboxes() {
		return showCheckboxes;
	}

	public boolean getHideCheckboxes() {
		return hideCheckboxes;
	}

	public void setViewEngine(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
	}

}