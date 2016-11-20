package com.asiainfo.sh.cache.core.redis;

import redis.clients.jedis.JedisCluster;

/**
 * 集群
 * 
 * @author yecl
 * 
 */
public class Cluster {
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

	public Cluster() {
		super();
	}

	public Cluster(JedisCluster masterJedisCluster, JedisCluster backupJedisCluster) {
		super();
		this.masterJedisCluster = masterJedisCluster;
		this.backupJedisCluster = backupJedisCluster;
	}

	public JedisCluster getMasterJedisCluster() {
		return masterJedisCluster;
	}

	public void setMasterJedisCluster(JedisCluster masterJedisCluster) {
		this.masterJedisCluster = masterJedisCluster;
	}

	public JedisCluster getBackupJedisCluster() {
		return backupJedisCluster;
	}

	public void setBackupJedisCluster(JedisCluster backupJedisCluster) {
		this.backupJedisCluster = backupJedisCluster;
	}

}
