package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.util.Assert;

public abstract class AbstractDataTemplate<K extends Serializable, V extends Serializable> {

	private ValueOperations<K, V> ops;

	private Cache<K, V> cache;

	private String cacheName;

	protected abstract Cache<K, V> getCache();

	protected abstract ValueOperations<K, V> getOps(Cache<K, V> cache);

	public String getCacheName() {
		return cacheName;
	}

	public AbstractDataTemplate(String cacheName) {
		Assert.notNull(cacheName);
		this.cacheName = cacheName;
		this.cache = getCache();
	}

	public ValueOperations<K, V> opsForValue() {
		if (ops == null) {
			ops = getOps(this.cache);
		}
		return ops;
	}

}
