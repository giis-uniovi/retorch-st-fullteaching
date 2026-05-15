#!/bin/bash
# The tjob-teardown.sh script provides all the necessary commands to tear-down each TJob's Resources after the
# test execution has ended. It stores the container logs of the current TJob, tear down the containers
# and execute the custom commands provided in the custom-tjob-teardown file.

set -e

if [ "$#" -ne 2 ]; then
    "$SCRIPTS_FOLDER/printLog.sh" "ERROR" "TJob-tear-down" "Usage: $0 <TJobName> <Stage>"
    exit 1
fi

EXEC_PATH="$WORKSPACE/target/coverage/execfiles"
COVERAGE_PATH="$WORKSPACE/target/coverage"

# Execute the script to write timestamp
"$SCRIPTS_FOLDER/writetime.sh" "$2" "$1"
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "$1-tear-down" "Starting the TJob tear-down"
# Store docker logs
"$SCRIPTS_FOLDER/storeContainerLogs.sh" "$1"

# Change to SUT location
cd "$SUT_LOCATION"

if [ "${JACOCO_ENABLED:-false}" = "true" ]; then
  mkdir -p "$EXEC_PATH"

  # Dump Jacoco coverage from the running container
  docker exec "full-teaching-$1" java -jar /org.jacoco.cli-0.8.13-nodeps.jar dump --address localhost --port 6300 --destfile /jacoco.exec

  # Copy the exec file to the workspace
  docker cp "full-teaching-$1:/jacoco.exec" "$EXEC_PATH/jacoco-$1.exec"

  # Copy app.jar and extract classes once (shared across all tjobs)
  if [ ! -f "$COVERAGE_PATH/app.jar" ]; then
    echo "app.jar not found in $COVERAGE_PATH, copying and extracting..."
    mkdir -p "$COVERAGE_PATH"
    docker cp "full-teaching-$1:/app.jar" "$COVERAGE_PATH/app.jar"

    cd "$COVERAGE_PATH"
    jar xf "$COVERAGE_PATH/app.jar"
    wget -q "https://repo1.maven.org/maven2/org/jacoco/org.jacoco.cli/0.8.13/org.jacoco.cli-0.8.13-nodeps.jar"
    mkdir -p classes
    mv "$COVERAGE_PATH/WEB-INF/classes" "$COVERAGE_PATH"
    cd "$SUT_LOCATION"
  else
    echo "app.jar already exists in $COVERAGE_PATH, skipping extraction."
  fi
fi


# Tear down Docker containers and volumes
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "$1-tear-down" "Tearing down Docker containers and volumes for TJOB $1"
docker compose -f docker-compose.yml --env-file "$WORKSPACE/.retorch/envfiles/$1.env" --ansi never -p "$1" down --volumes

# Return to the original working directory
cd "$WORKSPACE"

# START Custom Set-up commands
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "$1-set-up" "Start executing custom commands"
echo "This TJOB-teardown dont have any kind of specific commands"
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "$1-set-up" "End executing custom commands"
# END Custom Set-up commands

"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "$1-tear-down" "Tear-down ended"
# Execute the script to write timestamp again
"$SCRIPTS_FOLDER/writetime.sh" "$2" "$1"
