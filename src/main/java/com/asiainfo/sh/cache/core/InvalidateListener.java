package com.asiainfo.sh.cache.core;

/**
 * 失效缓存数据监听者.
 * 
 * @author holme
 * @param <K>
 *
 */
public interface InvalidateListener {

	void update(InvalidateObservable o, String... keys);

}
