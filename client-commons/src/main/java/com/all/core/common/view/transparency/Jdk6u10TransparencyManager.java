package com.all.core.common.view.transparency;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.Window;

import javax.swing.JComponent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.all.core.common.util.MethodWrapper;

public class Jdk6u10TransparencyManager implements TransparencyManager {
	private static final Log log = LogFactory.getLog(Jdk6u10TransparencyManager.class);

	private MethodWrapper<Boolean> isTranslucencySupportedMethod;
	private MethodWrapper<Boolean> isTranslucencyCapableMethod;
	private MethodWrapper<Void> setWindowShapeMethod;
	private MethodWrapper<Void> setWindowOpacityMethod;
	private MethodWrapper<Void> setWindowOpaqueMethod;

	public Jdk6u10TransparencyManager() {
		Class<?> translucencyClass = MethodWrapper.getClass("com.sun.awt.AWTUtilities$Translucency");
		isTranslucencySupportedMethod = MethodWrapper //
				.forClass("com.sun.awt.AWTUtilities") //
				.method("isTranslucencySupported") //
				.withParameters(translucencyClass) //
				.andReturnType(boolean.class);
		isTranslucencyCapableMethod = MethodWrapper //
				.forClass("com.sun.awt.AWTUtilities") //
				.method("isTranslucencyCapable") //
				.withParameters(GraphicsConfiguration.class) //
				.andReturnType(boolean.class);
		setWindowShapeMethod = MethodWrapper //
				.forClass("com.sun.awt.AWTUtilities") //
				.method("setWindowShape") //
				.withParameters(Window.class, Shape.class) //
				.andReturnType(null);
		setWindowOpacityMethod = MethodWrapper //
				.forClass("com.sun.awt.AWTUtilities") //
				.method("setWindowOpacity") //
				.withParameters(Window.class, float.class) //
				.andReturnType(null);
		setWindowOpaqueMethod = MethodWrapper //
				.forClass("com.sun.awt.AWTUtilities") //
				.method("setWindowOpaque") //
				.withParameters(Window.class, boolean.class) //
				.andReturnType(null);
	}

	@Override
	public boolean isTranslucencySupported(Object kind) {
		return isTranslucencySupportedMethod.invoke(kind);
	}

	@Override
	public boolean isTranslucencyCapable(GraphicsConfiguration gc) {
		return isTranslucencyCapableMethod.invoke(gc);
	}

	@Override
	public void setWindowShape(Window window, Shape shape) {
		setWindowShapeMethod.invoke(window, shape);
	}

	@Override
	public void setWindowOpacity(Window window, float opacity) {
		setWindowOpacityMethod.invoke(window, opacity);
	}

	@Override
	public void setWindowOpaque(Window window, boolean opaque) {
		setWindowOpaqueMethod.invoke(window, opaque);
		doTheDoubleBuffer(window);
	}

	private void doTheDoubleBuffer(Component c) {
		if (c instanceof JComponent) {
			JComponent comp = (JComponent) c;
			comp.setDoubleBuffered(false);
		}
		if (c instanceof Container) {
			Container container = (Container) c;
			for (Component c2 : container.getComponents()) {
				doTheDoubleBuffer(c2);
			}
		}

	}

	@Override
	public GraphicsConfiguration getTranslucencyCapableGC() {
		GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
		GraphicsConfiguration translucencyCapableGC = defaultScreenDevice.getDefaultConfiguration();

		if (!isTranslucencyCapable(translucencyCapableGC)) {
			translucencyCapableGC = null;

			log.info("Default graphics configuration does not support translucency");

			GraphicsEnvironment env = localGraphicsEnvironment;
			GraphicsDevice[] devices = env.getScreenDevices();

			for (int i = 0; i < devices.length && translucencyCapableGC == null; i++) {
				GraphicsConfiguration[] configs = devices[i].getConfigurations();

				for (int j = 0; j < configs.length && translucencyCapableGC == null; j++) {
					if (isTranslucencyCapable(configs[j])) {
						translucencyCapableGC = configs[j];
					}
				}
			}
		}

		if (translucencyCapableGC == null) {
			log.warn("Translucency capable graphics configuration not found");
		}
		return translucencyCapableGC;
	}
}
