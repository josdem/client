package com.all.core.common.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

public class ErrorStatsAppender extends AppenderSkeleton {

	private Level level = null;

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (event.getLevel().isGreaterOrEqual(getValidatedLevel())) {
			ErrorEventStatProcessor.getInstance().process(event);
		}
	}

	private Level getValidatedLevel() {
		if (level == null) {
			level = Level.ERROR;
			LogLog.warn(this.getClass().getName() + " - level not set, using default level " + level);
		}
		return level;
	}

	@Override
	public void close() {
		// nothing to do
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
		if(Level.WARN.isGreaterOrEqual(level)) {
			LogLog.warn(this.getClass().getName() + " - level set to " + level);
		}
	}

}

