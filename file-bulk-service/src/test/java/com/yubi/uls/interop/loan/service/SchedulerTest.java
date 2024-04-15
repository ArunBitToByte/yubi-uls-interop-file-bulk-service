package com.yubi.uls.interop.loan.service;


import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

public class SchedulerTest {

    @Scheduled(cron = "0 * * * * *")
    @Test
    public void scheduleJobTest() {
        System.out.println("SchedulerTest.scheduleJobTest() called");
    }
}
