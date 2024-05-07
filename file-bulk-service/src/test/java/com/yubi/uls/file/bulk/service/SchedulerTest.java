package com.yubi.uls.file.bulk.service;


import com.sun.jdi.connect.spi.Connection;
import org.duckdb.DuckDBConnection;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SchedulerTest {

//    @Scheduled(cron = "0 * * * * *")
    @Test
    public void scheduleJobTest() {
        System.out.println("SchedulerTest.scheduleJobTest() called");
    }
//SELECT to_json(record) as record_json FROM '/Users/arunkumar.chouhan/projectHome/rnd/bulk_processing/data/sample_dataset.csv_partition/partition_group=1/data_0.csv' as record where record_offset>=10000 limit 100

    @Test
    public void test() throws SQLException {
        DuckDBConnection connection = (DuckDBConnection)DriverManager.getConnection("jdbc:duckdb:/Users/arunkumar.chouhan/projectHome/rnd/bulk_processing/data/test1.db");
        ResultSet resultSet =connection.createStatement().executeQuery("select count(*), max(partition_group) from '/Users/arunkumar.chouhan/projectHome/rnd/bulk_processing/data/sample_dataset.csv_partition/partition_group=0/data_0.csv'");
        while (resultSet.next()) {
            System.out.println(resultSet.getInt(1));
            System.out.println(resultSet.getInt(2));
        }

    }
}
