package com.akur8tech.batchdemo.tasklets;

import com.akur8tech.batchdemo.BookRepository;
import com.akur8tech.batchdemo.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.HashMap;
import java.util.List;

public class BooksProcessor implements Tasklet, StepExecutionListener {
    private final Logger logger = LoggerFactory.getLogger(BooksProcessor.class);

    private HashMap<String, Integer> collectionCount;

    private BookRepository bookRepository;

    public BooksProcessor(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext)
            throws Exception {
        for (Book book : this.bookRepository.getBooks()) {
            collectionCount.put(
                    book.getItemCollection(),
                    collectionCount.getOrDefault(book.getItemCollection(), 0) + 1);
        }
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.collectionCount = new HashMap<>();
        logger.debug("Books Processor initialized.");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("Books Processor ended.");
        for(String collection : this.collectionCount.keySet()){
            logger.info("{} : {}", collection, this.collectionCount.get(collection));
        }
        return ExitStatus.COMPLETED;
    }
}
