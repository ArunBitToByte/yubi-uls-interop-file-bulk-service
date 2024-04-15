package com.yubi.uls.bulk.factory;

import com.yubi.uls.bulk.core.temporal.JobWorkflow;
import com.yubi.uls.bulk.impl.DefaultJobImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JobFactory {


    private  final DefaultJobImpl defaultJobImpl;
    public JobWorkflow createJob(String type) {
       if(StringUtils.isEmpty(type)){
           return buildDefaultJob();
        }
        return null;
    }

    private JobWorkflow buildDefaultJob() {
        return defaultJobImpl;
    }
}
