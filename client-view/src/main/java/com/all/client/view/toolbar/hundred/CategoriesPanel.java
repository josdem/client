package com.all.client.view.toolbar.hundred;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.action.ResponseCallback;
import com.all.action.SwingResponseCallback;
import com.all.appControl.control.ViewEngine;
import com.all.client.view.View;
import com.all.core.actions.Actions;
import com.all.core.common.view.SynthFonts;
import com.all.core.events.Events;
import com.all.core.model.ModelContainerView;
import com.all.core.model.TopHundredModelContainer;
import com.all.event.EventListener;
import com.all.event.Listener;
import com.all.event.ValueEvent;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;
import com.all.shared.model.Category;
import com.all.shared.model.Playlist;

public final class CategoriesPanel extends JPanel implements Internationalizable, View {

	private static final long serialVersionUID = 1L;

	private final static Log log = LogFactory.getLog(CategoriesPanel.class);

	private static final Dimension CATEGORIES_SCROLLPANE_DEFAULT_SIZE = new Dimension(296, 160);

	private static final Dimension CATEGORIES_SCROLLPANE_MINIMUM_SIZE = new Dimension(195, 160);

	private static final Dimension CATEGORIES_BOTTOM_PANEL_MINIMUM_SIZE = new Dimension(195, 16);

	private static final Dimension CATEGORIES_BOTTOM_PANEL_DEFAULT_SIZE = new Dimension(296, 16);

	private static final Dimension CATEGORIES_TOP_PANEL_MINIMUM_SIZE = new Dimension(195, 24);

	private static final Dimension CATEGORIES_TOP_PANEL_DEFAULT_SIZE = new Dimension(296, 24);

	private static final Dimension CATEGORIES_TITLE_PANEL_DEFAULT_SIZE = new Dimension(296, 20);

	private static final Dimension CATEGORIES_TITLE_PANEL_MINIMUM_SIZE = new Dimension(195, 20);

	private static final Dimension DEFAULT_SIZE = new Dimension(296, 222);

	private static final Dimension MINIMUM_SIZE = new Dimension(195, 222);

	private static final Insets TITLE_LABEL_INSETS = new Insets(0, 10, 0, 0);

	private static final Insets INSTRUCTIONS_LABEL_INSETS = new Insets(0, 15, 0, 0);

	private static final String CATEGORIES_CONTAINER_PANEL_NAME = "hundredContainerBackgroundPanel";

	private static final String CATEGORIES_LIST_NAME = "categoriesList";

	private static final String NAME = "hundredBackgroundPanel";

	private JPanel categoriesContainerPanel;

	private JPanel categoriesTitlePanel;

	private JPanel categoriesTopPanel;

	private JPanel categoriesBottomPanel;

	private JScrollPane categoriesScrollPane;

	private JList categoriesViewList;

	private JLabel titleLabel;

	private JLabel instructionsLabel;

	private ViewEngine viewEngine;

	private PlaylistPanel playlistPanel;

	private final JPanel loaderPanel;

	private final HundredTablePanel tablePanel;

	private final HundredModelSourceProvider sourceProvider;

	private Listener<ValueEvent<ModelContainerView>> selectCategorieListener;
	private Long currentCategoryId;
	private List<Category> categories;
	private boolean selectFeedCategory = false ;
	
	private TopHundredModelContainer modelContainerView;

	public CategoriesPanel(JPanel loaderPanel, HundredTablePanel tablePanel, HundredModelSourceProvider sourceProvider) {
		this.loaderPanel = loaderPanel;
		this.tablePanel = tablePanel;
		this.sourceProvider = sourceProvider;
		selectCategorieListener = new EventListener<ValueEvent<ModelContainerView>>() {

			public void handleEvent(ValueEvent<ModelContainerView> eventArgs) {
				modelContainerView = (TopHundredModelContainer) eventArgs.getValue();
				if (modelContainerView != null) {
					if (modelContainerView.getPlaylistHash() != null) {
						selectFeedCategory = true;
						viewEngine.sendValueAction(Actions.View.SET_TOP_HUNDRED_PLAYLIST_VIEW, modelContainerView);
					}
					doSelectCategory(modelContainerView);
				}
			}

			private void doSelectCategory(TopHundredModelContainer modelContainerView) {
				currentCategoryId = modelContainerView.getCategoryId();
				for (Iterator<Category> iterator = categories.iterator(); iterator.hasNext();) {
					Category category = iterator.next();
					if (category.getId() == currentCategoryId) {
						categoriesViewList.setSelectedValue(category, true);
					}
				}
			}
		};
	}

