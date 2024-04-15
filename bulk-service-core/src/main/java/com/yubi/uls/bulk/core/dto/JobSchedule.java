package com.yubi.uls.bulk.core.dto;

import lombok.Data;

@Data
public class JobSchedule {
    private String jobName;
    private String cronExpression;
    private JobConfiguration jobConfiguration;
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
    public JobConfiguration getJobConfiguration() {
        return jobConfiguration;
    }
    public void setJobConfiguration(JobConfiguration jobConfiguration) {
        this.jobConfiguration = jobConfiguration;
    }
}
