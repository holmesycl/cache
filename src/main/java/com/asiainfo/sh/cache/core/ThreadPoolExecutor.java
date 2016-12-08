package com.asiainfo.sh.cache.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolExecutor {

	private static ExecutorService executor = Executors.newCachedThreadPool();

	public static <V> void submit(final Callable<V> callable) {
		executor.submit(callable);
	}

}
