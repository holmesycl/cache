package com.asiainfo.sh.cache.core.redis;

import redis.clients.jedis.JedisCluster;

/**
 * 集群
 * 
 * @author yecl
 * 
 */
public class Cluster {

	private String name;

	/**
	 * 主集群
	 */
	private JedisCluster masterJedisCluster;

	/**
	 * 备份集群
	 */
	private JedisCluster backupJedisCluster;

	public JedisCluster forType(Type type) {
		if (type == null) {
			return null;
		}
		if (type == Type.MASTER) {
			return this.getMasterJedisCluster();
		}
		if (type == Type.BACKUP) {
			return this.getBackupJedisCluster();
		}
		return null;
	}

	public Cluster(String name) {
		this.name = name;
	}

	public Cluster(String name, JedisCluster masterJedisCluster, JedisCluster backupJedisCluster) {
		this(name);
		this.masterJedisCluster = masterJedisCluster;
		this.backupJedisCluster = backupJedisCluster;
	}

	public String getName() {
		return name;
	}

	public JedisCluster getMasterJedisCluster() {
		return masterJedisCluster;
	}

	public JedisCluster getBackupJedisCluster() {
		return backupJedisCluster;
	}

}
