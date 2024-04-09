#!/bin/bash

# Run ingestion and outliers and check number of rows
rm -f warehouse.db
make ingest-data
make detect-outliers

TABLE_EXISTS=$(make -s query="SELECT name FROM sqlite_master WHERE (type='table' or type='view') AND name='outlier_weeks'" run-query)
if [ -z "$TABLE_EXISTS" ]; then
echo "Test Failed: Table/View outlier_weeks does not exist"
exit 1
fi

OUTLIER_ROWS=$(make -s query="SELECT COUNT(*) FROM outlier_weeks" run-query)
MINIMUM_EXPECTED_ROWS=1
if [ "$OUTLIER_ROWS" -lt $MINIMUM_EXPECTED_ROWS ]; then
  echo "Test Failed: No outliers detected"
  exit 1
fi
echo "Test OK!"
