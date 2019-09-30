package com.akur8tech.batchdemo;

import com.akur8tech.batchdemo.tasklets.BooksProcessor;
import com.akur8tech.batchdemo.tasklets.BooksReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
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
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.context.config.annotation.EnableContextResourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;

@PropertySource(value = "application.properties")
@Configuration
@EnableAutoConfiguration(
        exclude = {
            DataSourceAutoConfiguration.class,
            ContextStackAutoConfiguration.class,
            ContextInstanceDataAutoConfiguration.class
        })
@EnableBatchProcessing
@EnableContextResourceLoader
public class BatchDemoApplicationConfig {

    @Autowired private ApplicationContext appContext;

    @Autowired private JobBuilderFactory jobs;

    @Autowired private StepBuilderFactory steps;


    @Bean
    public BookRepository bookRepository(){
        return new BookRepository();
    }


    @Value("${ordered}")
    private Boolean ordered;

    @Value("${input.csv}")
    private String inputCsv;

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
        return new BooksReader(appContext.getResource(this.inputCsv), bookRepository(), ordered);
    }

    @Bean
    public BooksProcessor booksProcessor() {
        return new BooksProcessor(bookRepository());
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
