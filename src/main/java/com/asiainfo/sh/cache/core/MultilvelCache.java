package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.ehcache.EhCache;
import com.asiainfo.sh.cache.core.ehcache.EhCacheManager;
import com.asiainfo.sh.cache.core.redis.Cluster;
import com.asiainfo.sh.cache.core.redis.ClusterTypeHolder;
import com.asiainfo.sh.cache.core.redis.JedisClusterManager;
import com.asiainfo.sh.cache.core.util.Assert;
import com.asiainfo.sh.cache.core.util.SerializationUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class MultilvelCache<V extends Serializable> extends AbstractCache<String, V> {

	private Logger log = LoggerFactory.getLogger(EhCache.class);

	private Cache cache;

	private Cluster cluster;

	public MultilvelCache(String name) {
		super(name);
		cache = EhCacheManager.getInstance().getCache(getName());
		cluster = JedisClusterManager.getJedisCluster(getName());
	}

	@Override
	public V get(String key, Loader<? extends V> loader) throws CacheException {
		Assert.hasText(key, "key不能为空.");
		// 访问ehcache缓存
		Element element = cache.get(key);
		if (element == null) {
			// 不能获取时，访问redis
			byte[] seriValue = cluster.forType(ClusterTypeHolder.get(getName())).get(SerializationUtils.serialize(key));
			@SuppressWarnings("unchecked")
			V v = (V) SerializationUtils.deserialize(seriValue);
			if (v == null) {
				if (loader != null) {
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
				return null;
			}
			cache.put(new Element(key, v));
			return v;
		}
		@SuppressWarnings("unchecked")
		V v = (V) element.getObjectValue();
		return v;
	}

	/**
	 * 1、设置redis; 2、通知ehcache删除key.
	 */
	@Override
	public void put(String key, V value) {
		Assert.hasText(key, "key不能为空。");
		Assert.notNull(value, "value不能为空。");
		cluster.forType(ClusterTypeHolder.get(getName())).set(SerializationUtils.serialize(key),
				SerializationUtils.serialize(value));
		cluster.forType(ClusterTypeHolder.get(getName())).publish(MultilvelCachePubSub.getInvalidateChannel(), key);
	}

	/**
	 * 1、删除redis的key值; 2、通知ehcache删除key.
	 */
	@Override
	public void invalidate(String key) {
		Assert.hasText(key, "key不能为空。");
		cluster.forType(ClusterTypeHolder.get(getName())).del(SerializationUtils.serialize(key));
		cluster.forType(ClusterTypeHolder.get(getName())).publish(MultilvelCachePubSub.getInvalidateChannel(), key);
	}

}
