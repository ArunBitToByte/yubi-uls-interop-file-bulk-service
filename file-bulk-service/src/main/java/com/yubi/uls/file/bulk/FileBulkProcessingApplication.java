package com.yubi.uls.file.bulk;

import com.yubi.uls.file.bulk.service.JobWorker;
import com.yubi.uls.file.bulk.service.JobWorkerDuckDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class FileBulkProcessingApplication {

  public static void main(String[] args) {
    ApplicationContext applicationContext = SpringApplication.run(FileBulkProcessingApplication.class, args);
    JobWorker JobWorker = applicationContext.getBean(JobWorker.class);
    JobWorkerDuckDB JobWorkerDuckDB = applicationContext.getBean(JobWorkerDuckDB.class);
    JobWorker.run();
   // JobWorkerDuckDB.run();
  }
}
