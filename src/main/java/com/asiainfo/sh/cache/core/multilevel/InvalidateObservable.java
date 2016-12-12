package com.asiainfo.sh.cache.core.multilevel;

public interface InvalidateObservable {

	public void addObserver(InvalidateListener listener);

	public void deleteObserver(InvalidateListener listener);

	public void notifyObservers(String... keys);

}
