#!/bin/bash
# Copyright (c) Software Engineering Research Group (GIIS), University of Oviedo.
# All intellectual property rights on this file belong to GIIS.
# The coi-teardown.sh script provides all the necessary commands to tear-down the infrastructure after executing
# the TJobs. It performs cleaning actions, like remove all the containers and docker volumes, but also ends, collects
# and creates file with the different TJob and COI lifecycles using the savetjoblifecycledata.sh script.

if [ "$#" -ne 0 ]; then
    "$SCRIPTS_FOLDER/printLog.sh" "ERROR" "COI-tear-down" "Usage: $0 - This script does not take any parameters"
    exit 1
fi

COITEARDOWNSTART="$(date +%s%3N)"
# Log the start of the container teardown process
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Switching off all containers that start with *tjob*..."
# Get the list of container IDs to stop and remove
container_ids=$(docker ps -a -q --filter name=tjob)
if [ -n "$container_ids" ]; then
    # Stop the containers
    if docker stop $container_ids; then
        "$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Successfully stopped containers: $container_ids"
    else
        "$SCRIPTS_FOLDER/printLog.sh" "ERROR" "COI-tear-down" "Failed to stop containers: $container_ids"
        exit 1
    fi
    # Collect the volumes used by the containers, one container at a time (a single filter cannot take several ids)
    volumes_to_prune=""
    for container_id in $container_ids; do
        volumes_to_prune="$volumes_to_prune $(docker inspect --format '{{range .Mounts}}{{if eq .Type "volume"}}{{.Name}} {{end}}{{end}}' "$container_id")"
    done
    # Remove the containers
    if docker rm $container_ids; then
        "$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Successfully removed containers: $container_ids"
    else
        "$SCRIPTS_FOLDER/printLog.sh" "ERROR" "COI-tear-down" "Failed to remove containers: $container_ids"
        exit 1
    fi
    # Prune the collected volumes
       if [ -n "$volumes_to_prune" ]; then
           for volume in $volumes_to_prune; do
               if docker volume rm "$volume"; then
                   "$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Successfully removed volume: $volume"
               else
                   "$SCRIPTS_FOLDER/printLog.sh" "ERROR" "COI-tear-down" "Failed to remove volume: $volume"
               fi
           done
    else
        "$SCRIPTS_FOLDER/printLog.sh" "INFO" "COI-tear-down" "No volumes associated with containers to prune."
    fi
else
    "$SCRIPTS_FOLDER/printLog.sh" "INFO" "COI-tear-down" "No containers with names starting with 'tjob' found to stop and remove."
fi
# START Custom COI tear-down commands
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Start executing custom commands"
# Coverage (JaCoCo) report of the whole execution: merges the exec files dumped by every TJob.
# This code is inserted into coi-teardown.sh, so it must never exit the script: the steps use return instead.
EXEC_PATH="$WORKSPACE/target/coverage"
JACOCO_ENABLED="${JACOCO_ENABLED:-true}"

generate_coverage_report() {
  echo "Change directory to the coverage ones"
  cd "$EXEC_PATH" || return 0
  JACOCO_JAR="$EXEC_PATH/org.jacoco.cli-0.8.13-nodeps.jar"
  if [ ! -f "$JACOCO_JAR" ]; then
    echo "Jacoco CLI jar not found at $JACOCO_JAR, skipping report generation"
    return 0
  fi
  find "$EXEC_PATH" -type f -name "*.exec" > exec_files.txt
  if [ ! -s exec_files.txt ]; then
    echo "No .exec files found, skipping report generation"
    return 0
  fi
  echo "Merging exec files"
  java -jar "$JACOCO_JAR" merge --destfile "$EXEC_PATH/merged.exec" $(cat exec_files.txt)
  mkdir -p jacoco-report
  echo "Generating HTML report"
  java -jar "$JACOCO_JAR" report "$EXEC_PATH/merged.exec" \
    --classfiles "$EXEC_PATH/classes" \
    --sourcefiles "$WORKSPACE/coverage/code/java" \
    --html ./jacoco-report \
    --name FullCoverageReport
  echo "Generating XML report"
  java -jar "$JACOCO_JAR" report "$EXEC_PATH/merged.exec" \
    --classfiles "$EXEC_PATH/classes" \
    --sourcefiles "$WORKSPACE/coverage/code/java" \
    --xml ./jacoco-report/coverage.xml \
    --name FullCoverageReport
}

if [ "$JACOCO_ENABLED" = "true" ]; then
  generate_coverage_report
fi
cd "$WORKSPACE"

"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "End executing custom commands"
# END Custom COI tear-down commands
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Teardown process completed successfully."

COITEARDOWNEND="$(date +%s%3N)"
OUTPUTDIRCOI="$WORKSPACE/retorchcostestimationdata/exec$BUILD_NUMBER/COI.data"
# The COI tear-down timestamps must be written BEFORE savetjoblifecycledata.sh, which reads them from this file
echo -n ";$COITEARDOWNSTART;$COITEARDOWNEND" >> "$OUTPUTDIRCOI"

# Run saveTJobLifecycledata.sh script
echo "Running saveTJobLifecycledata.sh script..."
sh "$SCRIPTS_FOLDER/savetjoblifecycledata.sh"
"$SCRIPTS_FOLDER/printLog.sh" "DEBUG" "COI-tear-down" "Script completed successfully."
