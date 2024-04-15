package com.yubi.uls.bulk.core.client;

import com.yubi.uls.bulk.core.dto.JobConfiguration;

import java.util.List;


public interface Partitioner {
    List<JobConfiguration> partition(JobConfiguration jobConfiguration);
}
