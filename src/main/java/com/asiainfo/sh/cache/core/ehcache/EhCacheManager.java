package com.asiainfo.sh.cache.core.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * 
 * @author yecl
 *
 */
public class EhCacheManager {

	private CacheManager cacheManager;

	private EhCacheManager() {
		cacheManager = CacheManager.newInstance();
	}

	private static class EhCacheManagerHolder {
		static EhCacheManager instance = new EhCacheManager();
	}

	public static EhCacheManager getInstance() {
		return EhCacheManagerHolder.instance;
	}

	public Cache getCache(String name) {
		return cacheManager.getCache(name);
	}
}
