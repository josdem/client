package com.all.client.view.components;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridBagConstraintsFactory {

	public enum FillMode {
		HORIZONTAL(GridBagConstraints.HORIZONTAL), VERTICAL(GridBagConstraints.VERTICAL), NONE(GridBagConstraints.NONE), BOTH(
				GridBagConstraints.BOTH);
		private final int value;

		private FillMode(int value) {
			this.value = value;
		}
	}

	public enum Orientation {
		NORTH(GridBagConstraints.NORTH);
		private final int value;

		private Orientation(int value) {
			this.value = value;
		}
	}

	public static GridBagConstraintsFactory create() {
		return new GridBagConstraintsFactory();
	}

	private GridBagConstraints constraints = new GridBagConstraints();

	public GridBagConstraintsFactory() {
	}

	public GridBagConstraintsFactory grid(int x, int y) {
		constraints.gridx = x;
		constraints.gridy = y;
		return this;
	}

	public GridBagConstraintsFactory fill(double weightX, double weightY, FillMode fillMode) {
		constraints.weightx = weightX;
		constraints.weighty = weightY;
		constraints.fill = fillMode.value;
		return this;
	}

	public GridBagConstraintsFactory orientation(Orientation orientation) {
		constraints.anchor = orientation.value;
		return this;
	}

	public GridBagConstraintsFactory insets(int top, int left, int bottom, int right) {
		return insets(new Insets(top, left, bottom, right));
	}

	public GridBagConstraintsFactory insets(Insets insets) {
		constraints.insets = new Insets(insets.top, insets.left, insets.bottom, insets.right);
		return this;
	}

	public GridBagConstraints get() {
		return (GridBagConstraints) constraints.clone();
	}

	public GridBagConstraintsFactory span(int x, int y) {
		constraints.gridwidth = x;
		constraints.gridheight = y;
		return this;
	}
}
