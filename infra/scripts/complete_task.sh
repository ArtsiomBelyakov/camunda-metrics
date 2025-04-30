#!/bin/bash

# Configuration
CAMUNDA_URL="http://localhost:8080/engine-rest"
TOPIC_NAME=$1
WORKER_ID="myWorker1"
LOCK_DURATION="10000"  # Lock duration in milliseconds
MAX_TASKS="1"         # Max tasks to fetch at once

# Step 1: Fetch and lock the external task
echo "Fetching and locking task..."

TASK_RESPONSE=$(curl -s -X POST "$CAMUNDA_URL/external-task/fetchAndLock" \
    -H "Content-Type: application/json" \
    -d '{
          "workerId": "'"$WORKER_ID"'",
          "maxTasks": '"$MAX_TASKS"',
          "topics": [{
              "topicName": "'"$TOPIC_NAME"'",
              "lockDuration": '"$LOCK_DURATION"'
          }]
    }')

# Step 2: Extract task ID from the response
TASK_ID=$(echo "$TASK_RESPONSE" | jq -r '.[0].id')

# Check if a task was returned
if [ "$TASK_ID" == "null" ]; then
    echo "No tasks available to fetch."
    exit 1
fi

echo "Task fetched successfully. Task ID: $TASK_ID"

# Step 3: Complete the task
echo "Completing task..."

curl -s -X POST "$CAMUNDA_URL/external-task/$TASK_ID/complete" \
    -H "Content-Type: application/json" \
    -d '{
        "workerId": "'"$WORKER_ID"'"
    }'

echo "Task completed successfully!"
