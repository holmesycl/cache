package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.ehcache.EhCache;
import com.asiainfo.sh.cache.core.redis.ClusterTypeHolder;
import com.asiainfo.sh.cache.core.redis.Type;
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

	public String getCluster() {
		return cluster;
	}

	public Type getType() {
		return type;
	}

	/**
	 * 返回所有的channel
	 * 
	 * @return
	 */
	public String[] channels() {
		return new String[] { cluster, getInvalidateChannel(), getRefreshChannel() };
	}

	@Override
	public void onMessage(String channel, String message) {
		log.info("在集群[" + cluster + " | " + type.getName() + "]的channel[" + channel + "]中获取到message[" + message + "]");
		// 本地缓存失效
		if (ObjectUtils.nullSafeEquals(channel, getInvalidateChannel())) {
			log.info("本地缓存[" + cluster + "]执行key:[" + message + "]失效操作...");
			ehCache.invalidate(message);
			log.info("本地缓存[" + cluster + "]key:[" + message + "]失效成功...");
		}
		// 全量刷新
		else if (ObjectUtils.nullSafeEquals(channel, getRefreshChannel())) {
			log.error("开始执行redis主备集群切换...");
			// 切换集群
			String[] values = message.split(CURRENT_CLUSTER_SPLIT);
			// 获取主备标志
			Type type = Type.forValue(Integer.parseInt(values[1]));
			log.error("开始切换到redis的[" + type.getName() + "]集群...");
			ClusterTypeHolder.put(cluster, type);
			log.error("成功换到redis的[" + ClusterTypeHolder.get(cluster).getName() + "]集群...");
			log.error("开始清空所有的本地缓存[" + cluster + "]...");
			// 本地所有缓存失效
			((EhCache<Serializable, Serializable>) ehCache).invalidateAll();
			log.error("本地缓存[" + cluster + "]清除成功...");
		} else if (ObjectUtils.nullSafeEquals(cluster, channel)) {
			// [operFlag, operFlag=0表示更新写入redis，operFlag=1表示从redis删除]:[更新的key名]
			String key = message.substring(2);
			log.info("本地缓存[" + cluster + "]执行key:[" + key + "]失效操作...");
			ehCache.invalidate(message);
			log.info("本地缓存[" + cluster + "]key:[" + key + "]失效成功...");
		} else {
			// 待定...
		}
	}
}
