package com.asiainfo.sh.cache.core.redis;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.AbstractCache;
import com.asiainfo.sh.cache.core.util.Assert;
import com.asiainfo.sh.cache.core.util.SerializationUtils;

public class RedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

	private Cluster cluster;

	public RedisCache(String name) {
		super(name);
		cluster = JedisClusterManager.getJedisCluster(name);
	}

	@Override
	public void put(K key, V value) {
		Assert.notNull(key, "key不能为空。");
		Assert.notNull(value, "value不能为空。");
		cluster.forType(ClusterTypeHolder.get(getName())).set(SerializationUtils.serialize(key),
				SerializationUtils.serialize(value));
	}

	@Override
	public void invalidate(K key) {
		Assert.notNull(key, "key不能为空。");
		cluster.forType(ClusterTypeHolder.get(getName())).del(SerializationUtils.serialize(key));
	}

	@Override
	protected V getCacheValue(K key) {
		Assert.notNull(key, "key不能为空。");
		byte[] seriValue = cluster.forType(ClusterTypeHolder.get(getName())).get(SerializationUtils.serialize(key));
		@SuppressWarnings("unchecked")
		V v = (V) SerializationUtils.deserialize(seriValue);
		return v;
	}

}
