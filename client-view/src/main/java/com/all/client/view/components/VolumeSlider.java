package com.all.client.view.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import javax.swing.JSlider;

public class VolumeSlider extends JSlider {

	private static final long serialVersionUID = 1966736845763134672L;
	
	private Shape volumeBlackShape;
	private Shape volumeWhiteShape;
	private Shape volumeStrokeShape;
	private AffineTransform affineTransform;
	private double degrees;

	private float strokeFractions[] = {
			0.000f,
			1.000f
	};
	
	private Color strokeColors[] = {
			Color.white,
			new Color(0x1,0x1,0x1,1)
	};
	
	private float whiteFractions[] = {
			0.000f, 
			0.0376f,
			0.0762f,
			0.1160f,
			0.1570f,
			0.1994f,
			0.2435f,
			0.2894f,
			0.3374f,
			0.3879f,
			0.4414f,
			0.4987f,
			0.5609f,
			0.6298f,
			0.7090f,
			0.8072f,
			1.000f};

	private Color whiteColors[] = {
			new Color(0xb4,0xb3,0xb4),
			new Color(0xb8,0xb7,0xb8),
			new Color(0xbd,0xbc,0xbd),
			new Color(0xc2,0xc1,0xc2),
			new Color(0xc6,0xc6,0xc6),
			new Color(0xcb,0xca,0xcb),
			new Color(0xd0,0xcf,0xd0),
			new Color(0xd4,0xd4,0xd4),
			new Color(0xd9,0xd9,0xd9),
			new Color(0xde,0xdd,0xde),
			new Color(0xe2,0xe2,0xe2),
			new Color(0xe7,0xe7,0xe7),
			new Color(0xec,0xec,0xec),
			new Color(0xf0,0xf0,0xf0),
			new Color(0xf5,0xf5,0xf5),
			new Color(0xfa,0xfa,0xfa),
			Color.WHITE};

	private float blackFractions[] = {
			0.0060f,
			0.0719f,
			0.1361f,
			0.1995f,
			0.2625f,
			0.3250f,
			0.3872f,
			0.4493f,
			0.5110f,
			0.5726f,
			0.6340f,
			0.6953f,
			0.7565f,
			0.8175f,
			0.8785f,
			0.9392f,
			1.0000f
	};
	
	private Color blackColors[] = {
			new Color(0xb4,0xb3,0xb4),
			new Color(0xac,0xab,0xac),
			new Color(0xa4,0xa3,0xa4),
			new Color(0x9c,0x9b,0x9c),
			new Color(0x94,0x93,0x94),
			new Color(0x8c,0x8b,0x8c),
			new Color(0x84,0x83,0x84),
			new Color(0x7c,0x7b,0x7c),
			new Color(0x74,0x73,0x74),
			new Color(0x6c,0x6b,0x6c),
			new Color(0x64,0x63,0x64),
			new Color(0x5c,0x5b,0x5c),
			new Color(0x54,0x53,0x54),
			new Color(0x4c,0x4b,0x4c),
			new Color(0x44,0x43,0x44),
			new Color(0x3c,0x3b,0x3c),
			new Color(0x34,0x34,0x34)
	};
	
	private transient RadialGradientPaint blackPaint = new RadialGradientPaint(36.66f, -8.23f, 69.36f, 36.66f, -8.23f, blackFractions, blackColors, CycleMethod.REFLECT);
	private transient RadialGradientPaint whitePaint = new RadialGradientPaint(37.42f, 34.79f, 42.69f, 37.42f, 34.79f, whiteFractions, whiteColors, CycleMethod.REFLECT);
	private transient LinearGradientPaint strokePaint = new LinearGradientPaint(37.50f, 44.72f, 37.50f, -8.67f, strokeFractions, strokeColors);

	public VolumeSlider() {
		initialize();
	}

	private void initialize() {
		setMouseListeners();
		
		affineTransform = AffineTransform.getTranslateInstance(3, 2);
		volumeBlackShape = createVolumeBlackShape();
		volumeWhiteShape = createVolumeWhiteShape();
		volumeStrokeShape = createVolumeStrokeShape();
	}

