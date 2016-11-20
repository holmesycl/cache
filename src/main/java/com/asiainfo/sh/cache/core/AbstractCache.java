package com.asiainfo.sh.cache.core;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.asiainfo.sh.cache.core.util.Assert;

public abstract class AbstractCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

	private String name;

	public AbstractCache(String name) {
		Assert.notNull(name, "名称不能为空。");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public abstract V get(K key, Callable<? extends V> loader) throws CacheException;

	@Override
	public abstract void put(K key, V value);

	@Override
	public abstract void invalidate(K key);

}
