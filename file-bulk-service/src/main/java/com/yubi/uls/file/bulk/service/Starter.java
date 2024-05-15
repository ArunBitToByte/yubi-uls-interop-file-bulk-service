package com.yubi.uls.file.bulk.service;

import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.temporal.JobWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@RequiredArgsConstructor
@Component
public class Starter {


    private final String FILE_PATH = "/Users/arunkumar.chouhan/projectHome/rnd/bulk_processing/data/sample_dataset.csv";
    private final int CHUNK_SIZE = 100;
    private final int MAX_CONCURRENCY = 1;
    private final int PARTITION_SIZE = 10000;
    private final String PARTITION_DIR_PATH = "/Users/arunkumar.chouhan/projectHome/rnd/bulk_processing/data/sample_dataset_partition/";


    @Value("${file.bulkservice.workflow.job.queue}")
    private String TASK_QUEUE;

    private static final String PARTITION_QUERY =
            """
               COPY (
                SELECT *, CAST(FLOOR((ROW_NUMBER() OVER () - 1) / %s) AS INTEGER) AS partition_group,
                (ROW_NUMBER() OVER () - 1) %% %s AS record_offset
               FROM read_csv_auto('%s', HEADER=TRUE))
               TO '%s'(FORMAT 'CSV', PARTITION_BY partition_group,  OVERWRITE_OR_IGNORE TRUE)
                """;
    public void launchJob() {
        log.info("Launching job ");
        JobConfiguration jobConfiguration = geJobConfig();
        launchJob(jobConfiguration);
        log.info("job successfully launched");
    }

    private void launchJob(JobConfiguration jobConfiguration) {
        log.info("Launching job workflow with configuration: {}", jobConfiguration);
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient workflowClient = WorkflowClient.newInstance(service);
        WorkflowOptions options = WorkflowOptions.newBuilder().setTaskQueue(TASK_QUEUE).build();
        JobWorkflow jobWorkflow = workflowClient.newWorkflowStub(JobWorkflow.class, options);
        WorkflowExecution execution = WorkflowClient.start(jobWorkflow::process,
               jobConfiguration);
        log.info("Started job workflow. WorkflowId= {} , RunId= {}",execution.getWorkflowId(),execution.getRunId());
    }

    private JobConfiguration geJobConfig() {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(ConfigParams.PARTITION_ACTIVITY_QUEUE.name(), TASK_QUEUE);
        parameters.put(ConfigParams.FILE_PATH.name(), FILE_PATH);
        parameters.put(ConfigParams.CHUNK_SIZE.name(), 100);
        parameters.put(ConfigParams.MAX_CONCURRENCY.name(), 1);
        parameters.put(ConfigParams.PARTITION_SIZE.name(), PARTITION_SIZE);
        parameters.put(ConfigParams.PARTITION_DIR_PATH.name(),  PARTITION_DIR_PATH);
//        parameters.put(ConfigParams.PARTITION_QUERY.name(),  String.format(PARTITION_QUERY, PARTITION_SIZE, PARTITION_SIZE, FILE_PATH, PARTITION_DIR_PATH));
        return JobConfiguration.builder().parameters(parameters).build();
    }
}
