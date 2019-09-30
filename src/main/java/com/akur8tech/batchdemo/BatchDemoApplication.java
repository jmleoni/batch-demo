package com.akur8tech.batchdemo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Profile;
@SpringBootApplication
public class BatchDemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(BatchDemoApplication.class, args);
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BatchDemoApplicationConfig.class);
        context.refresh();

        final JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
        final Job job = (Job) context.getBean("job");
        System.out.println("Starting the batch job");
        try {
            final JobExecution execution = jobLauncher.run(job, new JobParameters());
            System.out.println("Job Status : " + execution.getStatus());
            System.out.println("Job succeeded");
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Job failed");
        }
    }

}
