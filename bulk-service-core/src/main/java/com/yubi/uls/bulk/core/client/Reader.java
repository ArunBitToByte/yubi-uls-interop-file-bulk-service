package com.yubi.uls.bulk.core.client;

import com.yubi.uls.bulk.core.dto.JobConfiguration;

import java.util.Iterator;

public interface Reader<T> {

    Iterator<T> read(JobConfiguration itemConfig);



}
