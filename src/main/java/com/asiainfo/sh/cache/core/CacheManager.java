package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.ehcache.EhCache;
import com.asiainfo.sh.cache.core.redis.RedisCache;

/**
 * 缓存管理器
 * 
 * @author holme
 *
 * @param <K>
 * @param <V>
 */
public class CacheManager<K extends Serializable, V extends Serializable> {

	private Cache<K, V> redisCache, ehCache;

	private Cache<String, V> multivelCache;

	/**
	 * 返回redis缓存。
	 * 
	 * @param name
	 * @return
	 */
	public Cache<K, V> getRedisCache(String name) {
		redisCache = new RedisCache<K, V>(name);
		return redisCache;
	}

	/**
	 * 返回ehcache缓存。
	 * 
	 * @param name
	 * @return
	 */
	public Cache<K, V> getEhCache(String name) {
		ehCache = new EhCache<K, V>(name);
		return ehCache;
	}

	/**
	 * 返回多级（二级）缓存。
	 * 
	 * @param name
	 * @return
	 */
	public Cache<String, V> getMultilvelCache(String name) {
		multivelCache = new MultilvelCache<V>(name);
		return multivelCache;
	}

}
