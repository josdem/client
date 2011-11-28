package com.all.client.view.components;

public interface CellFilter<T> {
	Object filter(T value, int row, int column);
}
