package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.Loader;
import com.asiainfo.sh.cache.core.exception.CacheException;

public class NullLoaderValueOperations<K extends Serializable, V extends Serializable> extends ValueOperations<K, V> {

	public NullLoaderValueOperations(Cache<K, V> cache) {
		super(cache);
	}

	@Override
	public V get(K key, Loader<? extends V> loader) throws CacheException {
		return super.get(key, loader);
	}

}
