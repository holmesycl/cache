package com.asiainfo.sh.cache.core.ehcache;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.AbstractCache;
import com.asiainfo.sh.cache.core.util.Assert;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Ehcache缓存.
 * 
 * @author holme
 *
 * @param <K>
 * @param <V>
 */
public class EhCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

	private Cache cache;

	public EhCache(String name) {
		super(name);
		cache = CacheManager.create().getCache(getName());
	}

	@Override
	public void put(K key, V value) {
		Assert.notNull(key, "key不能为空。");
		Assert.notNull(value, "value不能为空。");
		cache.put(new Element(key, value));
	}

	@Override
	public void invalidate(K key) {
		Assert.notNull(key, "key不能为空。");
		cache.remove(key);
	}

	/**
	 * 移除所有的缓存
	 */
	public void invalidateAll() {
		cache.removeAll();
	}

	@Override
	protected V getCacheValue(K key) {
		Element element = cache.get(key);
		if (element != null) {
			@SuppressWarnings("unchecked")
			V v = (V) element.getObjectValue();
			return v;
		}
		return null;
	}

}
