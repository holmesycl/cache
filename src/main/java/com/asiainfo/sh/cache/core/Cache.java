package com.asiainfo.sh.cache.core;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface Cache<K extends Serializable, V extends Serializable> {

	V get(K key, Callable<? extends V> loader) throws CacheException;

	void put(K key, V value);

	void invalidate(K key);

}