package com.all.client.view.music;

import java.util.Comparator;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.all.shared.model.Track;

public final class DescriptionTableComparators {
	
//	private final static Log log = LogFactory.getLog(DescriptionTableComparators.class);
		
	private DescriptionTableComparators() {
	}
	
	public static Comparator<?> getAlbumComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareString(t1.getAlbumArtist(), t2.getAlbumArtist());
			}
		};
	}

	public static Comparator<?> getArtistComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareString(t1.getArtist(), t2.getArtist());
			}
		};
	}

	public static Comparator<?> getBitrateComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				try {
					int int1 = Integer.parseInt(t1.getBitRate());
					int int2 = Integer.parseInt(t2.getBitRate());
					return compareLong(int1, int2);
				} catch (NumberFormatException e) {
					return compareString(t1.getBitRate(), t2.getBitRate());
				}
			}
		};
	}

	public static Comparator<?> getDateAddedComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareDate(t1.getDateAdded(), t2.getDateAdded());
			}
		};
	}

	public static Comparator<?> getGenreComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareString(t1.getGenre(), t2.getGenre());
			}
		};
	}

	public static Comparator<?> getIndexComparator(final DescriptionTableStyle style) {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return style.getIndexForTrack(t1) - style.getIndexForTrack(t2);
			}
		};
	}

	public static Comparator<?> getKindComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareString(t1.getFileFormat(), t2.getFileFormat());
			}
		};
	}

	public static Comparator<?> getLastPlayedComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareDate(t1.getLastPlayed(), t2.getLastPlayed());
			}
		};
	}

	public static Comparator<?> getLastSkippedComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareDate(t1.getLastSkipped(), t2.getLastSkipped());
			}
		};
	}

	public static Comparator<?> getPlaysComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return t1.getPlaycount() - t2.getPlaycount();
			}
		};
	}

	public static Comparator<?> getRatingComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return t1.getRating() - t2.getRating();
			}
		};
	}

	public static Comparator<?> getSkipsComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return t1.getSkips() - t2.getSkips();
			}
		};
	}

	public static Comparator<?> getTimeComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareLong(t1.getDuration(), t2.getDuration());
			}
		};
	}

	public static Comparator<?> getYearComparator() {
		return new Comparator<Track>() {
			@Override
			public int compare(Track t1, Track t2) {
				return compareString(t1.getYear(), t2.getYear());
			}
		};
	}

	public static Comparator<?> getSizeComparator() {
		return new Comparator<Track>() {
			public int compare(Track t1, Track t2) {
				return compareLong(t1.getSize(), t2.getSize());
			}
		};
	}

	public static Comparator<?> getNameComparator() {
		return new Comparator<Track>() {
			public int compare(Track t1, Track t2) {
				return compareString(t1.getName(), t2.getName());
			}
		};
	}

	public static Comparator<?> getDefaultComparator() {
		return new Comparator<Object>() {
			public int compare(Object t1, Object t2) {
				return compareString(t1.toString(), t2.toString());
			}
		};
	}

	public static int compareLong(long l1, long l2) {
		if (l1 > l2) {
			return 1;
		}
		if (l2 > l1) {
			return -1;
		}
		return 0;
	}

	public static int compareString(String s1, String s2) {
		if (!StringUtils.hasText(s1) && !StringUtils.hasText(s2)) {
			return 0;
		}
		if (!StringUtils.hasText(s1)) {
			return 1;
		}
		if (!StringUtils.hasText(s2)) {
			return -1;
		}

		return s1.compareToIgnoreCase(s2);
	}

	public static int compareDate(Date s1, Date s2) {
		if (s1 == null && s2 == null) {
			return 0;
		}
		if (s1 == null) {
			return 1;
		}
		if (s2 == null) {
			return -1;
		}
		return s1.compareTo(s2);
	}

}
