package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.exception.CacheException;

/**
 * 缓存接口
 * 
 * @author yecl
 *
 * @param <K>
 * @param <V>
 */
public interface Cache<K extends Serializable, V extends Serializable> {

	/**
	 * 根据key从缓存中获取value.当缓存不存在值，且loader不为空时，会调用loader的call方法获取数据并载入缓存中。
	 * 
	 * @param key
	 * @param loader
	 * @return
	 * @throws CacheException
	 */
	V get(K key, Loader<? extends V> loader) throws CacheException;

	/**
	 * 
	 * @param key
	 * @param value
	 */
	void put(K key, V value);

	/**
	 * 
	 * @param key
	 */
	void invalidate(K key);

}