package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.CacheManager;
import com.asiainfo.sh.cache.core.ehcache.EhCache;
import com.asiainfo.sh.cache.core.redis.Type;
import com.asiainfo.sh.cache.core.redis.ClusterTypeHolder;
import com.asiainfo.sh.cache.core.util.ObjectUtils;

import redis.clients.jedis.JedisPubSub;

/**
 * 集群订阅.
 * 
 * @author yecl
 *
 */
public class MultilvelCachePubSub extends JedisPubSub {

	private static final Logger log = LoggerFactory.getLogger(MultilvelCachePubSub.class);

	private String cluster;

	private Type type;

	private static final String INVALIDATE_CHANNEL = "invalidate";

	/**
	 * 集群全量刷新完毕时，会向该channel发布消息.消息格式：[timestamp]|[主备标志，0-主；1-备]|[ip1]:[port1],
	 * [ip2]:[port2],…[ipn]:[portn]
	 */
	private static final String REFRESH_CHANNEL_SUFFIX = "_current_cluster";

	private static final String CURRENT_CLUSTER_SPLIT = "\\|";

	private Cache<Serializable, Serializable> ehCache;

	public MultilvelCachePubSub(String cluster, Type type) {
		super();
		this.cluster = cluster;
		this.type = type;
		this.ehCache = new CacheManager<Serializable, Serializable>().getEhCache(cluster);
	}

	public MultilvelCachePubSub() {
		this("", Type.MASTER);
	}

	/**
	 * 返回数据失效channel
	 * 
	 * @return
	 */
	public static String getInvalidateChannel() {
		return INVALIDATE_CHANNEL;
	}

	/**
	 * 返回全量刷新channel
	 * 
	 * @return
	 */
	public String getRefreshChannel() {
		return cluster + REFRESH_CHANNEL_SUFFIX;
	}

	/**
	 * 返回所有的channel
	 * 
	 * @return
	 */
	public String[] channels() {
		return new String[] { getInvalidateChannel(), getRefreshChannel() };
	}

	@Override
	public void onMessage(String channel, String message) {
		log.info("[" + cluster + "_" + type.getName() + "_" + channel + "]: " + message);
		// 本地缓存失效
		if (ObjectUtils.nullSafeEquals(channel, getInvalidateChannel())) {
			ehCache.invalidate(message);
		}
		// 全量刷新
		else if (ObjectUtils.nullSafeEquals(channel, getRefreshChannel())) {
			// 切换集群
			String[] values = message.split(CURRENT_CLUSTER_SPLIT);
			// 获取主备标志
			Type type = Type.forValue(Integer.parseInt(values[1]));
			ClusterTypeHolder.put(cluster, type);
			// 本地所有缓存失效
			((EhCache<Serializable, Serializable>) ehCache).invalidateAll();
		} else {
			// 待定...
		}
	}
}
