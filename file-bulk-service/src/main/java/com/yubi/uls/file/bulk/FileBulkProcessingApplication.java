package com.yubi.uls.file.bulk;

import com.yubi.uls.file.bulk.service.Starter;
import com.yubi.uls.file.bulk.service.JobWorkerDuckDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FileBulkProcessingApplication {

  public static void main(String[] args) {
    ApplicationContext applicationContext = SpringApplication.run(FileBulkProcessingApplication.class, args);
    JobWorkerDuckDB jobWorkerDuckDB = applicationContext.getBean(JobWorkerDuckDB.class);
    Starter jobStarter = applicationContext.getBean(Starter.class);

    jobWorkerDuckDB.run();
    jobStarter.launchJob();
  }
}
