#!/bin/bash

# Camunda API URL
CAMUNDA_URL="http://localhost:8080/engine-rest"

# processInstanceId
PROCESS_INSTANCE_ID=$1

curl -X DELETE "${CAMUNDA_URL}/process-instance/${PROCESS_INSTANCE_ID}" \
     -H "Content-Type: application/json" \
     -d '{"deleteReason": "Process failed and is no longer needed"}'

echo "Process instance: ${PROCESS_INSTANCE_ID} deleted."