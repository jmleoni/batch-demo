package com.akur8tech.batchdemo;

import com.akur8tech.batchdemo.tasklets.BooksProcessor;
import com.akur8tech.batchdemo.tasklets.BooksReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.aws.context.config.annotation.EnableContextCredentials;
import org.springframework.cloud.aws.context.config.annotation.EnableContextRegion;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableAutoConfiguration
// @EnableBatchProcessing
@EnableContextResourceLoader
@EnableContextCredentials(
        accessKey = "***REMOVED***",
        secretKey = "***REMOVED***")
@EnableContextRegion(region = "eu-west-1")
public class BatchDemoApplicationConfig {

    @Autowired private JobBuilderFactory jobs;

    @Autowired private StepBuilderFactory steps;

    @Value("s3://demo-batch-data/library-collection-inventory.1000.csv")
    private Resource inputS3Csv;

    @Value("file:/Users/jean-marcleoni/Downloads/library-collection-inventory.1000.csv")
    private Resource inputLocalCsv;

    @Bean
    public JobRepository jobRepository() throws Exception {
        MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
        factory.setTransactionManager(transactionManager());
        return (JobRepository) factory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }

    @Bean
    public BooksReader booksReader() {
        return new BooksReader(this.inputLocalCsv);
    }

    @Bean
    public BooksProcessor booksProcessor() {
        return new BooksProcessor();
    }

    @Bean
    protected Step readBooks() {
        return steps.get("readBooks").tasklet(booksReader()).build();
    }

    @Bean
    protected Step processBooks() {
        return steps.get("processBooks").tasklet(booksProcessor()).build();
    }

    @Bean
    public Job job() {
        return jobs.get("taskletsJob").start(readBooks()).next(processBooks()).build();
    }
}
