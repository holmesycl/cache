package com.asiainfo.sh.cache.core.ehcache;

import java.io.Serializable;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.AbstractCache;
import com.asiainfo.sh.cache.core.CacheException;
import com.asiainfo.sh.cache.core.util.Assert;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class EhCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

	private Cache cache;

	private Logger log = LoggerFactory.getLogger(EhCache.class);

	public EhCache(String name) {
		super(name);
		cache = EhCacheManager.getInstance().getCache(getName());
	}

	@Override
	public V get(K key, Callable<? extends V> loader) throws CacheException {
		Assert.notNull(key, "key不能为空.");
		Element element = cache.get(key);
		if (element == null) {
			if (loader != null) {
				try {
					V value = loader.call();
					if (value != null) {
						this.put(key, value);
					}
					return value;
				} catch (Exception e) {
					log.error("loader获取数据出错.", e);
				}
			}
			return null;
		}
		@SuppressWarnings("unchecked")
		V v = (V) element.getObjectValue();
		return v;
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

}
