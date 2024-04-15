package com.yubi.uls.bulk.impl;

import com.yubi.uls.bulk.core.client.temporal.PartitionerActivity;
import com.yubi.uls.bulk.core.temporal.JobWorkflow;
import com.yubi.uls.bulk.core.dto.BatchProgress;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.temporal.PartitionHandlerChildWorkflow;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Workflow;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Builder
@RequiredArgsConstructor
@Component
public class DefaultJobImpl implements JobWorkflow {

    public String getName() {
        return null;
    }
    private final BatchProgress batchProgress = new BatchProgress();
    private final Set<JobConfiguration> runningWorkflows = new HashSet<>();

    @Override
    public void process(JobConfiguration jobConfiguration) {
        log.info("Processing job configuration: {}", jobConfiguration);
        //run prehooks
        //run job
        processJob(jobConfiguration);
        //run posthooks
        log.info("job completed successfully, configuration: {}", jobConfiguration);

    }

    private void processJob(JobConfiguration jobConfiguration) {
        //partition the job
        //inititalize the job
        //execute the partitions
        String actvityTaskQueue = (String) jobConfiguration.getParameters().get(ConfigParams.PARTITION_ACTIVITY_QUEUE.name());
        int maxConcurrency = (int) jobConfiguration.getParameters().get(ConfigParams.MAX_CONCURRENCY.name());
        PartitionerActivity partitionerActivity = Workflow.newActivityStub(
                PartitionerActivity.class,
                ActivityOptions.newBuilder().setTaskQueue(actvityTaskQueue).setStartToCloseTimeout(Duration.ofSeconds(60)).build());
        List<JobConfiguration> partitions = partitionerActivity.partition(jobConfiguration);
        batchProgress.initialize(partitions);
        processPartitions(partitions, maxConcurrency);
    }

    private void processPartitions(List<JobConfiguration> partitions, int maxConcurrency) {
        int partitionIndex = 0;
        for (JobConfiguration partition : partitions) {
            // Uses human friendly child id.
            String childId = Workflow.getInfo().getWorkflowId() + "/" + partitionIndex++;

            PartitionHandlerChildWorkflow childWorkflow = Workflow.newChildWorkflowStub(
                    PartitionHandlerChildWorkflow.class,
                    ChildWorkflowOptions.newBuilder().setWorkflowId(childId).build());

            // Starts a child workflow asynchronously ignoring its result.
            // The assumption is that the parent workflow doesn't need to deal with child workflow
            // results and failures. Another assumption is that a child in any situation calls
            // the reportCompletion signal.
            Async.procedure(childWorkflow::handlePartition, partition);

            // Resolves when a child reported successful start.
            // Used to wait for a child start on continue-as-new.
//            Promise<WorkflowExecution> childStartedPromise = Workflow.getWorkflowExecution(partitionProcessor);
//            childrenStartedByThisRun.add(childStartedPromise);
//            Promise.allOf(childrenStartedByThisRun).get();
            runningWorkflows.add(partition);
            batchProgress.updateInProgress(partition);
            // After starting MAX_CONCURRENCY children blocks until a completion signal is received.
            Workflow.await(() -> runningWorkflows.size() < maxConcurrency);
        }

        //Complete workflow, if no more records to process.
        Workflow.await(runningWorkflows::isEmpty);
    }


    @Override
    public void reportCompletion(JobConfiguration partition) {
        runningWorkflows.remove(partition);
        batchProgress.updateCompletion(partition);
    }

    @Override
    public BatchProgress getProgress() {
        return null;
    }
}
