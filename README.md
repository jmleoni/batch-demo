# AWS Batch and spring Batch demo

This small project is a simple Spring batch application that reads a CSV file from AWS S3 and parses it using Open CSV multithreaded parser.
It may run on AWS Batch once package as a docker container (see [Dockerfile](Dockerfile))

* [OpenCSV](http://opencsv.sourceforge.net/)
* [Spring Batch](https://docs.spring.io/spring-batch/3.0.x/reference/html/index.html)
* [AWS batch](https://docs.aws.amazon.com/en_pv/batch/latest/userguide/what-is-batch.html)
* [Seattle Library inventory](https://www.kaggle.com/city-of-seattle/seattle-library-collection-inventory)

With 40 cores and enough RAM it parses 29Millions rows (10Gb of data) in less than 5 minutes.