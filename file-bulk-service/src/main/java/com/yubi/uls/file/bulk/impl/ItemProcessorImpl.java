package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.ItemProcessorWithDuckDBReader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ItemProcessorImpl implements ItemProcessorWithDuckDBReader {
    @Override
    public void process(List<String> chunkData) {
        log.info("Processing chunk completed: {}", chunkData.get(0));
    }

}
