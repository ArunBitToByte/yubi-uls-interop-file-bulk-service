package com.yubi.uls.bulk.core;

import com.yubi.uls.bulk.core.dto.JobConfiguration;
import lombok.Builder;

public interface JobLauncher {


    void launchJob(String name);
}
