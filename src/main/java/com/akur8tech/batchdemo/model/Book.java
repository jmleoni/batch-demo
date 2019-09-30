package com.akur8tech.batchdemo.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {
    @CsvBindByName(column = "BibNum")
    private Integer bibNum;

    @CsvBindByName(column = "Title")
    private String title;

    @CsvBindByName(column = "Author")
    private String author;

    @CsvBindByName(column = "ISBN")
    private String isbn;

    @CsvBindByName(column = "PublicationYear")
    private String publicationYear;

    @CsvBindByName(column = "Publisher")
    private String publisher;

    @CsvBindByName(column = "Subjects")
    private String Subjects;

    @CsvBindByName(column = "ItemType")
    private String itemType;

    @CsvBindByName(column = "ItemCollection")
    private String itemCollection;

    @CsvBindByName(column = "FloatingItem")
    private String floatingItem;

    @CsvBindByName(column = "ItemLocation")
    private String itemLocation;

    @CsvBindByName(column = "ReportDate")
    @CsvDate("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Date reportDate;

    @CsvBindByName(column = "ItemCount")
    private Integer ItemCount;

    public String getItemCollection() {
        return itemCollection;
    }

}
