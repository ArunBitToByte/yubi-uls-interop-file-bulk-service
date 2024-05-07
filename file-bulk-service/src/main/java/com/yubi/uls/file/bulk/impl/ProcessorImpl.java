package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.Processor;
import com.yubi.uls.file.bulk.dto.ProcessorItem;
import com.yubi.uls.file.bulk.dto.ReaderItem;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProcessorImpl implements Processor<ReaderItem, ProcessorItem> {

    @Override
    public List<ProcessorItem> process(List<ReaderItem> readerItem) {
        log.info("Processing object: {}", readerItem);
        return null;
    }
}
