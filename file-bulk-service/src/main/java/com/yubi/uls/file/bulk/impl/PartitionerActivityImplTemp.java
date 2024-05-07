package com.yubi.uls.file.bulk.impl;




import com.yubi.uls.bulk.core.client.temporal.PartitionerActivity;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.Partition;
import com.yubi.uls.bulk.core.dto.State;
import com.yubi.uls.bulk.core.temporal.JobWorkflow;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PartitionerActivityImplTemp implements PartitionerActivity {

    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    private static final WorkflowClient client = WorkflowClient.newInstance(service);

    @Override
    public List<Partition> partition(JobConfiguration jobConfiguration) {
        try {
            return createPartition( jobConfiguration);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Partition> createPartition(JobConfiguration jobConfiguration) throws SQLException {
        log.info("Partitioning the file for job: {}", jobConfiguration.getParameters().get(ConfigParams.JOB_NAME.name()));
        String filePath = (String) jobConfiguration.getParameters().get(ConfigParams.FILE_PATH.name());
        int chunkSize = (int) jobConfiguration.getParameters().get(ConfigParams.CHUNK_SIZE.name());
        int partitionSize = (int) jobConfiguration.getParameters().get(ConfigParams.PARTITION_SIZE.name());
        List<Partition> partitions= split(filePath, partitionSize);
        log.info("Partitioning successful for job: {}", jobConfiguration.getParameters().get(ConfigParams.JOB_NAME.name()));
        return partitions;
    }


    private List<JobConfiguration> createPartitionDummy(JobConfiguration jobConfiguration) throws SQLException {
        log.info("Partitioning the file for job: {}", jobConfiguration.getParameters().get(ConfigParams.JOB_NAME.name()));
        int fileCount = 10;//readFileCount(connection);
        String filePath = (String) jobConfiguration.getParameters().get(ConfigParams.FILE_PATH.name());
        int chunkSize = (int) jobConfiguration.getParameters().get(ConfigParams.CHUNK_SIZE.name());
        int partitionCount = fileCount / chunkSize;
        List<JobConfiguration> offsets = new ArrayList<>();
        for (int i = 0; i < partitionCount; i++) {
            Map<String, Object> parameters = new HashMap<>(jobConfiguration.getParameters());
            JobConfiguration jobConfiguration1 = JobConfiguration.builder().parameters(parameters).build();
//            jobConfiguration.getParameters().put(ConfigParams.PARTITION.name(), new Partition(filePath, i * chunkSize, chunkSize));
            offsets.add(jobConfiguration1);
        }
        log.info("Partitioning successful for job: {}", jobConfiguration.getParameters().get(ConfigParams.JOB_NAME.name()));
        return offsets;
    }

    public List<Partition> split(String FilePath, int partitionSize) {
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        JobWorkflow workflow =
                client.newWorkflowStub(JobWorkflow.class, ctx.getInfo().getWorkflowId());
        int currentLen = 0;
        int count = 1, data;
        List<Partition> partitions = new ArrayList<>();
        try {
            File filename = new File(FilePath);
            String basePath = filename.getParent();
            String name = filename.getName();
            log.info("Base path: {}", basePath);
            //RandomAccessFile infile = new RandomAccessFile(filename, "r");
            InputStream infile = new BufferedInputStream(new FileInputStream(filename));
            data = infile.read();
            while (data != -1) {
                log.info("Creating partition: {}", count);
                filename = new File(basePath+"/split/"+name+"_"+count + ".csv");
                //RandomAccessFile outfile = new RandomAccessFile(filename, "rw");
                OutputStream outfile = new BufferedOutputStream(new FileOutputStream(filename));
                while (data != -1 && currentLen < partitionSize) {
                    outfile.write(data);
                    currentLen++;
                    data = infile.read();
                }

                Partition partition = Partition.builder().parameters(new HashMap<>()).status(State.PENDING).partitionIndex(count).partitionSize(partitionSize).build();
                partition.getParameters().put(ConfigParams.PARTITION_FILE_PATH.name(), filename.getAbsolutePath());
                partitions.add(partition);
                outfile.close();
                workflow.reportPartitionProgress(count);
                currentLen = 0;
                log.info("Partition created: {}", count);
                count++;
            }
            log.info("Total partitions created: {}", partitions.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partitions;
    }




}
