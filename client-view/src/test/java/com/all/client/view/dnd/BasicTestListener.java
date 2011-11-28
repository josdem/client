package com.all.client.view.dnd;

class BasicTestListener implements DragAndDropListener {
	private final Class<?>[] clazzes;

	public BasicTestListener(Class<?>... clazzes) {
		this.clazzes = clazzes;
	}

	@Override
	public Class<?>[] handledTypes() {
		return clazzes;
	}

}