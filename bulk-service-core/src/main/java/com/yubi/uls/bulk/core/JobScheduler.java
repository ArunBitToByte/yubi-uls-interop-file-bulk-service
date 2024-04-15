package com.yubi.uls.bulk.core;

import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.JobSchedule;

public interface JobScheduler {

    void scheduleJob(JobSchedule schedule);
}
