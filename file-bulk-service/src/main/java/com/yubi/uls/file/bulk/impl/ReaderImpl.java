package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.Reader;
import com.yubi.uls.bulk.core.dto.ChunkConfig;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.dto.Partition;
import com.yubi.uls.bulk.core.dto.State;
import com.yubi.uls.bulk.core.temporal.JobWorkflow;
import com.yubi.uls.file.bulk.dto.ReaderItem;
import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ReaderImpl implements Reader<ReaderItem> {

    @Override
    public List<ReaderItem> read(ChunkConfig chunkConfig) {
        log.info("Reading chunk in Reader: {}", chunkConfig);
        List<ReaderItem> readerItems = readData(chunkConfig);
        log.info("Reading chunk completed in Reader: {}", readerItems.get(0));
        return readerItems;
    }

    private List<ReaderItem> readData(ChunkConfig chunkConfig) {
          String filePath = (String) chunkConfig.getParameters().get(ConfigParams.PARTITION_FILE_PATH.name());
            List<ReaderItem> readerItems = new ArrayList<>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(filePath));
                int count = 1;
                String data;
                while( (data = reader.readLine()) != null && count <= chunkConfig.getSize()){
                    ReaderItem readerItem = ReaderItem.builder().item(data).build();
                    readerItems.add(readerItem);
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return readerItems;
        }
}
