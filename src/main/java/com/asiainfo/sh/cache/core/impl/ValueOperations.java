package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.Loader;
import com.asiainfo.sh.cache.core.exception.CacheException;

public abstract class ValueOperations<K extends Serializable, V extends Serializable> {

	private Cache<K, V> cache;

	public ValueOperations(Cache<K, V> cache) {
		this.cache = cache;
	}

	public V get(K key, Loader<? extends V> loader) throws CacheException {
		return cache.get(key, loader);
	}

	public V get(K key) throws CacheException {
		return get(key, null);
	}

	public void put(K key, V value) throws CacheException {
		cache.put(key, value);
	}

	public void invalidate(K key) throws CacheException {
		cache.invalidate(key);
	}

}
