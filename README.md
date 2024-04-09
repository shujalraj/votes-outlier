## Reviewer Instructions
We were unable to create a pull request for this submission.
This is probably because the candidate force pushed the code or pushed it to a non default branch.
If you can't find the assignment in the default branch, it is likely that it exists in a different branch.
    

## :warning: Please read these instructions carefully and entirely first
* Clone this repository to your local machine.
* Use your IDE of choice to complete the assignment.
* When you have completed the assignment, you need to  push your code to this repository and [mark the assignment as completed by clicking here](https://app.snapcode.review/submission_links/c8e6c14b-9e10-472f-a291-20730fb70620).
* Once you mark it as completed, your access to this repository will be revoked. Please make sure that you have completed the assignment and pushed all code from your local machine to this repository before you click the link.

## Before you start
### Why complete this task?

We want to make the interview process as simple and stress-free as possible. That’s why we ask you to complete the first stage of the process from the comfort of your own home.

Your submission will help us to learn about your skills and approach. If we think you’re a good fit for our network, we’ll use your submission in the next interview stages too.

### About the task

You’ll be creating an ingestion process to ingest files containing vote data. You’ll also create a means to query the ingested data to determine outlier weeks.

There’s no time limit for this task, but we expect it to take less than 2 hours.

### Software prerequisites

The exercise requires Docker in order to run, so please make sure you have this installed. For convenience, we also
provide a [`Makefile`](Makefile) to easily launch various commands. If you aren't familiar with GNU Make or don't have
it installed on your system, [these equivalents](docs/how-to-run.md) should help you.

### Bootstrap solution

This repository contains a bootstrap solution that you can use to build upon. 

You can make any changes you like, as long as the solution can still be executed using the supplied Makefile.
To view the targets supported by the Makefile please execute the `make help` target.

Our review process starts with a very simplistic test set in exercise-tests folder that you should also check before submission:
- `make run-ingestion-test`
- `make run-outliers-test`

You should not change the exercise-tests folder and your solution should be able to pass both tests.

If you don't have GNU Make or are running on Windows without WSL, check [here](docs/how-to-run.md) to see how to run these checks.

The base solution uses SQLite3 as the database, and you should treat SQLite3 as if it were a real data warehouse. 
The database should be saved in the root folder of the project as `warehouse.db`, as shown in the 
[src/test/scala/com/exercise/ExerciseSpec.scala](src/test/scala/com/exercise/ExerciseSpec.scala) file.

* [src/main/scala/com/exercise.ExerciseApp](src/main/scala/com/exercise/ExerciseApp.scala) is provided as the entry point 
  for running both the ingestion and outlier detection processes. The ingestion should be implemented in the 
  `performIngestion` function, and the outlier detection should be implemented in the `executeOutliersQuery` function.

### Tips on what we’re looking for

1. **Test coverage**

    Your solution must have good test coverage, including common execution paths.

2. **Self-contained tests**

    Your tests should be self-contained, with no dependence on being run in a specific order.

3. **Simplicity**

    We value simplicity as an architectural virtue and a development practice. Solutions should reflect the difficulty of the assigned task, and shouldn’t be overly complex. We prefer simple, well tested solutions over clever solutions. 

    Please avoid:

   * unnecessary layers of abstraction
   * patterns
   * custom test frameworks
   * architectural features that aren’t called for

4. **Self-explanatory code**

    The solution you produce must speak for itself. Multiple paragraphs explaining the solution is a sign that the code isn’t straightforward enough to understand on its own.

5. **Dealing with ambiguity**

    If there’s any ambiguity, please add this in a section at the bottom of the README. You should also make a choice to resolve the ambiguity.

## Begin the two-part task

There are two requirements for the task, and you are expected to complete both. 
A user should be able to execute each task independently of the other. 
For example, ingestion shouldn't cause the outliers query to be executed.

### Part 1: Ingestion
Create an ingestion process that can be run on demand to ingest files containing vote data. You should ensure that data scientists, who will be consumers of the data, do not need to consider duplicate records in their queries. The data should be stored in a table called `votes`. 

### Part 2: Outliers calculation
Create a SQL view/table named `outlier_weeks`. It will contain the output of a SQL calculation for which weeks are regarded as outliers based on the vote data that was ingested.
The view should contain the year, week number and the number of votes for the week _for only those weeks which are determined to be outliers_, according to the following rule:

NB! If you're viewing this Markdown document in a viewer
where the math isn't rendering, try viewing this README in GitHub on your web browser, or [see this pdf](docs/calculating_outliers.pdf).

> 
> **A week is classified as an outlier when the total votes for the week deviate from the average votes per week for the complete dataset by more than 20%.**</br>  
> For the avoidance of doubt, _please use the following formula_: 
>  
> > Say the mean votes is given by $\bar{x}$ and this specific week's votes is given by $x_i$. 
> > We want to know when $x_i$ differs from $\bar{x}$ by more than $20$%. 
> > When this is true, then the ratio $\frac{x_i}{\bar{x}}$ must be further from $1$ by more than $0.2$, i.e.: </br></br> 
> > $\big|1 - \frac{x_i}{\bar{x}}\big| > 0.2$

The data should be sorted in the view by year and week number, with the earliest week first.

Running `ExerciseApp` with the `executeOutliersQuery` argument should recreate the view and 
just print the contents of this `outliers_weeks` view to the terminal - don't do any more calculations after creating the view.

## Example

The sample dataset below is included in the test-resources folder and can be used when creating your tests.

Assuming a file is ingested containing the following entries:

```
{"Id":"1","PostId":"1","VoteTypeId":"2","CreationDate":"2022-01-02T00:00:00.000"}
{"Id":"2","PostId":"1","VoteTypeId":"2","CreationDate":"2022-01-09T00:00:00.000"}
{"Id":"4","PostId":"1","VoteTypeId":"2","CreationDate":"2022-01-09T00:00:00.000"}
{"Id":"5","PostId":"1","VoteTypeId":"2","CreationDate":"2022-01-09T00:00:00.000"}
{"Id":"6","PostId":"5","VoteTypeId":"3","CreationDate":"2022-01-16T00:00:00.000"}
{"Id":"7","PostId":"3","VoteTypeId":"2","CreationDate":"2022-01-16T00:00:00.000"}
{"Id":"8","PostId":"4","VoteTypeId":"2","CreationDate":"2022-01-16T00:00:00.000"}
{"Id":"9","PostId":"2","VoteTypeId":"2","CreationDate":"2022-01-23T00:00:00.000"}
{"Id":"10","PostId":"2","VoteTypeId":"2","CreationDate":"2022-01-23T00:00:00.000"}
{"Id":"11","PostId":"1","VoteTypeId":"2","CreationDate":"2022-01-30T00:00:00.000"}
{"Id":"12","PostId":"5","VoteTypeId":"2","CreationDate":"2022-01-30T00:00:00.000"}
{"Id":"13","PostId":"8","VoteTypeId":"2","CreationDate":"2022-02-06T00:00:00.000"}
{"Id":"14","PostId":"13","VoteTypeId":"3","CreationDate":"2022-02-13T00:00:00.000"}
{"Id":"15","PostId":"13","VoteTypeId":"3","CreationDate":"2022-02-20T00:00:00.000"}
{"Id":"16","PostId":"11","VoteTypeId":"2","CreationDate":"2022-02-20T00:00:00.000"}
{"Id":"17","PostId":"3","VoteTypeId":"3","CreationDate":"2022-02-27T00:00:00.000"}
```

Then the following should be the content of your `outlier_weeks` view:


| Year | WeekNumber | VoteCount |
|------|------------|-----------|
| 2022 | 0          | 1         |
| 2022 | 1          | 3         |
| 2022 | 2          | 3         |
| 2022 | 5          | 1         |
| 2022 | 6          | 1         |
| 2022 | 8          | 1         |

**Note that we strongly encourage you to use this data as a test case to ensure that you have the correct calculation!**

## Other Requirements

Please include instructions about your strategy and important decisions you made in the README file. You should also include answers to the following questions:

1. What kind of data quality measures would you apply to your solution in production?
2. What would need to change for the solution scale to work with a 10TB dataset with 5GB new data arriving each day?
3. Please tell us in your modified README about any assumptions you have made in your solution.


## Shivraj:

## What kind of data quality measures would you apply to your solution in production?
* Schema Validation before processing it further.
* Verify that all required fields are present and populated with data.
* Verify that all required fields are present and populated with data.
* Implement robust error handling mechanisms to capture and handle data quality issues encountered during processing.
* Log relevant information for auditing, troubleshooting, and continuous improvement.
* Set up monitoring and alerting systems to proactively detect and notify stakeholders of any deviations from expected data quality levels.

## What would need to change for the solution scale to work with a 10TB dataset with 5GB new data arriving each day?
* Optimizing cluster configuration and provision sufficient resources (CPU, memory, storage) to handle the increased volume of data.
  Number of executor depends on the available resources in your cluster  and the parallelism we want to achieve.


>Calculation: With 10TB + 5GB data is arriving daily.
>>          Total Partition : (10 * 1024 * 1024) + 5 * 1024 = 81920 + 40 = ~ 81960 partitions
>>           Suppose we have cluster of 100 nodes - each node has 32 cpu core and 128 GB ram
>>           Lets take a balanced approach of 5 core per executor and 23 GB RAM.
>>           So one executor can parallel work on 5 partitions/tasks each of 128 mb.
>>           Total core = num executor * executor cores * nodes = 6 * 5 * 100 = 300 cores
>>           So we can run 300 tasks in parallel. It will first fetch 300 partition data then run next 300 partition data and so on.

Some other aspect for improving the performance
* Partition the data across multiple nodes in the cluster to enable parallel processing.
* Choose efficient data formats for storing and processing large datasets, such as Parquet.
* Partition large tables based on relevant attributes to optimize query performance - column like CreationDate or year or weeknum will be better candidate for partitioning because of frequest query, filter and join on those column.
* if any data frame like votesDF is reused multiple times in workflow or if it's particularly expensive to compute, caching or persisting it can be beneficial.
* Repartitioning will be helpful when there is a data skewness for any particular partition. Suppose for week 9 data is more and other partition data is less i.e. unevenly distributed.
* Can have specific error message rather than broad except:
  AnalysisException,
  FileNotFoundException,
  IOException,
  SparkException,
  OutOfMemoryError,
  UnsupportedOperationException and
  IllegalArgumentException