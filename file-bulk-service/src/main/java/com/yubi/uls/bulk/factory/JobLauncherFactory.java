package com.yubi.uls.bulk.factory;

import com.yubi.uls.bulk.core.JobLauncher;
import com.yubi.uls.bulk.impl.DefaultJobLauncherImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JobLauncherFactory {

    private final DefaultJobLauncherImpl defaultJobLauncherImpl;
    public JobLauncher getJobLauncher(String type) {
        if(StringUtils.isEmpty(type)) {
            return buildDefaultJobLauncher();
        }
        return null;
    }

    private JobLauncher buildDefaultJobLauncher() {
        return defaultJobLauncherImpl;
    }
}
