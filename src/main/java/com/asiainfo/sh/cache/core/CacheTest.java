package com.asiainfo.sh.cache.core;

public class CacheTest {

	
	public static void main(String[] args) {
		CacheManager<String, String> manager = new CacheManager<String, String>();
		Cache<String, String> cache = manager.getMultilvelCache("crm_rule");
		cache.put("hello", "world");
		cache = manager.getMultilvelCache("crm");
		cache.put("hello", "world");
		cache = manager.getMultilvelCache("esb");
		cache.put("hello", "world");
		cache = manager.getMultilvelCache("promotion");
		cache.put("hello", "world");
	}
}
