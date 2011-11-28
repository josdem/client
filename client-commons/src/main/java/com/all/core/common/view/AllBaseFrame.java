package com.all.core.common.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.core.common.view.util.SetIconImageAll;
import com.all.core.common.view.util.WindowDraggerMouseListener;
import com.all.i18n.Internationalizable;
import com.all.i18n.Messages;

public class AllBaseFrame extends JFrame implements Internationalizable {

	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(this.getClass());

	private static final int HEIGHT_BOTTOM_PANEL = 28;
	
	private static final int HEIGHTBOTTOMRECTANTLEPANEL = 26;

	private static final int INITIAL_DELAY = 500;
	
	private static final int TOOLTIP_DELAY = 5000;

	private static final int WINDOW_CONTROLS_WIDTH = 79;
	
	private static final int WEITHBOTTOMRECTANTLEPANEL = 28;

	private static final Dimension BUTTON_DEFAULT_SIZE = new Dimension(12, 12);
	
	private static final Dimension DEFAULT_BOTTOM_PANEL_SIZE = new Dimension(224, 30);
	
	private static final Dimension DEFAULT_CONTENT_PANE_SIZE = new Dimension(224, 723);
	
	private static final Dimension DEFAULT_LAYOUT_SIZE = new Dimension(100, 100);
	
	private static final Dimension MENU_DEFAULT_SIZE = new Dimension(1024, 19);

	private static final Dimension MINIMUM_LAYOUT_SIZE = new Dimension(0, 0);
	
	private final static Dimension TOP_LEFT_RESIZER_DEFAULT_SIZE = new Dimension(2, 19);

	private final static Dimension TOP_RESIZER_DEFAULT_SIZE = new Dimension(1024, 2);
	
	private static final FlowLayout WINDOW_CONTROLS_PANEL_LAYOUT = new FlowLayout(FlowLayout.CENTER, 8, 5);
	
	private static final Point DEFAULT_LOCATION = new Point(0, 0);
	
	private static final String BOTTOM_PANEL_NAME = "bottomPanel";

	private static final String CUSTOM_CONTENT_PANE_NAME = "backgroundContentPane";

	private static final String CLOSE_WINDOW_BUTTON = "closeWindowButton";

	private static final String DEFAULT_TITLE = "Login to All";
	
	private static final String MAXIMIZE_WINDOW_BUTTON = "maximizeWindowButton";

	private static final String MINIMIZE_WINDOW_BUTTON = "minimizeWindowButton";
	
	private static final String RESIZE_PANEL_NAME = "bottomResizePanel";

	private static final String RESIZE_RECTANGLE_PANEL_NAME = "bottomProgressPanel";
	
	private static final String RESTORE_WINDOW_BUTTON = "restoreWindowButton";
	
	private final boolean isMainFrame;

	private JButton closeButton;

	private JButton maximizeButton;
	
	private JButton minimizeButton;
	
	protected JMenuBar menu;

	private JPanel contentPane;

	private JPanel customContentPane;
	
	private JPanel bottomPanel;
	
	private JPanel leftPanel;

	private JPanel resizePanel;
	
	private JPanel resizeRectanglePanel;
	
	private JPanel topLeftResizer;
	
	protected JPanel topResizer;
	
	private JPanel windowControlsPanel;

	public WindowDraggerMouseListener draggerMouseListener = new WindowDraggerMouseListener();

	public AllBaseFrame(Messages messages, boolean isMainFrame) {
		super();
		this.isMainFrame = isMainFrame;
		initialize();
		this.setMessages(messages);
	}

	@Override
	public void setJMenuBar(JMenuBar menubar) {
		if (menubar != null) {
			draggerMouseListener.setup(menubar);
		}
		super.setJMenuBar(menubar);
	}

	private void initialize() {
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		ToolTipManager.sharedInstance().setInitialDelay(INITIAL_DELAY);
		ToolTipManager.sharedInstance().setDismissDelay(TOOLTIP_DELAY);

		this.setUndecorated(true);

		this.setJMenuBar(getJJMenuBar());

		draggerMouseListener.setup(getBottomPanel());

		if (com.all.commons.Environment.isWindows()) {
			SetIconImageAll.getInstance().setIconImageAll(this);
		}

		this.setTitle(DEFAULT_TITLE);
		this.getLayeredPane().add(getWindowControlsPanel());
		this.getLayeredPane().add(getTopLeftResizer());
		this.getLayeredPane().add(getTopResizer());
		this.add(customContentPane());
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				repositionWindowsControlPanel();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				repositionWindowsControlPanel();
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				repositionWindowsControlPanel();
			}