	private void setup() {
		viewEngine.request(Actions.TopHundred.getCategories, null, new ResponseCallback<List<Category>>() {

			@Override
			public void onResponse(List<Category> categoryList) {
				categories = categoryList;
				DefaultListModel model = new DefaultListModel();
				categoriesViewList.setModel(model);
				for (Category category : categoryList) {
					model.addElement(category);
				}
				categoriesViewList.setSelectedIndex(0);
			}
		});
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setMinimumSize(MINIMUM_SIZE);
		this.setPreferredSize(DEFAULT_SIZE);
		this.add(getCategoriesTitlePanel(), BorderLayout.NORTH);
		this.add(getCategoriesContainerPanel(), BorderLayout.CENTER);
		this.setName(NAME);
	}

	private JPanel getCategoriesTitlePanel() {
		if (categoriesTitlePanel == null) {
			categoriesTitlePanel = new JPanel();
			categoriesTitlePanel.setMinimumSize(CATEGORIES_TITLE_PANEL_MINIMUM_SIZE);
			categoriesTitlePanel.setPreferredSize(CATEGORIES_TITLE_PANEL_DEFAULT_SIZE);
			categoriesTitlePanel.setLayout(new GridBagLayout());
			GridBagConstraints titleConstraints = new GridBagConstraints();
			titleConstraints.gridx = 0;
			titleConstraints.gridy = 0;
			titleConstraints.weightx = 1;
			titleConstraints.weighty = 1;
			titleConstraints.fill = GridBagConstraints.BOTH;
			titleConstraints.insets = TITLE_LABEL_INSETS;
			categoriesTitlePanel.add(getTitleLabel(), titleConstraints);
		}
		return categoriesTitlePanel;
	}

	private JPanel getCategoriesContainerPanel() {
		if (categoriesContainerPanel == null) {
			categoriesContainerPanel = new JPanel();
			categoriesContainerPanel.setLayout(new BorderLayout());
			categoriesContainerPanel.add(getCategoriesTopPanel(), BorderLayout.NORTH);
			categoriesContainerPanel.add(getCategoriesScrollPane(), BorderLayout.CENTER);
			categoriesContainerPanel.add(getCategoriesBottomPanel(), BorderLayout.SOUTH);
			categoriesContainerPanel.setName(CATEGORIES_CONTAINER_PANEL_NAME);
			categoriesContainerPanel.setMinimumSize(MINIMUM_SIZE);
			categoriesContainerPanel.setPreferredSize(DEFAULT_SIZE);
		}
		return categoriesContainerPanel;
	}

	private JPanel getCategoriesTopPanel() {
		if (categoriesTopPanel == null) {
			categoriesTopPanel = new JPanel();
			categoriesTopPanel.setPreferredSize(CATEGORIES_TOP_PANEL_DEFAULT_SIZE);
			categoriesTopPanel.setMinimumSize(CATEGORIES_TOP_PANEL_MINIMUM_SIZE);
			categoriesTopPanel.setLayout(new GridBagLayout());
			GridBagConstraints instructionsConstraints = new GridBagConstraints();
			instructionsConstraints.gridx = 0;
			instructionsConstraints.gridy = 0;
			instructionsConstraints.weightx = 1;
			instructionsConstraints.weighty = 1;
			instructionsConstraints.fill = GridBagConstraints.BOTH;
			instructionsConstraints.insets = INSTRUCTIONS_LABEL_INSETS;
			categoriesTopPanel.add(getInstructionsLabel(), instructionsConstraints);
		}
		return categoriesTopPanel;
	}

