package com.asiainfo.sh.cache.core.impl;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.Cache;
import com.asiainfo.sh.cache.core.CacheManager;

/**
 * 静态数据缓存
 * 
 * @author holme
 *
 * @param <V>
 */
public class StaticDataTemplate<V extends Serializable> extends AbstractDataTemplate<String, V> {

	public StaticDataTemplate(String cacheName) {
		super(cacheName);
	}

	@Override
	protected Cache<String, V> getCache() {
		return new CacheManager().getMultilevelCache(getCacheName());
	}

	@Override
	protected ValueOperations<String, V> getOps(Cache<String, V> cache) {
		return new NullLoaderValueOperations<String, V>(cache);
	}

	public static void main(String[] args) {
		StaticDataTemplate<User> template = new StaticDataTemplate<User>("crm_rule");
		User u = new User(1, "yecl");
		template.opsForValue().put("name", u);
		System.out.println(template.opsForValue().get("name"));
	}

	static class User implements Serializable {
		private long id;
		private String name;

		public User(long id, String name) {
			super();
			this.id = id;
			this.name = name;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + "]";
		}

	}

}
