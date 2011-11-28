package com.all.client.view.music;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.all.client.model.Download;
import com.all.client.view.components.CellFilter;
import com.all.client.view.components.RateItems;
import com.all.client.view.components.SimpleTableRenderer;
import com.all.client.view.toolbar.hundred.HundredTrackTable;
import com.all.core.common.view.SynthFonts;
import com.all.core.common.view.SynthIcons;
import com.all.shared.model.Track;

public final class DescriptionTableRenderers {

	DescriptionTableRenderers() {
	}

	public static TableCellRenderer getAlbumRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getAlbumArtist();
					}
				});
	}

	public static TableCellRenderer getArtistRenderer(
			DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getArtist();
					}
				});
	}

	public static TableCellRenderer getBitrateRenderer(
			DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.RIGHT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getBitRateDesc();
					}
				});
	}

	public static TableCellRenderer getDateAddedRenderer(
			DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							"MM/dd/yy hh:mm aa");

					@Override
					public Object filter(Track value, int row, int column) {
						if (value.getDateAdded() == null) {
							return "";
						}
						return simpleDateFormat.format(value.getDateAdded());
					}
				});
	}

	public static TableCellRenderer getGenreRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getGenre();
					}
				});
	}

	public static TableCellRenderer getIndexRenderer(DescriptionTableStyle style) {
		return new IndexRenderer(style);
	}

	public static TableCellRenderer getKindRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getFileFormat();
					}
				});
	}

	public static TableCellRenderer getLastPlayedRenderer(
			DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							"MM/dd/yy hh:mm aa");

					@Override
					public Object filter(Track value, int row, int column) {
						if (value.getLastPlayed() == null) {
							return "";
						}
						return simpleDateFormat.format(value.getLastPlayed());
					}
				});
	}

	public static TableCellRenderer getLastSkippedRenderer(
			DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
							"MM/dd/yy hh:mm aa");

					@Override
					public Object filter(Track value, int row, int column) {
						if (value.getLastSkipped() == null) {
							return "";
						}
						return simpleDateFormat.format(value.getLastSkipped());
					}
				});
	}

	public static TableCellRenderer getPlaysRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.RIGHT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return NumberFormat.getInstance().format(
								value.getPlaycount());
					}
				});
	}

	public static TableCellRenderer getRatingRenderer(
			DescriptionTableStyle style) {
		return new RatingRenderer();
	}

	public static TableCellRenderer getSkipsRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.RIGHT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return NumberFormat.getInstance().format(
								value.getSkips());
					}
				});
	}

	public static TableCellRenderer getTimeRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.RIGHT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getDurationMinutes();
					}
				});
	}

	public static TableCellRenderer getYearRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.LEFT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getYear();
					}
				});
	}

	public static TableCellRenderer getNameRenderer(DescriptionTableStyle style) {
		return new DescriptionTableNameRenderer(style);
	}

	public static TableCellRenderer getSizeRenderer(DescriptionTableStyle style) {
		return new DescriptionTableDefaultRenderer(style, JLabel.RIGHT,
				new CellFilter<Track>() {
					@Override
					public Object filter(Track value, int row, int column) {
						return value.getFormattedSize();
					}
				});
	}
}

final class IndexRenderer implements TableCellRenderer {
	private static final String QUEUE_FOR_DOWNLOAD = "Queue for download";
	private static final String DOWNLOADING = "Downloading";
	JPanel panel = new JPanel();
	JLabel iconLabel = new JLabel();
	JLabel indexLabel = new JLabel();
	private final DescriptionTableStyle style;

	// private Log log = LogFactory.getLog(this.getClass());

	public IndexRenderer(DescriptionTableStyle style) {
		this.style = style;
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

		panel.add(iconLabel, BorderLayout.CENTER);
		panel.add(indexLabel, BorderLayout.EAST);
	}

