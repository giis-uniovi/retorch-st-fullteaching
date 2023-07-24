#!/bin/bash
retorchfiles/scripts/writetime.sh "$2" "$1" #$2 is the stage, $1 tjob
docker compose --env-file "./retorchfiles/retorchdockercomposesenvfiles/${1}.env" --ansi never -p "$1" up -d
echo "Waiting for the SUT... "
retorchfiles/scripts/waitForFullteaching.sh "$1"
echo "SUT READY!"
retorchfiles/scripts/writetime.sh "$2" "$1" #$2 is the stage, $1 tjob
