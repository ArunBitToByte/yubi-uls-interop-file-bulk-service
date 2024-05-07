package com.yubi.uls.file.bulk.service;

import com.yubi.uls.bulk.core.client.temporal.impl.ChunkProcessorActivityImpl;
import com.yubi.uls.bulk.core.client.temporal.impl.PartitionerActivityImpl;
import com.yubi.uls.file.bulk.dto.ProcessorItem;
import com.yubi.uls.file.bulk.dto.ReaderItem;
import com.yubi.uls.file.bulk.impl.PartitionerActivityImplTemp;
import com.yubi.uls.file.bulk.impl.PartitionerImpl;
import com.yubi.uls.file.bulk.impl.ReaderImpl;
import com.yubi.uls.file.bulk.impl.WriterImpl;
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
public class JobWorker {

    @Value("${file.bulkservice.workflow.job.queue}")
    private  String TASK_QUEUE;

    public void run() {
        log.info("Running bulk job worker");
        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TASK_QUEUE);
        ChunkProcessorActivityImpl.ChunkProcessorActivityImplBuilder<ReaderItem, ProcessorItem> builder =  ChunkProcessorActivityImpl.builder();
        worker.registerActivitiesImplementations(PartitionerActivityImpl.builder().partitioner(new PartitionerImpl()).build(), builder.reader(new ReaderImpl()).writer(new WriterImpl()).build());
        factory.start();
        log.info("Worker started for task queue: {}", TASK_QUEUE);
    }

}
