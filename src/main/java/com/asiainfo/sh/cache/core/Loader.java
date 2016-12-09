package com.asiainfo.sh.cache.core;

import java.io.Serializable;

public interface Loader<V extends Serializable> {

	V load() throws LoadException;

}
