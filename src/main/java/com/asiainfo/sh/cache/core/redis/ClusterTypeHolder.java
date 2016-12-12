package com.asiainfo.sh.cache.core.redis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClusterTypeHolder {

	private static final Map<String, Type> holder = new ConcurrentHashMap<String, Type>();

	private ClusterTypeHolder() {
	}

	public static void put(String cluster, Type type) {
		holder.put(cluster, type);
	}

	/**
	 * 第一次访问主集群
	 * 
	 * @param cluster
	 * @return
	 */
	public static Type get(String cluster) {
		return holder.get(cluster) == null ? Type.MASTER : holder.get(cluster);
	}
}
