package com.asiainfo.sh.cache.core.redis;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.util.Assert;
import com.asiainfo.sh.cache.core.util.ClassUtils;

/**
 * 
 * @author yecl
 * 
 */
public final class RedisConfigurationProperties {

	private static final Logger log = LoggerFactory.getLogger(RedisConfigurationProperties.class);

	/**
	 * 主集群节点后缀
	 */
	private static final String MASTER_NODES = "master.nodes";

	/**
	 * 备份集群节点后缀
	 */
	private static final String BACKUP_NODES = "backup.nodes";

	private Set<String> clusters;

	private Map<String, String[]> masterClusterNodes;

	private Map<String, String[]> backupClusterNodes;

	private static final String REDIS_CLUSTER = "redis.cluster";

	private static final String REDIS_CFG_NAME = "redis.properties";

	private static final String MAX_IDEL = "redis.pool.maxIdel";
	private int maxIdel;

	private static final String MAX_TOTAL = "redis.pool.maxTotal";
	private int maxTotal;

	private static final String MAX_WAIT_MILLIS = "redis.pool.maxWaitMillis";
	private long maxWaitMillis;

	private static final String MIN_IDEL = "redis.pool.minIdel";
	private int minIdel;

	private static final String EXTENSION_SEPARATOR = ",";

	private static final String DOT = ".";

	/**
	 * 初始化.
	 * 
	 * @throws RuntimeException
	 */
	private void init() {
		Properties redisProperties = new Properties();
		InputStream inStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(REDIS_CFG_NAME);
		try {
			redisProperties.load(inStream);
			for (Map.Entry<Object, Object> entry : redisProperties.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				Assert.notNull(value);
				if (key.startsWith(REDIS_CLUSTER)) {
					for (String cluster : value.split(EXTENSION_SEPARATOR)) {
						clusters.add(cluster);
					}
				} else if (key.endsWith(MASTER_NODES)) {
					String cluster = key.substring(0, key.indexOf(DOT));
					masterClusterNodes.put(cluster, value.split(EXTENSION_SEPARATOR));
				} else if (key.endsWith(BACKUP_NODES)) {
					String cluster = key.substring(0, key.indexOf(DOT));
					backupClusterNodes.put(cluster, value.split(EXTENSION_SEPARATOR));
				} else if (key.startsWith(MAX_IDEL)) {
					this.maxIdel = Integer.parseInt(value);
				} else if (key.startsWith(MAX_TOTAL)) {
					this.maxTotal = Integer.parseInt(value);
				} else if (key.startsWith(MAX_WAIT_MILLIS)) {
					this.maxWaitMillis = Long.parseLong(value);
				} else if (key.startsWith(MIN_IDEL)) {
					this.minIdel = Integer.parseInt(value);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public RedisConfigurationProperties() {
		clusters = new HashSet<String>();
		masterClusterNodes = new HashMap<String, String[]>();
		backupClusterNodes = new HashMap<String, String[]>();
		init();
	}

	public Map<String, String[]> getMasterClusterNodes() {
		return masterClusterNodes;
	}

	public void setMasterClusterNodes(Map<String, String[]> masterClusterNodes) {
		this.masterClusterNodes = masterClusterNodes;
	}

	public Map<String, String[]> getBackupClusterNodes() {
		return backupClusterNodes;
	}

	public void setBackupClusterNodes(Map<String, String[]> backupClusterNodes) {
		this.backupClusterNodes = backupClusterNodes;
	}

	public int getMaxIdel() {
		return maxIdel;
	}

	public void setMaxIdel(int maxIdel) {
		this.maxIdel = maxIdel;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public long getMaxWaitMillis() {
		return maxWaitMillis;
	}

	public void setMaxWaitMillis(long maxWaitMillis) {
		this.maxWaitMillis = maxWaitMillis;
	}

	public int getMinIdel() {
		return minIdel;
	}

	public void setMinIdel(int minIdel) {
		this.minIdel = minIdel;
	}

	public Set<String> getClusters() {
		return clusters;
	}

	public void setClusters(Set<String> clusters) {
		this.clusters = clusters;
	}

}
