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
public class CacheManager {

	/**
	 * 返回redis缓存。
	 * 
	 * @param name
	 * @return
	 */
	public <K extends Serializable, V extends Serializable> Cache<K, V> getRedisCache(String name) {
		return new RedisCache<K, V>(name);
	}

	/**
	 * 返回ehcache缓存。
	 * 
	 * @param name
	 * @return
	 */
	public <K extends Serializable, V extends Serializable> Cache<K, V> getEhCache(String name) {
		return new EhCache<K, V>(name);
	}

	/**
	 * 返回多级（二级）缓存。
	 * 
	 * @param name
	 * @return
	 */
	public <V extends Serializable> Cache<String, V> getMultilvelCache(String name) {
		return new MultilvelCache<V>(name);
	}

}
