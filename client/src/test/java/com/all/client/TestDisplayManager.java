package com.all.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.all.core.common.view.util.DisplayManager;


public class TestDisplayManager {
	private static Log log = LogFactory.getLog(TestDisplayManager.class);
 
	@Test
	public void shouldDetectDisplays() throws Exception {
		GraphicsDevice[] graphicDevices= DisplayManager.getGraphicDevices();
		assertNotNull(graphicDevices);
	}
	
	@Test
	public void shouldGetDisplayInsets() throws Exception {
		Insets insets= DisplayManager.getDeviceInsets(DisplayManager.MAIN_DISPLAY);
		assertNotNull(insets);
	}
	
	@Test
	public void shouldGetDisplayBounds() throws Exception {
		Rectangle bounds= DisplayManager.getMaximumDisplayBounds(DisplayManager.MAIN_DISPLAY);
		assertNotNull(bounds);
	}
	
	@Test
	public void shouldFindTheNumberOfDisplayScreens() throws Exception {
		
		GraphicsDevice[] graphicDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Rectangle rect0 = graphicDevices[0].getDefaultConfiguration().getBounds();
		Rectangle rectSecondary = DisplayManager.getMaximumDisplayBounds(DisplayManager.SECONDARY_DISPLAY);
		
		log.info("->rect0="+rect0);
		log.info("->rectSecondary="+rectSecondary);
				
		if( graphicDevices.length == 1) {
			assertEquals(rectSecondary, rect0);			
		} else {
			assertEquals(rectSecondary, graphicDevices[DisplayManager.SECONDARY_DISPLAY].getDefaultConfiguration().getBounds());									
		}
	}
	
	@Test
	public void shouldDetectWhenXScreenCoordinateIsOverAnAdditionalDisplay() throws Exception {
		Rectangle bounds= DisplayManager.getMaximumDisplayBounds(DisplayManager.MAIN_DISPLAY);
		
		boolean isOverMainDisplay= DisplayManager.belongsToMainDisplay(0);
		assertTrue(isOverMainDisplay);
		
		int arbitraryScreenXAmount = 100;
		isOverMainDisplay= DisplayManager.belongsToMainDisplay(bounds.width+arbitraryScreenXAmount);
		assertFalse(isOverMainDisplay);

	}
	
	@Test
	public void shouldGetAvailableDisplayBounds() throws Exception {
		Rectangle maximumBounds= DisplayManager.getMaximumDisplayBounds(DisplayManager.MAIN_DISPLAY);
		Rectangle availableBounds= DisplayManager.getAvailableDisplayBounds(DisplayManager.MAIN_DISPLAY);
		assertTrue(availableBounds.width<=maximumBounds.width);
		assertTrue(availableBounds.height<=maximumBounds.height);
	}
	
}
