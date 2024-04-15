package com.yubi.uls.bulk.core;

import com.yubi.uls.bulk.core.dto.BatchProgress;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.Partition;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;



public interface Job {


 void process(JobConfiguration jobConfiguration);


 void reportCompletion(JobConfiguration partition);


 BatchProgress getProgress();
}
