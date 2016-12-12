package com.asiainfo.sh.cache.core.multilevel;

import com.asiainfo.sh.cache.core.redis.CachePubSub;
import com.asiainfo.sh.cache.core.redis.Cluster;
import com.asiainfo.sh.cache.core.redis.ClusterTypeHolder;

public class DefaultInvalidateListener implements InvalidateListener {

	private Cluster cluster;

	public DefaultInvalidateListener(Cluster cluster) {
		this.cluster = cluster;
	}

	@Override
	public void update(InvalidateObservable o, String... keys) {
		if (keys != null) {
			for (String key : keys) {
				cluster.forType(ClusterTypeHolder.get(cluster.getName()))
						.publish(CachePubSub.getInvalidateChannel(), key);

			}
		}
	}

}
