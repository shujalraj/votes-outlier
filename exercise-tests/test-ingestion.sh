#!/bin/bash

# Run ingestion and check number of rows
rm -f warehouse.db
make ingest-data

TABLE_EXISTS=$(make -s query="SELECT name FROM sqlite_master WHERE type='table' AND name='votes'" run-query)
if [ -z "$TABLE_EXISTS" ]; then
echo "Test Failed: Table votes does not exist"
exit 1
fi

INGESTED_ROWS=$(make -s query="SELECT COUNT(*) FROM votes" run-query)
EXPECTED_ROWS=$(($(wc -l < ./uncommitted/votes.jsonl)+1))
if [ "$INGESTED_ROWS" -ne $EXPECTED_ROWS ]; then
  echo "Test Failed: Incorrect number of rows ingested: $EXPECTED_ROWS != $INGESTED_ROWS"
  exit 1
fi

# Re-ingestion of same data, check same number of rows
make ingest-data
INGESTED_ROWS=$(make -s query="SELECT COUNT(*) FROM votes" run-query)
EXPECTED_ROWS=$(($(wc -l < ./uncommitted/votes.jsonl)+1))
if [ "$INGESTED_ROWS" -ne $EXPECTED_ROWS ]; then
  echo "Test Failed: Incorrect number of rows ingested when re-ingesting same data: $EXPECTED_ROWS != $INGESTED_ROWS"
  exit 1
fi
echo "Test OK!"
