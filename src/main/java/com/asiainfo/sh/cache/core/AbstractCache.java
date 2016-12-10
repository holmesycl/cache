package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.exception.CacheException;
import com.asiainfo.sh.cache.core.util.Assert;

public abstract class AbstractCache<K extends Serializable, V extends Serializable> implements Cache<K, V> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private String name;

	public AbstractCache(String name) {
		Assert.notNull(name, "名称不能为空。");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * 获取当前缓存数据
	 * 
	 * @return
	 */
	protected abstract V getCacheValue(K key);

	@Override
	public V get(K key, Loader<? extends V> loader) throws CacheException {
		Assert.notNull(key, "key不能为空.");
		V v = getCacheValue(key);
		if (v == null && loader != null) {
			try {
				V value = loader.load();
				if (value != null) {
					this.put(key, value);
				}
				return value;
			} catch (Exception e) {
				log.error("loader获取数据出错.", e);
			}
		}
		return v;
	}

	@Override
	public abstract void put(K key, V value);

	@Override
	public abstract void invalidate(K key);

}
