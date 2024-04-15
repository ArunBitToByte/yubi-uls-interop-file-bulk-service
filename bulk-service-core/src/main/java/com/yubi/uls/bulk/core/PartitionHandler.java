package com.yubi.uls.bulk.core;

import com.yubi.uls.bulk.core.dto.JobConfiguration;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


public interface PartitionHandler {

    void handlePartition(JobConfiguration jobConfiguration);
}
