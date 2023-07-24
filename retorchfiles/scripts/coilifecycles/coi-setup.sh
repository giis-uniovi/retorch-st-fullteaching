#!/bin/bash
OUTPUTDIRCOI="./src/retorchcostestimationdata/exec$BUILD_NUMBER/COI.data"
COISETUPSTART=$(date +%s%3N)

echo 'Remove older videos (7 days)! The number of videos prior remove :'
find /opt/selenoid/video/ | wc -l
find /opt/selenoid/video/ -mindepth 1 -maxdepth 1 -mtime +7 -name '*.mp4' | xargs rm -rf
echo 'Remove older videos (7 days)! The number of videos after remove : '
find /opt/selenoid/video/ | wc -l
echo 'Remove older logs (7days)! The number of logs prior remove :'
find /opt/selenoid/logs/ | wc -l
find /opt/selenoid/logs/ -mindepth 1 -maxdepth 1 -mtime +7 -name '*.log' | xargs rm -rf
echo 'Remove older logs (7days)! The number of logs prior remove : '
find /opt/selenoid/logs/ | wc -l
mkdir -p "./src/retorchcostestimationdata/exec$BUILD_NUMBER"
mkdir -p "./artifacts"

echo "Pulling the Docker Images requirede by the Test suite"
docker pull eexit/mirror-http-server
docker pull mysql:5.7.21
docker pull openvidu/openvidu-server-kms:1.7.0
docker pull codeurjc/full-teaching:demo
COISETUPEND=$(date +%s%3N)


echo "COI-SETUP-START;COI-SETUP-END;COI-TEARDOWN-START;COI-TEARDOWN-END" >"$OUTPUTDIRCOI"
echo -n "$COISETUPSTART;$COISETUPEND">>"$OUTPUTDIRCOI"

