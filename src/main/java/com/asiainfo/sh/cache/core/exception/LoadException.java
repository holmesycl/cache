package com.asiainfo.sh.cache.core.exception;

public class LoadException extends CacheException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3746293578218216179L;

	public LoadException() {
		super();
	}

	public LoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public LoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoadException(String message) {
		super(message);
	}

	public LoadException(Throwable cause) {
		super(cause);
	}

}
