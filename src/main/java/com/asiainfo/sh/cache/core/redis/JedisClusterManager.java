package com.asiainfo.sh.cache.core.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.MultilvelCachePubSub;
import com.asiainfo.sh.cache.core.MultilvelCachePubSubTask;
import com.asiainfo.sh.cache.core.ThreadPoolExecutor;
import com.asiainfo.sh.cache.core.util.Assert;
import com.asiainfo.sh.cache.core.util.ObjectUtils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisClusterManager {

	private static final Logger log = LoggerFactory.getLogger(JedisClusterManager.class);

	private static final String DOT = ".";

	private static final ConcurrentMap<String, JedisCluster> clusters = new ConcurrentHashMap<String, JedisCluster>();

	private static RedisConfigurationProperties redisConfigurationProperties;

	private static JedisPoolConfig jedisPoolConfig;

	private JedisClusterManager() {
	}

	private static RedisConfigurationProperties getRedisConfigurationProperties() {
		// 初始化redis配置
		if (redisConfigurationProperties == null) {
			try {
				synchronized (JedisClusterManager.class) {
					if (redisConfigurationProperties == null) {
						redisConfigurationProperties = new RedisConfigurationProperties();
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		return redisConfigurationProperties;
	}

	/**
	 * 获取JedisPoolConfig
	 * 
	 * @return JedisPoolConfig
	 */
	private static JedisPoolConfig getJedisPoolConfig() {
		if (jedisPoolConfig == null) {
			synchronized (JedisClusterManager.class) {
				if (jedisPoolConfig == null) {
					jedisPoolConfig = new JedisPoolConfig();
					jedisPoolConfig.setMaxIdle(getRedisConfigurationProperties().getMaxIdel());
					jedisPoolConfig.setMaxTotal(getRedisConfigurationProperties().getMaxTotal());
					jedisPoolConfig.setMaxWaitMillis(getRedisConfigurationProperties().getMaxWaitMillis());
					jedisPoolConfig.setMinIdle(getRedisConfigurationProperties().getMinIdel());
				}
			}
		}
		return jedisPoolConfig;
	}

	/**
	 * 初始化特定集群的某个类型的集群
	 * 
	 * @param cluster
	 * @param type
	 */
	private static JedisCluster getJedisCluster(final String cluster, final Type type) {
		Assert.notNull(cluster, "集群名称不能为空.");
		Assert.notNull(type, "集群类型不能为空，只能是主集群或者备份集群.");
		String clusterType = cluster + DOT + type.getName();
		if (!clusters.containsKey(clusterType)) {
			Map<String, String[]> clusterNodes = null;
			if (type == Type.MASTER) {
				clusterNodes = getRedisConfigurationProperties().getMasterClusterNodes();
			} else if (type == Type.BACKUP) {
				clusterNodes = getRedisConfigurationProperties().getBackupClusterNodes();
			}
			Assert.notNull(clusterNodes);
			for (Map.Entry<String, String[]> entry : clusterNodes.entrySet()) {
				final String _cluster = entry.getKey();
				Assert.hasText(_cluster);
				if (ObjectUtils.nullSafeEquals(cluster, _cluster)) {
					String[] nodes = entry.getValue();
					Assert.notEmpty(nodes);
					Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
					for (String node : nodes) {
						HostAndPort hostAndPort = parseString(node);
						hostAndPorts.add(hostAndPort);
					}
					final JedisCluster jedisCluster = new JedisCluster(hostAndPorts, getJedisPoolConfig());
					Assert.notNull(jedisCluster);
					// 判断当前集群是否可用
					try {
						jedisCluster.setex("0", 1, "0");
						// 启动集群广播监控
						// 获取当前集群监控和支持的channels
						ThreadPoolExecutor.submit(
								new MultilvelCachePubSubTask(jedisCluster, new MultilvelCachePubSub(_cluster, type)));
						clusters.put(clusterType, jedisCluster);
					} catch (Exception e) {
						log.info("集群[" + _cluster + " | " + type.getName() + "]不可用...");
					}
				}
			}
		}
		return clusters.get(clusterType);
	}

	/**
	 * 新建JedisCluster.
	 * 
	 * @param cluster
	 * @return
	 */
	public static Cluster getJedisCluster(final String cluster) {
		return new Cluster(getJedisCluster(cluster, Type.MASTER), getJedisCluster(cluster, Type.BACKUP));
	}

	/**
	 * Splits String into host and port parts. String must be in ( host + ":" +
	 * port ) format. Port is optional
	 * 
	 * @param from
	 *            String to parse
	 * @return array of host and port strings
	 */
	public static String[] extractParts(String from) {
		int idx = from.lastIndexOf(":");
		String host = idx != -1 ? from.substring(0, idx) : from;
		String port = idx != -1 ? from.substring(idx + 1) : "";
		return new String[] { host, port };
	}

	/**
	 * Creates HostAndPort instance from string. String must be in ( host + ":"
	 * + port ) format. Port is mandatory. Can convert host part.
	 * 
	 * @see #convertHost(String)
	 * @param from
	 *            String to parse
	 * @return HostAndPort instance
	 */
	public static HostAndPort parseString(String from) {
		// NOTE: redis answers with
		// '99aa9999aa9a99aa099aaa990aa99a09aa9a9999 9a09:9a9:a090:9a::99a slave
		// 8c88888888cc08088cc8c8c888c88c8888c88cc8 0 1468251272993 37
		// connected'
		// for CLUSTER NODES, ASK and MOVED scenarios. That's why there is no
		// possibility to parse address in 'correct' way.
		// Redis should switch to 'bracketized' (RFC 3986) IPv6 address.
		try {
			String[] parts = extractParts(from);
			String host = parts[0];
			int port = Integer.valueOf(parts[1]);
			return new HostAndPort(convertHost(host), port);
		} catch (NumberFormatException ex) {
			log.error(ex.getMessage(), ex);
			throw new IllegalArgumentException(ex);
		}
	}

	public static String convertHost(String host) {
		if (host.equals("127.0.0.1") || host.startsWith("localhost") || host.equals("0.0.0.0")
				|| host.startsWith("169.254") || host.startsWith("::1") || host.startsWith("0:0:0:0:0:0:0:1")) {
			return HostAndPort.LOCALHOST_STR;
		} else {
			return host;
		}
	}

}
