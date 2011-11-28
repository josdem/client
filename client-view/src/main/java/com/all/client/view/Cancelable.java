package com.all.client.view;

public interface Cancelable {
	 Cancelable EMPTY = new Cancelable() {
		@Override
		public void cancel() {
		}
	};

	void cancel();
}