	@Override
	public Component getTableCellRendererComponent(JTable jtable, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Track track = (Track) value;

		Track playingTrack = null;
		Download download = null;
		indexLabel.setVisible(true);
		if (jtable instanceof DescriptionTable) {
			DescriptionTable table = (DescriptionTable) jtable;
			playingTrack = table.getPlayingTrack();
			download = table.getDownload(track.getHashcode());
			indexLabel.setVisible(table.isIndexVisible());
		}else{
			HundredTrackTable table = (HundredTrackTable) jtable;
			download = table.getDownload(track.getHashcode());
		}
		indexLabel.setText("" + (row + 1));
		indexLabel.setName(style.getAppropiateColorForTrack(track, isSelected));
		if (playingTrack != null && playingTrack.equals(track)) {
			iconLabel.setIcon(SynthIcons.SPEAKER_ICON);
		} else if (download != null) {
			switch (download.getStatus()) {
			case Downloading:
				iconLabel.setIcon(SynthIcons.DOWNLOAD_ICON);
				iconLabel.setToolTipText(DOWNLOADING);
				break;
			case Error:
				iconLabel.setIcon(SynthIcons.DOWNLOAD_ERROR_ICON);
				break;
			case Complete:
				iconLabel.setIcon(SynthIcons.SPEAKER_INVISIBLE_ICON);
				break;
			default:
				iconLabel.setIcon(SynthIcons.DOWNLOAD_QUEUE_ICON);
				iconLabel.setToolTipText(QUEUE_FOR_DOWNLOAD);
				break;
			}
		} else if (track.isNewContent()) {
			iconLabel.setIcon(SynthIcons.NEW_ICON);
		} else {
			iconLabel.setIcon(SynthIcons.SPEAKER_INVISIBLE_ICON);
		}

		return panel;
	}

}

final class RatingRenderer implements TableCellRenderer {
	private static final Insets RATING_BUTTON_INSETS = new Insets(2, 24, 2, 24);

	public static final int RATE_VALUE_DOWN = 1;
	public static final int RATE_VALUE_X = 2;
	public static final int RATE_VALUE_UP = 3;
	private static final Dimension PANEL_DIMENSION = new Dimension(62, 18);
	private static final Dimension RATING_BUTTONS_SIZE = new Dimension(14, 14);

	private JButton ratingButton;
	private JPanel ratingPanel;

	public RatingRenderer() {
		ratingPanel = new JPanel();
		ratingPanel.setSize(PANEL_DIMENSION);
		ratingPanel.setMinimumSize(PANEL_DIMENSION);
		ratingPanel.setMaximumSize(PANEL_DIMENSION);
		ratingPanel.setPreferredSize(PANEL_DIMENSION);

		ratingPanel.setLayout(new GridBagLayout());

		ratingButton = new JButton();
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = RATING_BUTTON_INSETS;
		ratingButton.setSize(RATING_BUTTONS_SIZE);
		ratingButton.setPreferredSize(RATING_BUTTONS_SIZE);
		ratingButton.setMinimumSize(RATING_BUTTONS_SIZE);
		ratingButton.setMaximumSize(RATING_BUTTONS_SIZE);
		ratingPanel.add(ratingButton);

	}

	public void setRateStyle(RateItems item) {
		ratingButton.setName(item.getSynthName());
		ratingButton.setToolTipText(item.getToolTip());
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Track track = (Track) value;
		RateItems item = RateItems.getItem(track.getRating());
		setRateStyle(item);
		return ratingPanel;
	}
}

final class DescriptionTableNameRenderer implements TableCellRenderer {
	private final Dimension CHECK_SIZE = new Dimension(14, 20);
	private final DescriptionTableStyle style;
	private final JPanel panel;
	private final JLabel label;
	private final JCheckBox checkbox;

	public DescriptionTableNameRenderer(DescriptionTableStyle style) {
		this.style = style;

		label = new JLabel();
		label.setOpaque(false);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		label.setName(SynthFonts.PLAIN_FONT11_GRAY77_77_77);

		checkbox = new JCheckBox();
		checkbox.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
		checkbox.setName("checkboxRendererTable");
		checkbox.setSize(CHECK_SIZE);
		checkbox.setPreferredSize(CHECK_SIZE);
		checkbox.setMinimumSize(CHECK_SIZE);
		checkbox.setMaximumSize(CHECK_SIZE);

		panel = new JPanel(new BorderLayout());
		panel.add(checkbox, BorderLayout.WEST);
		panel.add(label, BorderLayout.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Track track = (Track) value;
		if (style.isTrackInMyLibrary(track)) {
			checkbox.setVisible(style.getShowCheckboxes());
		} else {
			checkbox.setVisible(style.getHideCheckboxes());
		}
        label.setName(style.getAppropiateColorForTrack(track, isSelected));
		label.setText(track.getName());
		checkbox.setSelected(track.isEnabled());
		return panel;
	}
}

class DescriptionTableDefaultRenderer extends SimpleTableRenderer {
	private final DescriptionTableStyle style;
	private final CellFilter<Track> filter;

	public DescriptionTableDefaultRenderer(DescriptionTableStyle style,
			int horizontalAlignment, CellFilter<Track> filter) {
		super(horizontalAlignment);
		this.style = style;
		this.filter = filter;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Track track = (Track) value;
		JLabel label = (JLabel) super.getTableCellRendererComponent(table,
				filter.filter(track, row, column), isSelected, hasFocus, row,
				column);
		label.setName(style.getAppropiateColorForTrack(track, isSelected));
		return label;
	}
}
