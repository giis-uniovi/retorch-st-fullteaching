#!/bin/bash
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <TJobName>"
  exit 1
fi
DOCKER_HOST_IP=$(/sbin/ip route | awk '/default/ { print $3 }')
COUNTER=0
WAIT_LIMIT=40

while ! curl --insecure -s "https://full-teaching-$1:5000" | grep -q "<title>FullTeaching</title>"; do
  echo "Waiting $COUNTER seconds for $1 with URL https://full-teaching-$1:5000"
  sleep 5
  ((COUNTER++))

  if ((COUNTER > WAIT_LIMIT)); then
    echo "The container is down"
    exit 1
  fi
done
