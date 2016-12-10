package com.asiainfo.sh.cache.core;

import java.util.LinkedList;
import java.util.List;

public class DefaultInvalidateObservable implements InvalidateObservable {

	private List<InvalidateListener> invalidateListeners;

	public DefaultInvalidateObservable() {
		invalidateListeners = new LinkedList<InvalidateListener>();
	}

	@Override
	public synchronized void addObserver(InvalidateListener listener) {
		invalidateListeners.add(listener);
	}

	@Override
	public synchronized void deleteObserver(InvalidateListener listener) {
		invalidateListeners.remove(listener);
	}

	@Override
	public void notifyObservers(String... keys) {
		if (keys != null && !invalidateListeners.isEmpty()) {
			for (InvalidateListener listener : invalidateListeners) {
				listener.update(this, keys);
			}
		}
	}

}
