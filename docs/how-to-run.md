# How to run

To make this simple to run, we have provided a [`Makefile`](../Makefile) which trivially allows you to 
run the code in the same containerised setup we'll use to evaluate it. But if you haven't got GNU Make on your 
system, try these commands:

## Running `sbt` in the docker container
We're using the `hseeberger/scala-sbt:8u222_1.3.5_2.13.1` Docker image to run Scala 2.12. To run it in your
terminal / shell / command prompt, use:

Linux / Mac:
```shell
docker run --mount type=bind,source="$(shell pwd)",target=/root/ hseeberger/scala-sbt:8u222_1.3.5_2.13.1
```

Windows:
```powershell
docker run --mount type=bind,source="%cd%",target=/root/ hseeberger/scala-sbt:8u222_1.3.5_2.13.1
```

For the rest of these commands, we'll refer to this as `$(docker_run)` in this document. Create an appropriate
alias or just replace the appropriate text for your system whenever you see `$(docker_run)`. In other words,
if we say `$(docker_run) sbt compile` we mean execute the following in a terminal / command prompt:

```shell
docker run --mount type=bind,source="$(shell pwd)",target=/root/ hseeberger/scala-sbt:8u222_1.3.5_2.13.1 sbt compile
```

(for the Linux/Mac case)

## Compile the code
```shell
$(docker_run) sbt clean compile
```

## Run tests
```shell
$(docker_run) sbt clean test
```

## Fetch data
Linux / Mac / Windows WSL:
```shell
./fetch_data.sh
```

Windows:

1. Download the file at [https://drive.google.com/uc?export=download&id=1jLcE2Jw1znaBy7FD7XCme_My_1PTZk17](https://drive.google.com/uc?export=download&id=1jLcE2Jw1znaBy7FD7XCme_My_1PTZk17)
2. Ensure that a subdirectory called `uncomitted` exists
3. Copy the donwload file to `uncomitted\dataset.tar.gz`
4. Extract it. You should have a file called `uncomitted\votes.jsonl`

## Ingesting data
Make sure you have compiled and then

Linux / Mac / Windows WSL:
```shell
$(docker_run) sbt "run ingest uncommitted/votes.jsonl"
```

Windows:
```shell
$(docker_run) sbt "run ingest uncommitted\votes.jsonl"
```

## Detect outliers
Make sure you have compiled, and then:
```shell
$(docker_run) sbt "run outliers"
```

## Running ingestion checks
We'll run this SQL to check that the `votes` table exists:
```sql
SELECT name FROM sqlite_master WHERE type='table' AND name='votes
```

After ingesting the provided dataset, we'll check the row count - it should be less than `40299`.
```sql
SELECT COUNT(*) FROM votes
```

Then, we'll re-run the ingestion without manually deleting the data and check that you haven't appended duplicates:
```sql
SELECT COUNT(*) FROM votes
```
Should still be less than `40299`.


## Running outliers checks

We'll run this SQL to check that the `outliers_weeks` table exists:
```sql
SELECT name FROM sqlite_master WHERE (type='table' or type='view') AND name='outlier_weeks'
```

We'll also check that there is something in the view:
```sql
SELECT COUNT(*) FROM outlier_weeks
```
This should return at least one row.

