package com.asiainfo.sh.cache.core;

import java.io.Serializable;

import com.asiainfo.sh.cache.core.exception.LoadException;

public interface Loader<V extends Serializable> {

	V load() throws LoadException;

}
