# cache
二级缓存策略

# 如何使用
~~~
// 初始化缓存管理器
CacheManager<String, Serializable> multivelCache = new CacheManager<String, Serializable>();

// 获取指定名称的二级缓存.任何序列化的类均可作为value
Cache<String, Serializable> cache = multivelCache.getMultilvelCache("rule");

// 往缓存里面设置值
// 1、设置redis缓存
// 2、广播通知删除ehcache缓存
cache.put("hello", "world!");

// 缓存失效
// 1、删除redis缓存
// 2、广播通知删除ehcache缓存
cache.invalidate("hello");

// 缓存获取
// 1、ehcache缓存获取
// 2、ehcache获取不到，则从redis缓存获取
// 3、redis缓存获取成功，设置本地ehcache缓存
// 4、loader是一个是Callable实现，如果均获取不到时，可以选择从loader里面加载。
cache.get("hello", loader);
~~~
