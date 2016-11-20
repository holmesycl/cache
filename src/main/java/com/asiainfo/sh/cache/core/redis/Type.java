package com.asiainfo.sh.cache.core.redis;

import com.asiainfo.sh.cache.core.util.Assert;
import com.asiainfo.sh.cache.core.util.ObjectUtils;

/**
 * 集群类型.
 * 
 * @author holme
 *
 */
public enum Type {

	/**
	 * 主集群
	 */
	MASTER("master", 0),

	/**
	 * 备份集群
	 */
	BACKUP("backup", 1);

	private String name;
	private int value;

	Type(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public int getValue() {
		return value;
	}

	public static Type forName(String name) {
		Assert.notNull(name);
		if (ObjectUtils.nullSafeEquals(name, MASTER.getName())) {
			return MASTER;
		} else if (ObjectUtils.nullSafeEquals(name, BACKUP.getName())) {
			return BACKUP;
		} else {
			return null;
		}
	}

	public static Type forValue(int value) {
		if (MASTER.getValue() == value) {
			return MASTER;
		} else if (BACKUP.getValue() == value) {
			return BACKUP;
		} else {
			return null;
		}
	}

}