	private void setMouseListeners() {
		MouseMotionAdapter mouseMotionAdapter = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				manageMouseEvent(e);
			}
		};
		this.addMouseMotionListener(mouseMotionAdapter);
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				manageMouseEvent(e);
			}
		};
		this.addMouseListener(mouseAdapter);
	}

		@Override
	public void setValue(int value) {
		degrees = 180*(maximum()-value)/maximum();
		this.setValue(value, degrees);
	}

	private void manageMouseEvent(MouseEvent e) {
		if(isEnabled()) {
			int y = height() - e.getY();
			int x = e.getX() - width() / 2;
			double degrees = calculateDegrees(y, x);
			int value = calculateValue(degrees);
			setValue(value, degrees);
		}
	}

	private int calculateValue(double degrees) {
		return (int) ((double) maximum() * (1 - degrees / 180.0));
	}

	private double calculateDegrees(int y, int x) {
		y = y<0 ? -1 : y;
		double degrees2 = Math.toDegrees(Math.atan2(y, x));
		return Math.abs(degrees2);
	}

	private void setValue(int value, double degrees) {
		this.degrees = degrees;
		super.setValue(value);
		repaint();
	}

	private int maximum() {
		return getMaximum();
	}

	private int height() {
		return this.getHeight();
	}

	private int width() {
		return this.getWidth();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//get image for the current value
		Arc2D.Double volumeArc = new Arc2D.Double(0, 0, width(), height() * 2, this.degrees, 180, Arc2D.CHORD);
		Area arcArea = new Area(volumeArc); 
		Area volumeArea = new Area(volumeWhiteShape);
		volumeArea.intersect(arcArea);
		
		//set anti alias to smooth image borders
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//draw the shadow
		g2.setColor(new Color(140,140,140, 180));
		g2.setStroke(new BasicStroke(4));
		g2.draw(volumeBlackShape);
		
		g2.setColor(new Color(140,140,140, 140));
		g2.setStroke(new BasicStroke(2));
		g2.draw(volumeBlackShape);
		
		//draw the background
		g2.setPaint(blackPaint);
		g2.fill(volumeBlackShape);
		
		//draw the current value
		g2.setPaint(whitePaint);
		g2.fill(volumeArea);
		
		//draw the stroke
		g2.setPaint(strokePaint);
		g2.fill(volumeStrokeShape);
	}

	private Shape createVolumeWhiteShape() {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(17.49,35.34);
		gp.curveTo(18.20,24.80,26.88,16.47,37.50,16.47);
		gp.curveTo(48.17,16.47,56.89,24.88,57.53,35.51);
		gp.curveTo(57.55,35.91,58.09,36.75,58.95,36.75);
		gp.curveTo(62.74,36.75,72.98,36.75,73.31,36.75);
		gp.curveTo(74.13,36.75,74.78,36.03,74.75,35.34);
		gp.curveTo(74.00,15.83,57.61,0.24,37.50,0.24);
		gp.curveTo(17.39,0.24,1.00,15.83,0.25,35.34);
		gp.curveTo(0.23,35.83,0.66,36.75,1.68,36.75);
		gp.curveTo(5.56,36.75,15.82,36.75,16.04,36.75);
		gp.curveTo(16.69,36.75,17.42,36.27,17.49,35.34);
		gp.closePath();
		gp.transform(affineTransform);
		return gp;
	}

	private Shape createVolumeStrokeShape() {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(73.31,37.00);
		gp.lineTo(58.95,37.00);
		gp.curveTo(57.92,37.00,57.31,36.02,57.28,35.52);
		gp.curveTo(56.65,24.98,47.96,16.72,37.50,16.72);
		gp.curveTo(27.12,16.72,18.44,24.91,17.73,35.36);
		gp.curveTo(17.66,36.42,16.81,37.00,16.04,37.00);
		gp.lineTo(1.68,37.00);
		gp.curveTo(1.21,37.00,0.79,36.82,0.48,36.49);
		gp.curveTo(0.11,36.10,-0.01,35.63,0.00,35.33);
		gp.curveTo(0.76,15.52,17.24,-0.00,37.50,-0.00);
		gp.curveTo(57.76,-0.00,74.23,15.52,75.00,35.33);
		gp.curveTo(75.02,35.73,74.85,36.14,74.55,36.46);
		gp.curveTo(74.22,36.80,73.77,37.00,73.31,37.00);
		gp.closePath();

		gp.moveTo(37.50,16.22);
		gp.curveTo(48.22,16.22,57.13,24.68,57.77,35.49);
		gp.curveTo(57.79,35.75,58.21,36.50,58.95,36.50);
		gp.lineTo(73.31,36.50);
		gp.curveTo(73.63,36.50,73.95,36.36,74.19,36.11);
		gp.curveTo(74.40,35.89,74.51,35.61,74.50,35.35);
		gp.curveTo(73.75,15.81,57.49,0.49,37.50,0.49);
		gp.curveTo(17.50,0.49,1.25,15.81,0.50,35.35);
		gp.curveTo(0.49,35.56,0.59,35.89,0.84,36.15);
		gp.curveTo(1.06,36.38,1.34,36.50,1.68,36.50);
		gp.lineTo(16.04,36.50);
		gp.curveTo(16.60,36.50,17.18,36.09,17.24,35.33);
		gp.curveTo(17.96,24.61,26.86,16.22,37.50,16.22);
		gp.closePath();
		gp.transform(affineTransform);
		return gp;
	}

	private Shape createVolumeBlackShape() {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(17.48,35.34);
		gp.curveTo(18.19,24.80,26.88,16.47,37.50,16.47);
		gp.curveTo(48.17,16.47,56.89,24.88,57.52,35.51);
		gp.curveTo(57.55,35.91,58.09,36.75,58.95,36.75);
		gp.curveTo(62.74,36.75,72.98,36.75,73.31,36.75);
		gp.curveTo(74.13,36.75,74.78,36.03,74.75,35.34);
		gp.curveTo(73.99,15.83,57.61,0.24,37.50,0.24);
		gp.curveTo(17.39,0.24,1.00,15.83,0.25,35.34);
		gp.curveTo(0.23,35.83,0.66,36.75,1.68,36.75);
		gp.curveTo(5.56,36.75,15.82,36.75,16.04,36.75);
		gp.curveTo(16.69,36.75,17.42,36.27,17.48,35.34);		
		gp.closePath();
		gp.transform(affineTransform);
		return gp;
	}

}
