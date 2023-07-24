#!/bin/bash

COITEARDOWNSTART="$(date +%s%3N)"

echo 'Switch off all containers...'
docker stop "$(docker ps | grep tjob | awk '{print \$1}')" || echo 'All the containers are stopped!'
docker rm --volumes "$(docker ps -a | grep tjob | awk '{print \$1}')" || echo 'All the containers are removed!'
sh 'retorchfiles/scripts/saveTJobLifecycledata.sh'
COITEARDOWNEND="$(date +%s%3N)"

OUTPUTDIRCOI="./src/retorchcostestimationdata/exec$BUILD_NUMBER/COI.data"
echo -n ";$COITEARDOWNSTART;$COITEARDOWNEND" >>"$OUTPUTDIRCOI"