			@Override
			public void windowActivated(WindowEvent e) {
				repositionWindowsControlPanel();
			}
		});

	}

	protected JPanel customContentPane() {

		if (customContentPane == null) {
			customContentPane = new JPanel();
			customContentPane.setName(CUSTOM_CONTENT_PANE_NAME);
			customContentPane.add(getContentPanel());
			customContentPane.add(getBottomPanel());
			customContentPane.setLayout(new LayoutManager2() {

				@Override
				public void addLayoutComponent(String name, Component comp) {
				}

				@Override
				public void layoutContainer(Container parent) {
					doResize();
				}

				@Override
				public Dimension minimumLayoutSize(Container parent) {
					return MINIMUM_LAYOUT_SIZE;
				}

				@Override
				public Dimension preferredLayoutSize(Container parent) {
					return DEFAULT_LAYOUT_SIZE;
				}

				@Override
				public void removeLayoutComponent(Component comp) {
				}

				@Override
				public void addLayoutComponent(Component comp, Object constraints) {
				}

				@Override
				public float getLayoutAlignmentX(Container target) {
					return 0;
				}

				@Override
				public float getLayoutAlignmentY(Container target) {
					return 0;
				}

				@Override
				public void invalidateLayout(Container target) {
					doResize();
				}

				@Override
				public Dimension maximumLayoutSize(Container target) {
					return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
				}
			});
		}
		return customContentPane;
	}

	@Override
	public void setBounds(Rectangle rectangle) {
		if (getState() != ICONIFIED) {
			super.setBounds(rectangle);
			repositionWindowsControlPanel();
		}
	}

	private void repositionWindowsControlPanel() {
		Rectangle rectangle = getBounds();
		windowControlsPanel.setBounds(rectangle.width - WINDOW_CONTROLS_WIDTH, 0, WINDOW_CONTROLS_WIDTH, 24);
	}

	protected void doResize() {
		int h = getContentPane().getHeight();
		int w = getContentPane().getWidth();
		contentPane.setBounds(0, 0, w, h - bottomPanel.getHeight());
		bottomPanel.setBounds(2, h - 30, w - 4, 30);
	}

	private JMenuBar getJJMenuBar() {
		if (menu == null) {
			menu = new JMenuBar();
			menu.setSize(MENU_DEFAULT_SIZE);
			menu.setPreferredSize(MENU_DEFAULT_SIZE);
		}
		return menu;
	}

	private JPanel getWindowControlsPanel() {
		windowControlsPanel = new JPanel();
		windowControlsPanel.setLayout(WINDOW_CONTROLS_PANEL_LAYOUT);
		windowControlsPanel.add(getMinimizeButton());
		windowControlsPanel.add(getMaximizeButton());
		windowControlsPanel.add(getCloseButton());
		return windowControlsPanel;
	}

	/*
	 * Overriding update is a well-known Swing technique to avoid flickering when other components are resized or moved
	 * 
	 * @see javax.swing.JFrame#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		// Keep empty, read above
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setName(CLOSE_WINDOW_BUTTON);
			closeButton.setPreferredSize(BUTTON_DEFAULT_SIZE);
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
		}
		return closeButton;
	}

	public void close() {
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public JButton getMaximizeButton() {
		if (maximizeButton == null) {
			maximizeButton = new JButton();
			maximizeButton.setName(MAXIMIZE_WINDOW_BUTTON);
			maximizeButton.setPreferredSize(BUTTON_DEFAULT_SIZE);
			maximizeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					toggleMaximize();
					log.debug("MAX" + maximizeButton.getToolTipText());
				}
			});
		}
		return maximizeButton;
	}

	protected void toggleMaximize() {
		if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
			setExtendedState(JFrame.NORMAL);
			maximizeButton.setName(MAXIMIZE_WINDOW_BUTTON);
		} else {
		   GraphicsEnvironment env =
		     GraphicsEnvironment.getLocalGraphicsEnvironment();
			setMaximizedBounds(env.getMaximumWindowBounds());
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
			maximizeButton.setName(RESTORE_WINDOW_BUTTON);
		}
		if (getExtendedState() != ICONIFIED) {
			windowControlsPanel.setLocation(getWidth() - WINDOW_CONTROLS_WIDTH, 0);
		}
	}

	public JButton getMinimizeButton() {
		if (minimizeButton == null) {
			minimizeButton = new JButton();
			minimizeButton.setName(MINIMIZE_WINDOW_BUTTON);
			minimizeButton.setPreferredSize(BUTTON_DEFAULT_SIZE);
			minimizeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					minimizeWindow();
				}

			});
		}
		return minimizeButton;
	}

	protected void minimizeWindow() {
		setState(JFrame.ICONIFIED);
	}

	protected JPanel getTopLeftResizer() {
		if (topLeftResizer == null) {
			topLeftResizer = new JPanel();
			topLeftResizer.setLocation(DEFAULT_LOCATION);
			topLeftResizer.setSize(TOP_LEFT_RESIZER_DEFAULT_SIZE);
			topLeftResizer.setPreferredSize(TOP_LEFT_RESIZER_DEFAULT_SIZE);
		}
		return topLeftResizer;
	}

	protected JPanel getTopResizer() {
		if (topResizer == null) {
			topResizer = new JPanel();
			topResizer.setLocation(DEFAULT_LOCATION);
			topResizer.setSize(TOP_RESIZER_DEFAULT_SIZE);
			topResizer.setPreferredSize(TOP_RESIZER_DEFAULT_SIZE);
		}
		return topResizer;
	}

	protected final JPanel getBottomPanel() {
		if (bottomPanel == null) {
			bottomPanel = new JPanel();
			bottomPanel.setName(BOTTOM_PANEL_NAME);
			bottomPanel.setLayout(new GridBagLayout());
			bottomPanel.setSize(DEFAULT_BOTTOM_PANEL_SIZE);
			bottomPanel.setPreferredSize(DEFAULT_BOTTOM_PANEL_SIZE);
			bottomPanel.setMinimumSize(DEFAULT_BOTTOM_PANEL_SIZE);
			GridBagConstraints leftPanelGridBagConstraints = new GridBagConstraints();
			leftPanelGridBagConstraints.gridx = 0;
			leftPanelGridBagConstraints.gridy = 0;
			leftPanelGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
			leftPanelGridBagConstraints.weightx = 1.0;
			bottomPanel.add(getBottomLeftPanel(), leftPanelGridBagConstraints);

			GridBagConstraints bottomPanelGridBagConstraints = new GridBagConstraints();
			bottomPanelGridBagConstraints.gridx = 1;
			bottomPanelGridBagConstraints.gridy = 0;
			if(isMainFrame){
				bottomPanel.add(getResizeRectanglePanel(), bottomPanelGridBagConstraints);
			}
			else{
				bottomPanel.add(getResizePanel(), bottomPanelGridBagConstraints);
			}
		}
		return bottomPanel;
	}

	protected JPanel getBottomLeftPanel() {
		if (leftPanel == null) {
			leftPanel = new JPanel();
			leftPanel.setLayout(new BorderLayout());
		}
		return leftPanel;
	}

	protected JPanel getResizePanel() {
		if (resizePanel == null) {
			resizePanel = new JPanel();
			resizePanel.setName(RESIZE_PANEL_NAME);
			resizePanel.setSize(new Dimension(HEIGHT_BOTTOM_PANEL, HEIGHT_BOTTOM_PANEL));
			resizePanel.setMinimumSize(new Dimension(HEIGHT_BOTTOM_PANEL, HEIGHT_BOTTOM_PANEL));
			resizePanel.setMaximumSize(new Dimension(HEIGHT_BOTTOM_PANEL, HEIGHT_BOTTOM_PANEL));
			resizePanel.setPreferredSize(new Dimension(HEIGHT_BOTTOM_PANEL, HEIGHT_BOTTOM_PANEL));
		}
		return resizePanel;
	}
	
    public JPanel getResizeRectanglePanel() {
    	if(resizeRectanglePanel == null){
    		resizeRectanglePanel = new JPanel();
    		resizeRectanglePanel.setSize(new Dimension(WEITHBOTTOMRECTANTLEPANEL, HEIGHTBOTTOMRECTANTLEPANEL));
    		resizeRectanglePanel.setMinimumSize(new Dimension(WEITHBOTTOMRECTANTLEPANEL, HEIGHTBOTTOMRECTANTLEPANEL));
    		resizeRectanglePanel.setMaximumSize(new Dimension(WEITHBOTTOMRECTANTLEPANEL, HEIGHTBOTTOMRECTANTLEPANEL));
    		resizeRectanglePanel.setPreferredSize(new Dimension(WEITHBOTTOMRECTANTLEPANEL, HEIGHTBOTTOMRECTANTLEPANEL));
    		resizeRectanglePanel.setName(RESIZE_RECTANGLE_PANEL_NAME);
    		resizeRectanglePanel.setLayout(new BorderLayout());
    		resizeRectanglePanel.add(getResizePanel(), BorderLayout.CENTER);
    	}
		return resizeRectanglePanel;
	}

	protected JPanel getContentPanel() {
		if (contentPane == null) {
			contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout());
			contentPane.setMinimumSize(DEFAULT_CONTENT_PANE_SIZE);
			contentPane.setMaximumSize(DEFAULT_CONTENT_PANE_SIZE);
			contentPane.setPreferredSize(DEFAULT_CONTENT_PANE_SIZE);
		}
		return contentPane;
	}

	@Override
	public void internationalize(Messages messages) {
		getCloseButton().setToolTipText(messages.getMessage("tooltip.close"));
		getMaximizeButton().setToolTipText(messages.getMessage("tooltip.maximize"));
		getMinimizeButton().setToolTipText(messages.getMessage("tooltip.minimize"));
		getResizePanel().setToolTipText(messages.getMessage("tooltip.resize"));

	}

	@Override
	public void removeMessages(Messages messages) {
		messages.remove(this);
	}

	@Override
	public void setMessages(Messages messages) {
		if (messages != null) {
			messages.add(this);
		}
	}

}