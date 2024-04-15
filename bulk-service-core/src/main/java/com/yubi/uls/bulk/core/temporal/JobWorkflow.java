package com.yubi.uls.bulk.core.temporal;

import com.yubi.uls.bulk.core.Job;
import com.yubi.uls.bulk.core.dto.BatchProgress;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.Partition;
import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


@WorkflowInterface
public interface JobWorkflow extends Job {
 @WorkflowMethod
 void process(JobConfiguration jobConfiguration);

 @SignalMethod
 void reportCompletion(JobConfiguration partition);

 @QueryMethod
 BatchProgress getProgress();
}
