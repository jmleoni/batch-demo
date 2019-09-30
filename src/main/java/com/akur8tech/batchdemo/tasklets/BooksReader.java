package com.akur8tech.batchdemo.tasklets;

import com.akur8tech.batchdemo.model.Book;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.DefaultBufferedReaderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class BooksReader implements Tasklet, StepExecutionListener {

    public BooksReader(Resource resource) {
        this.resource = resource;
    }

    private final Resource resource;

    private final Logger logger = LoggerFactory.getLogger(BooksReader.class);

    private List<Book> books;

    private CsvToBean<Book> csvToBean;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        DefaultBufferedReaderFactory defaultBufferedReaderFactory =
                new DefaultBufferedReaderFactory();
        try {
            HeaderColumnNameMappingStrategy hcnms = new HeaderColumnNameMappingStrategy();
            hcnms.setType(Book.class);
            csvToBean =
                    new CsvToBeanBuilder(
                                    defaultBufferedReaderFactory.create(
                                            resource, Charset.defaultCharset().name()))
                            .withType(Book.class)
                            .withMappingStrategy(hcnms)
                            .build();
        } catch (IOException e) {
            logger.error("Books Reader not initialized.", e);
            return;
        }
        logger.debug("Books Reader initialized.");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
            throws Exception {
        logger.debug("Launching mutliThreaded parsing with OpenCSV");
        books = csvToBean.parse();
        logger.debug("Books read!!!");
        return RepeatStatus.FINISHED;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecution.getJobExecution().getExecutionContext().put("books", this.books);
        logger.debug("Books Reader ended.");
        return ExitStatus.COMPLETED;
    }
}
