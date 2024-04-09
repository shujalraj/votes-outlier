DOCKER_IMAGE_VERSION=8u222_1.3.5_2.13.1
docker_run = docker run --mount type=bind,source="$(shell pwd)",target=/root/ --rm hseeberger/scala-sbt:$(DOCKER_IMAGE_VERSION)
docker_run_sqlite = docker run --rm --mount type=bind,source="$(shell pwd)",target=/workspace keinos/sqlite3 sqlite3 /workspace/warehouse.db
.DEFAULT_GOAL := help

.PHONY: help
help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'


.PHONY: compile
compile: ##Compiles the code
	 $(docker_run) sbt clean compile

.PHONY: test
test: ## Run tests
	 $(docker_run) sbt clean test

.PHONY: fetch-data
fetch-data: ## Fetch the vote data from the remote location
	./fetch_data.sh

.PHONY: ingest-data
ingest-data: compile fetch-data ## Invoke the ingestion process
	 $(docker_run) sbt "run ingest uncommitted/votes.jsonl"

.PHONY: detect-outliers
detect-outliers: compile ## Invoke the outlier detection process
	 $(docker_run) sbt "run outliers"

.PHONY: run-query
run-query: ## Run an arbitrary query against the database (i.e. make query="select * from posts" run-query)
	$(docker_run_sqlite) "$(query)"

.PHONY: run-ingestion-test
run-ingestion-test:
	./exercise-tests/test-ingestion.sh

.PHONY: run-outliers-test
run-outliers-test:
	./exercise-tests/test-outliers.sh
