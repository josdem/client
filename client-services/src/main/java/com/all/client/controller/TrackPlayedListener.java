package com.all.client.controller;

import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.all.appControl.control.ControlEngine;
import com.all.client.components.TrackPlayedEvent;
import com.all.client.model.PlayCountCriteria;
import com.all.client.services.MusicEntityService;
import com.all.client.services.reporting.ClientReporter;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.observ.Observer;
import com.all.shared.model.Track;
import com.all.shared.stats.PlayCountStat;
import com.all.shared.stats.SkipCountStat;
import com.all.shared.stats.usage.UserActions;

@Component
final class TrackPlayedListener implements Observer<TrackPlayedEvent> {
	private Log log = LogFactory.getLog(this.getClass());
	private final MusicEntityService musicEntityService;
	private TreeMap<Integer, PlayCountCriteria> playCriteria;
	private final ClientReporter reporter;
	private final ControlEngine controlEngine;

	@Autowired
	TrackPlayedListener(ControlEngine controlEngine, MusicEntityService musicEntityService, ClientReporter reporter) {
		this.controlEngine = controlEngine;
		this.musicEntityService = musicEntityService;
		this.reporter = reporter;
	}

	@Autowired
	public void setPlayCountCriteria(@Qualifier("playCountCriteria") PlayCountCriteria playCountCriteria) {
		this.playCriteria = new TreeMap<Integer, PlayCountCriteria>(playCountCriteria.getPlayCountCriteriaMap());
	}

	@Override
	public void observe(TrackPlayedEvent eventArgs) {
		Track track = eventArgs.getTrack();
		long duration = eventArgs.getDuration();
		long netPlayedTime = eventArgs.getPlayedTime();
		if (track != null) {
			boolean canRaisePlayCount = canRaisePlayCount(duration, netPlayedTime);
			boolean canRaiseSkipCount = canRaiseSkipCount(duration, netPlayedTime);
			String email = controlEngine.get(Model.CURRENT_USER).getEmail();
			if (canRaisePlayCount) {
				musicEntityService.addPlaycount(track);
				reporter.logUserAction(UserActions.Player.PLAYCOUNT);
				reporter.log(new PlayCountStat(email, track.getHashcode(), 1));
				controlEngine.fireEvent(Events.Player.PLAYCOUNT_CHANGED, new ValueEvent<Track>(track));
				log.info("playcount added for track " + eventArgs.getTrack());
			} else if (canRaiseSkipCount) {
				musicEntityService.addSkipcount(track);
				reporter.logUserAction(UserActions.Player.SKIPCOUNT);
				reporter.log(new SkipCountStat(email, track.getHashcode(), 1));
				log.info("skipcount added for track " + eventArgs.getTrack());
			}
			if (canRaisePlayCount || canRaiseSkipCount) {
				controlEngine.fireEvent(Events.Library.TRACK_UPDATED, new ValueEvent<Track>(eventArgs.getTrack()));
			}
		}
	}

	boolean canRaisePlayCount(long duration, long netPlayedTime) {
		PlayCountCriteria percentageCriteria = findCriteria((int) (duration / 1000));
		double percentagePlayed = (double) netPlayedTime / duration * 100;
		boolean result = false;
		if (percentageCriteria != null) {
			result = percentagePlayed > percentageCriteria.porcentage;
		}
		return result;
	}

	boolean canRaiseSkipCount(long duration, long playedTime) {
		PlayCountCriteria criteria = findCriteria((int) (duration / 1000));
		if (criteria != null) {
			return criteria.minSeconds <= playedTime / 1000;
		}
		return false;
	}

	public PlayCountCriteria findCriteria(int duration) {
		for (int criteria : playCriteria.descendingKeySet()) {
			if (duration >= criteria) {
				return playCriteria.get(criteria);
			}
		}
		return null;
	}

}