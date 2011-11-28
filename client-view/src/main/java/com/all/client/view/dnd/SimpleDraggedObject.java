package com.all.client.view.dnd;

public class SimpleDraggedObject implements DraggedObject {
	private final Object wrappedObject;

	public SimpleDraggedObject(Object wrappedObject) {
		this.wrappedObject = wrappedObject;
	}

	public Class<?> getContentClass() {
		return wrappedObject.getClass();
	}

	@Override
	public <T> T get(Class<T> clazz) {
		if (clazz == null) {
			@SuppressWarnings("unchecked")
			T wrap = (T) wrappedObject;
			return wrap;
		}
		if (is(clazz)) {
			@SuppressWarnings("unchecked")
			T wrap = (T) wrappedObject;
			return wrap;
		}
		return null;
	}

	@Override
	public Object get() {
		return wrappedObject;
	}

	@Override
	public boolean is(Class<?>... clazz) {
		return is(wrappedObject, clazz);
	}

	public static boolean is(Object object, Class<?>... clazz) {
		if (clazz == null || clazz.length == 0) {
			return object == null;
		}
		if (object != null) {
			Class<?> wrapClass = object.getClass();
			for (Class<?> targetClass : clazz) {
				if (targetClass.isAssignableFrom(wrapClass)) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

}
