/**
 * 
 */
package com.all.client.view.toolbar.downloads;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.all.client.model.Download;
import com.all.client.util.Formatters;
import com.all.client.util.TimeUtil;
import com.all.client.view.components.FilteredRenderer;
import com.all.client.view.components.SimpleTableRenderer;
import com.all.client.view.components.TableColumnStyle;
import com.all.client.view.download.AvailabilityRenderer;
import com.all.client.view.download.StatusRenderer;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;

public enum DownloadTableColumns implements TableColumnStyle<DownloadTableStyle> {
	INDEX(null, 28, 28, 28, false) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return indexTrackColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return indexTrackColumnRenderer();
		}
	},
	NAME("downloadTableColumn.name.title", 230, 60, Integer.MAX_VALUE, true) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return trackNameColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return trackNameColumnRenderer();
		}
	},
	PROGRESS("downloadTableColumn.progress.title", 230, 230, 230, false) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return progressColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return new DownloadTableProgressBarCellRenderer();
		}
	},
	STATUS("downloadTableColumn.status.title", 210, 210, 210, false) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return statusColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return new StatusRenderer(style);
		}
	},
	SIZE("downloadTableColumn.size.title", 100, 60, 140, true, JLabel.RIGHT) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return sizeColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return sizeColumnRenderer();
		}
	},
	TIME("downloadTableColumn.time.title", 70, 70, 70, false, JLabel.RIGHT) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return timeColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return timeColumnRenderer();
		}
	},
	AVAILABILITY("downloadTableColumn.availability.title", 80, 80, 80, false, JLabel.CENTER) {
		@Override
		public Comparator<?> comparator(DownloadTableStyle style) {
			return availabilityColumnComparator();
		}

		@Override
		public TableCellRenderer getRenderer(DownloadTableStyle style) {
			return availabilityColumnRenderer();
		}
	};
	public final String name;
	private final int defaultWidth;
	private final int minWidth;
	private final int maxWidth;
	private final boolean resizable;
	private final int index;
	private final int alignment;

	private DownloadTableColumns(String name, int defaultWidth, int minWidth, int maxWidth, boolean resizable) {
		this(name, defaultWidth, minWidth, maxWidth, resizable, JLabel.LEFT);
	}

	private DownloadTableColumns(String name, int defaultWidth, int minWidth, int maxWidth, boolean resizable,
			int alignment) {
		this.name = name;
		this.defaultWidth = defaultWidth;
		this.minWidth = minWidth;
		this.maxWidth = maxWidth;
		this.resizable = resizable;
		this.alignment = alignment;
		this.index = ordinal();
	}

	public String label() {
		return name == null ? "" : name;
	}

	@Override
	public int defaultWidth() {
		return defaultWidth;
	}

	@Override
	public int index() {
		return index;
	}

	@Override
	public int maxWidth() {
		return maxWidth;
	}

	@Override
	public int minWidth() {
		return minWidth;
	}

	@Override
	public boolean resizable() {
		return resizable;
	}

	public int alignment() {
		return alignment;
	}

	public TableCellRenderer getRenderer(DownloadTableStyle style) {
		return new DefaultTableCellRenderer();
	}

	public Comparator<?> comparator(DownloadTableStyle style) {
		return null;
	}

	private static String timeRemainingString(Download value) {
		String time = "";
		int remainingSeconds = value.getRemainingSeconds();

		if (remainingSeconds > 0) {
			time = TimeUtil.convertSecondsToTime(remainingSeconds);
		}
		return time;
	}

	private static Comparator<?> availabilityColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				int compare = o1.getFreeNodes() - o2.getFreeNodes();
				if (compare == 0) {
					return o1.getBusyNodes() - o2.getBusyNodes();
				}
				return compare;
			}
		};
	}

	private static Comparator<?> indexTrackColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				return o1.getPriority() - o2.getPriority();
			}
		};
	}

	private static Comparator<?> trackNameColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				return o1.getDisplayName().toLowerCase().compareTo(o2.getDisplayName().toLowerCase());
			}
		};
	}

	private static Comparator<?> progressColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				return o1.getProgress() - o2.getProgress();
			}
		};
	}

	private static Comparator<?> sizeColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				return (int) (o1.getSize() - o2.getSize());
			}
		};
	}

	private static Comparator<?> statusColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				return o1.getStatus().name().compareTo(o2.getStatus().name());
			}
		};
	}

	private static Comparator<?> timeColumnComparator() {
		return new Comparator<Download>() {
			@Override
			public int compare(Download o1, Download o2) {
				return o1.getRemainingSeconds() - o2.getRemainingSeconds();
			}
		};
	}

	private static TableCellRenderer availabilityColumnRenderer() {
		return new AvailabilityRenderer();
	}

	private static TableCellRenderer timeColumnRenderer() {
		return new FilteredRenderer<Download>(new SimpleTableRenderer(JLabel.RIGHT)) {
			@Override
			public Object filter(Download value, int row, int column) {
				return timeRemainingString(value);
			}
		};
	}

	private static TableCellRenderer sizeColumnRenderer() {
		return new FilteredRenderer<Download>(new SimpleTableRenderer(JLabel.RIGHT)) {
			@Override
			public Object filter(Download value, int row, int column) {
				return Formatters.formatDataSize(value.getSize(), true);
			}
		};
	}

	private static TableCellRenderer trackNameColumnRenderer() {
		return new FilteredRenderer<Download>(new SimpleTableRenderer(JLabel.LEFT)) {
			@Override
			public Object filter(Download download, int row, int column) {
				return download.getDisplayName();
			}
		};
	}

	private static TableCellRenderer indexTrackColumnRenderer() {
		return new DownloadTableIndexRenderer();
	}
}

class DownloadTableIndexRenderer implements TableCellRenderer {
	JPanel panel = new JPanel();
	JLabel iconLabel = new JLabel();
	JLabel indexLabel = new JLabel();

	// private Log log = LogFactory.getLog(this.getClass());

	public DownloadTableIndexRenderer() {
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);

		Dimension iconSize = new Dimension(14, 20);
		iconLabel.setPreferredSize(iconSize);
		iconLabel.setSize(iconSize);
		iconLabel.setMinimumSize(iconSize);
		iconLabel.setMaximumSize(iconSize);
		iconLabel.setOpaque(false);
		iconLabel.setHorizontalAlignment(JLabel.CENTER);

		indexLabel.setHorizontalAlignment(JLabel.RIGHT);
		indexLabel.setVerticalAlignment(JLabel.CENTER);
		indexLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		indexLabel.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		indexLabel.setOpaque(false);
		indexLabel.setVisible(true);

		panel.add(iconLabel, BorderLayout.WEST);
		panel.add(indexLabel, BorderLayout.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable jtable, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		DownloadTable table = (DownloadTable) jtable;
		Download download = (Download) value;
		indexLabel.setText(Integer.toString(download.getPriority() + 1));
		indexLabel.setName(isSelected ? SynthFonts.BOLD_FONT11_GRAY77_77_77 : SynthFonts.PLAIN_FONT11_GRAY77_77_77);
		if (table.isPlaying(download)) {
			iconLabel.setIcon(SynthIcons.SPEAKER_ICON);
		} else {
			iconLabel.setIcon(SynthIcons.SPEAKER_INVISIBLE_ICON);
		}

		return panel;
	}
}