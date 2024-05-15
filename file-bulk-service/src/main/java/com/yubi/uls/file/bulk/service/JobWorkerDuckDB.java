package com.yubi.uls.file.bulk.service;

import com.yubi.uls.bulk.core.client.temporal.impl.ChunkProcessorActivityImpl;
import com.yubi.uls.bulk.core.client.temporal.impl.DefaultChunkProcessorActivityImpl;
import com.yubi.uls.bulk.core.client.temporal.impl.PartitionerActivityImpl;
import com.yubi.uls.bulk.core.client.temporal.impl.PartitionerDuckDbImpl;
import com.yubi.uls.bulk.core.impl.DefaultJobImpl;
import com.yubi.uls.bulk.core.impl.DefaultPartitionHandlerChildWorkflow;
import com.yubi.uls.file.bulk.dto.Item;
import com.yubi.uls.file.bulk.impl.*;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JobWorkerDuckDB {

    @Value("${file.bulkservice.workflow.job.queue}")
    private  String TASK_QUEUE;

    public void run() {
        log.info("Running bulk job worker");
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TASK_QUEUE);

        // Registering the item processor activities
        DefaultChunkProcessorActivityImpl.DefaultChunkProcessorActivityImplBuilder<Item> builder =  DefaultChunkProcessorActivityImpl.builder();
        worker.registerActivitiesImplementations(builder.itemProcessor(new ItemProcessorImpl()).build());

        // Registering the workflow
        worker.registerWorkflowImplementationTypes(
                DefaultJobImpl.class, DefaultPartitionHandlerChildWorkflow.class);
        // Registering the partition activities
        worker.registerActivitiesImplementations(PartitionerActivityImpl.builder().partitioner(new PartitionerDuckDbImpl()).build());

        factory.start();
        log.info("Worker started for task queue: {}", TASK_QUEUE);
    }

}
