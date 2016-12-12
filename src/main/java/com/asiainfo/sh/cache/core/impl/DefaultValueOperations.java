package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.Loader;
import com.asiainfo.sh.cache.core.exception.CacheException;
import com.asiainfo.sh.cache.core.util.Assert;

public class DefaultValueOperations<K extends Serializable, V extends Serializable> extends ValueOperations<K, V> {

	public DefaultValueOperations(Cache<K, V> cache) {
		super(cache);
	}

	@Override
	public V get(K key, @NotNull Loader<? extends V> loader) throws CacheException {
		Assert.notNull(loader, "该loader用于在缓存获取不到时访问数据库，不能为空。");
		return super.get(key, loader);
	}

}
