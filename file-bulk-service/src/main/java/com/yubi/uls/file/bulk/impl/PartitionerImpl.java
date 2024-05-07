package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.Partitioner;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.Partition;
import com.yubi.uls.bulk.core.dto.State;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;

@Slf4j
public class PartitionerImpl implements Partitioner {

    private String filePath;
    private String basePath;
    private String name;
    private int partitionSize;
    BufferedReader reader;
    String header;
    int offset = 1;

    @SneakyThrows
    @Override
    public void init(JobConfiguration jobConfiguration) {
        log.info("Initializing partitioner");
         filePath = (String) jobConfiguration.getParameters().get(ConfigParams.FILE_PATH.name());
         partitionSize = (int) jobConfiguration.getParameters().get(ConfigParams.PARTITION_SIZE.name());
         File sourceFile = new File(filePath);
         basePath = sourceFile.getParent();
         name = sourceFile.getName();
         reader = new BufferedReader(new FileReader(filePath));
         header =reader.readLine();
         log.info("Partitioner initialized");
    }

    @SneakyThrows
    public Partition getNextPartition() {
        log.info("Creating partition: {}", offset);
        File partitionFile = new File(basePath+"/split/"+name+"_"+offset + ".csv");
        BufferedWriter outfile = new BufferedWriter(new FileWriter(basePath+"/split/"+name+"_"+offset + ".csv"));
        int currentLen = 0;
        String data = null;
        outfile.write(header);
        while((data = reader.readLine()) != null && currentLen < partitionSize) {
            outfile.write("\n");
            outfile.write(data);
            currentLen++;
        }
        if(currentLen == 0) {
            return null;
        }
        outfile.close( );
        Partition partition = Partition.builder().parameters(new HashMap<>()).status(State.PENDING).partitionIndex(offset).partitionSize(currentLen).build();
        partition.getParameters().put(ConfigParams.PARTITION_FILE_PATH.name(), partitionFile.getAbsolutePath());
        offset++;
        return partition;
    }

}