	private JLabel getInstructionsLabel() {
		if (instructionsLabel == null) {
			instructionsLabel = new JLabel();
			instructionsLabel.setName(SynthFonts.BOLD_FONT12_GRAY64_64_64);
		}
		return instructionsLabel;
	}

	private JLabel getTitleLabel() {
		if (titleLabel == null) {
			titleLabel = new JLabel();
			titleLabel.setName(SynthFonts.BOLD_FONT11_GRAY64_64_64);
		}
		return titleLabel;
	}

	private JPanel getCategoriesBottomPanel() {
		if (categoriesBottomPanel == null) {
			categoriesBottomPanel = new JPanel();
			categoriesBottomPanel.setPreferredSize(CATEGORIES_BOTTOM_PANEL_DEFAULT_SIZE);
			categoriesBottomPanel.setMinimumSize(CATEGORIES_BOTTOM_PANEL_MINIMUM_SIZE);
		}
		return categoriesBottomPanel;
	}

	private JScrollPane getCategoriesScrollPane() {
		if (categoriesScrollPane == null) {
			categoriesScrollPane = new JScrollPane();
			categoriesScrollPane.setViewportView(getCategoriesList());
			categoriesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			categoriesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			categoriesScrollPane.setMinimumSize(CATEGORIES_SCROLLPANE_MINIMUM_SIZE);
			categoriesScrollPane.setPreferredSize(CATEGORIES_SCROLLPANE_DEFAULT_SIZE);
		}
		return categoriesScrollPane;
	}

	private JList getCategoriesList() {
		if (categoriesViewList == null) {
			categoriesViewList = new JList();
			categoriesViewList.setName(CATEGORIES_LIST_NAME);
			categoriesViewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			categoriesViewList.setCellRenderer(new DefaultListCellRenderer() {
				private static final long serialVersionUID = -3940735057037889407L;

				@Override
				public Component getListCellRendererComponent(JList paramJList, Object paramObject, int paramInt,
						boolean paramBoolean1, boolean paramBoolean2) {
					JComponent component = (JComponent) super.getListCellRendererComponent(paramJList, paramObject,
							paramInt, paramBoolean1, paramBoolean2);
					component.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
					return component;
				}
			});

			categoriesViewList.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) {
						return;
					}
					final Category category = (Category) categoriesViewList.getSelectedValue();
					selectCategory(category);
				}
			});
		}
		return categoriesViewList;
	}

	private void selectCategory(Category category) {
		loaderPanel.setVisible(true);
		log.info("CATEGORY SELECTED: " + category);
		sourceProvider.setCategory(category);
		viewEngine.request(Actions.TopHundred.getPlaylistsFromCategoryAsync, category,
				new SwingResponseCallback<List<Playlist>>() {
					@Override
					public void updateGui(List<Playlist> t) {
						try {
							
							playlistPanel.setPlaylists(t);
							tablePanel.cleanPanel();
							if(selectFeedCategory == true){
								selectFeedCategory = false;
								viewEngine.sendValueAction(Actions.View.SET_TOP_HUNDRED_PLAYLIST_VIEW, modelContainerView);
							}
							
						} finally {
							loaderPanel.setVisible(false);
						}
					}
				});
		
		
		
	}

	@Override
	public void internationalize(Messages messages) {
		titleLabel.setText(messages.getMessage("hundred.categories.list.title"));
		instructionsLabel.setText(messages.getMessage("hundred.categories.list.instructions"));
	}

	@Override
	public void setMessages(Messages messages) {
		messages.add(this);
	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	public void setPlaylistPanel(PlaylistPanel playlistPanel) {
		this.playlistPanel = playlistPanel;
	}

	@Override
	public void destroy(ViewEngine viewEngine) {
		viewEngine.removeListener(Events.View.TOP_HUNDRED_CATEGORY_MODEL_SELECTION, selectCategorieListener);
	}

	@Override
	public void initialize(ViewEngine viewEngine) {
		this.viewEngine = viewEngine;
		initialize();
		setup();
		viewEngine.addListener(Events.View.TOP_HUNDRED_CATEGORY_MODEL_SELECTION, selectCategorieListener);
	}
}
