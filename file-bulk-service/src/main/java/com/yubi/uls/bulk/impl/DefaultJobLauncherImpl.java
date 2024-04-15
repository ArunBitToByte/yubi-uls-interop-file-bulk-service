package com.yubi.uls.bulk.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.yubi.uls.bulk.core.JobLauncher;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.temporal.JobWorkflow;
import com.yubi.uls.bulk.utility.helper.HelperUtility;
import com.yubi.uls.interop.bulk.entity.JobEntity;
import com.yubi.uls.interop.bulk.repository.JobRepository;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Data
@RequiredArgsConstructor
@Component
public class DefaultJobLauncherImpl implements JobLauncher {

//    private final  JobRepository jobRepository;

    @Value("${bulkservice.workflow.job.queue}")
    private String TASK_QUEUE;

    @Override
    public void launchJob(String jobId) {
        log.info("Launching job with id: {}", jobId);
        JobConfiguration jobConfiguration = geJobConfig(jobId);
        launchJob(jobConfiguration);
        log.info("job successfully launched with id: {}", jobId);
    }

    private void launchJob(JobConfiguration jobConfiguration) {
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient workflowClient = WorkflowClient.newInstance(service);
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build();
        JobWorkflow jobWorkflow = workflowClient.newWorkflowStub(JobWorkflow.class, options);
        WorkflowExecution execution = WorkflowClient.start(jobWorkflow::process,
               jobConfiguration);
        log.info("Started job workflow. WorkflowId= {} , RunId= {}",execution.getWorkflowId(),execution.getRunId());
    }

    private JobConfiguration geJobConfig(String jobId) {
//        JobEntity jobEntity = jobRepository.findById(jobId).get();
//        JobEntity jobEntity = new JobEntity();
//           jobEntity.setConfig("{\"key\":\"value\"}");
//        JobConfiguration jobConfiguration= JobConfiguration.builder().parameters(HelperUtility.convertJsonNodeToMap(jobEntity.getConfig())).build();
        JobConfiguration jobConfiguration= JobConfiguration.builder().parameters(new HashMap<>()).build();
        jobConfiguration.getParameters().put(ConfigParams.MAX_CONCURRENCY.name(),2);
        jobConfiguration.getParameters().put(ConfigParams.PARTITION_ACTIVITY_QUEUE.name(),"fileProcessingQueue");
        return jobConfiguration;
    }
}
