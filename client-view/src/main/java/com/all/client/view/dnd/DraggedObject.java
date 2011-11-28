package com.all.client.view.dnd;

public interface DraggedObject {

	public <T> T get(Class<T> clazz);

	public Object get();

	public boolean is(Class<?>... clazz);

	public Class<?> getContentClass();

	DraggedObject NULL = new DraggedObject() {
		@Override
		public <T> T get(Class<T> clazz) {
			return null;
		}

		@Override
		public Class<?> getContentClass() {
			return Void.class;
		}

		@Override
		public boolean is(Class<?>... clazz) {
			return false;
		}

		@Override
		public Object get() {
			return null;
		}

	};

}
