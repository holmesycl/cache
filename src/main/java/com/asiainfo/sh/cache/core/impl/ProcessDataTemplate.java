package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.CacheManager;

/**
 * 过程数据缓存
 * 
 * @author holme
 *
 */
public class ProcessDataTemplate<K extends Serializable, V extends Serializable>
		extends AbstractDataTemplate<String, V> {

	public ProcessDataTemplate(String cacheName) {
		super(cacheName);
	}

	@Override
	protected Cache<String, V> getCache() {
		return new CacheManager().getRedisCache(getCacheName());
	}

	@Override
	protected ValueOperations<String, V> getOps(Cache<String, V> cache) {
		return new DefaultValueOperations<String, V>(cache);
	}
}
