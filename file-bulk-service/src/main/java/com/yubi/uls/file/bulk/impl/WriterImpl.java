package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.Writer;
import com.yubi.uls.file.bulk.dto.ProcessorItem;
import com.yubi.uls.file.bulk.dto.ReaderItem;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class WriterImpl implements Writer<ReaderItem,ProcessorItem> {
    @Override
    public void write(List<ReaderItem> readerItems, List<ProcessorItem> processorItems) {
        log.info("Writing object: {}", readerItems.get(1));
//        log.info("Writing object");
    }
}
