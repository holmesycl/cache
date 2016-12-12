package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.CacheManager;

/**
 * 沉淀数据缓存
 * 
 * @author holme
 * @param <K>
 * @param <V>
 *
 */
public class PrecipitationDataTemplate<K extends Serializable, V extends Serializable>
		extends AbstractDataTemplate<String, V> {

	public PrecipitationDataTemplate(String cacheName) {
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
