package com.yubi.uls.bulk;

import com.yubi.uls.bulk.service.JobWorker;
import com.yubi.uls.bulk.service.JobStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BulkProcessingApplication {

  public static void main(String[] args) {
    ApplicationContext applicationContext = SpringApplication.run(BulkProcessingApplication.class, args);
    JobWorker JobWorker = applicationContext.getBean(JobWorker.class);
    JobStarter JobStarter = applicationContext.getBean(JobStarter.class);
    JobWorker.run();
    JobStarter.start();
  }
}
