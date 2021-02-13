#!/bin/bash
echo "Running health check to zookeeper .. "
if [ $(echo ruok | nc localhost 2181 | grep -c "imok") -eq 1 ]; then
  echo "healthy"
  exit 0
else
  echo "unhealthy"
  exit 1
fi
