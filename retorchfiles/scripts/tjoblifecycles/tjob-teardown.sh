#!/bin/bash
retorchfiles/scripts/writetime.sh "$2" "$1" #$2 is the stage, $1 tjob
docker compose --env-file "./retorchfiles/retorchdockercomposesenvfiles/${1}.env" --ansi never -p "$1" down --volumes
retorchfiles/scripts/writetime.sh "$2" "$1" #$2 is the stage, $1 tjob
