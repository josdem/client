package com.all.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.all.appControl.control.ControlEngine;
import com.all.client.devices.DevicesController;
import com.all.client.model.LocalModelDao;
import com.all.client.peer.share.ShareService;
import com.all.client.services.PortraitUtil;
import com.all.client.services.UserPreferenceService;
import com.all.client.services.UserProfileClientService;
import com.all.client.view.MainFrame;
import com.all.client.view.MiddlePanel;
import com.all.client.view.PanelFactory;
import com.all.client.view.alerts.DrawerDialog;
import com.all.client.view.chat.ChatViewManager;
import com.all.client.view.util.ViewRepository;
import com.all.core.common.services.UltrapeerProxy;
import com.all.core.common.services.reporting.Reporter;
import com.all.core.events.Events;
import com.all.core.model.Model;
import com.all.event.ValueEvent;
import com.all.messengine.MessEngine;
import com.all.shared.command.LoginCommand;
import com.all.shared.model.AllMessage;
import com.all.shared.model.User;

@SuppressWarnings("unused")
public class TestInitializer {
	@Mock
	private UserPreferenceService userPreferenceService;
	@Mock
	private ViewRepository viewState;
	@Mock
	private MainFrame mainFrame;
	@Mock
	private ShareService shareService;
	@Mock
	@Qualifier("userJdbcTemplate")
	private SimpleJdbcTemplate jdbcTemplate;
	@Mock
	private LocalModelDao dao;
	@Mock
	private UltrapeerProxy networkingInterceptor;
	@Mock
	private MessEngine messEngine;
	@Mock
	private MiddlePanel middlePanel;
	@Mock
	private DevicesController devicesController;
	@Mock
	private ChatViewManager chatViewManager;
	@Mock
	private Reporter reporter;
	@Mock
	private PanelFactory panelFactory;
	@Mock
	private ControlEngine controlEngine;
	@Mock
	private PortraitUtil portraitUtil;
	@Mock
	private UserProfileClientService profileService;
	
	@Captor
	private ArgumentCaptor<ValueEvent<User>> valueEventUserCaptor;
	@Captor
	private ArgumentCaptor<AllMessage<User>> allMesageCaptor;

	@InjectMocks
	private Initializer initializer = new Initializer();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldCreateInitializer() throws Exception {
		assertNotNull(initializer);
	}

	@Test
	public void shouldInitializeCorrectly() throws Exception {
		User user = mock(User.class);
		String sessionId = "";
		LoginCommand command = mock(LoginCommand.class);
		initializer.init(user);
		verify(controlEngine).set(Model.CURRENT_USER, user, null);
		verify(controlEngine).fireEvent(Events.Application.STARTED, new ValueEvent<User>(user));
	}

	@Test
	public void shouldShutDown() throws Exception {
		DrawerDialog drawerDialog = mock(DrawerDialog.class);
		initializer.shutdown();
		verify(controlEngine).fireEvent(eq(Events.Application.STOPED), valueEventUserCaptor.capture());
		verify(messEngine).send(allMesageCaptor.capture());
	}
}
