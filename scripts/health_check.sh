#!/bin/bash
HEALTH_CHECK_URL="http://localhost:8080/health"

for i in {1..10}; do
    RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" ${HEALTH_CHECK_URL})
    if [ "$RESPONSE" == "200" ]; then
        echo "Health check successful"
        exit 0
    fi
    echo "Health check failed. Retrying in 10 seconds..."
    sleep 10
done

echo "Health check failed after multiple attempts"
exit 1
