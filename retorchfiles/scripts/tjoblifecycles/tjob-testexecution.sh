#!/bin/bash
URL="https://full-teaching-$1:5000"
#https://stackoverflow.com/questions/4365418/get-state-of-maven-execution-within-shell-script
set -e
retorchfiles/scripts/writetime.sh "$2" "$1" #$2 is the stage, $1 tjob
mvn -Dapp.url="${URL}" -Dtest="$3" test -Ddirtarget="$1"
retorchfiles/scripts/writetime.sh "$2" "$1" #$2 is the stage, $1 tjob
#if [[$EXECUTION_RESULT -eq 0]]
#  then
#        echo "EXECUTION TJOB $1 FINISHED"
#        return 0
#  else
#      echo "SOMETHING GO WRONG WITH TJOB $1"
#      return 1
#  fi
