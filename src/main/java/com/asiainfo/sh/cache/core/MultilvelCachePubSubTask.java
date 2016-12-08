package com.asiainfo.sh.cache.core;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.sh.cache.core.MultilvelCachePubSub;

import redis.clients.jedis.JedisCluster;

public class MultilvelCachePubSubTask implements Callable<String> {

	private static final Logger log = LoggerFactory.getLogger(MultilvelCachePubSubTask.class);

	private JedisCluster jedisCluster;

	private MultilvelCachePubSub cachePubSub;

	public MultilvelCachePubSubTask(JedisCluster jedisCluster, MultilvelCachePubSub cachePubSub) {
		this.jedisCluster = jedisCluster;
		this.cachePubSub = cachePubSub;
	}

	@Override
	public String call() throws Exception {
		while (true) {
			try {
				log.info("开始订阅集群[" + cachePubSub.getCluster() + " | " + cachePubSub.getType().getName() + "]...");
				log.info("订阅channel:" + Arrays.toString(cachePubSub.channels()));
				jedisCluster.subscribe(cachePubSub, cachePubSub.channels());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				log.error(
						"集群[" + cachePubSub.getCluster() + " | " + cachePubSub.getType().getName() + "]失去连接.尝试重新订阅...");

			}
		}
	}

}
