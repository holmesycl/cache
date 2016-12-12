package com.asiainfo.sh.cache.core.multilevel;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.AbstractCache;
import com.asiainfo.sh.cache.core.Loader;
import com.asiainfo.sh.cache.core.ehcache.EhCache;
import com.asiainfo.sh.cache.core.exception.LoadException;
import com.asiainfo.sh.cache.core.redis.Cluster;
import com.asiainfo.sh.cache.core.redis.ClusterTypeHolder;
import com.asiainfo.sh.cache.core.redis.JedisClusterManager;
import com.asiainfo.sh.cache.core.util.Assert;
import com.asiainfo.sh.cache.core.util.SerializationUtils;

public class MultilevelCache<V extends Serializable> extends AbstractCache<String, V> {

	private EhCache<String, V> cache;

	private Cluster cluster;

	private InvalidateObservable invalidateObservable;

	public MultilevelCache(String name) {
		super(name);
		cache = new EhCache<String, V>(getName());
		cluster = JedisClusterManager.getJedisCluster(getName());
		invalidateObservable = new DefaultInvalidateObservable();
		invalidateObservable.addObserver(new DefaultInvalidateListener(cluster));
	}

	public InvalidateObservable getInvalidateObservable() {
		return invalidateObservable;
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
		invalidateObservable.notifyObservers(key);
	}

	/**
	 * 1、删除redis的key值; 2、通知ehcache删除key.
	 */
	@Override
	public void invalidate(String key) {
		Assert.hasText(key, "key不能为空。");
		cluster.forType(ClusterTypeHolder.get(getName())).del(SerializationUtils.serialize(key));
		invalidateObservable.notifyObservers(key);
	}

	@Override
	protected V getCacheValue(final String key) {
		Assert.hasText(key, "key不能为空.");
		// 访问ehcache
		V v = cache.get(key, new Loader<V>() {

			@Override
			public V load() throws LoadException {
				// 访问redis
				byte[] seriValue = cluster.forType(ClusterTypeHolder.get(getName()))
						.get(SerializationUtils.serialize(key));
				@SuppressWarnings("unchecked")
				V v = (V) SerializationUtils.deserialize(seriValue);
				return v;
			}
		});
		return v;
	}

}
