pipeline {
  agent {label 'xretorch-agent'}
  environment {
    SELENOID_PRESENT = "TRUE"
    SUT_LOCATION = "$WORKSPACE"
    SCRIPTS_FOLDER = "$WORKSPACE/retorchfiles/scripts"
    EXEC_PATH="$WORKSPACE/target/coverage"
  }// EndEnvironment
  options {
    disableConcurrentBuilds()
  }// EndPipOptions
  stages{
    stage('Clean Workspace') {
        steps{
            cleanWs()
        }// EndStepsCleanWS
      }// EndStageCleanWS
    stage('Clone Project') {
        steps{
            checkout scm
        }// EndStepsCloneProject
      }// EndStageCloneProject
    stage('SETUP-Infrastructure') {
        steps{
            sh 'chmod +x -R $SCRIPTS_FOLDER'
            sh '$SCRIPTS_FOLDER/coilifecycles/coi-setup.sh'
        }// EndStepsSETUPINF
      }// EndStageSETUPInf

           stage('20') {
             steps {
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobu 18'
                 catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                     sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobu 18 https://full-teaching- 5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome"'
                 }// EndExecutionStageErrorTJobM
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobu 18'
             }// EndStepsTJobM
                     }// EndStageTJobM
           stage('7 ') {
             steps {
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobe 2'
                 catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                     sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobe 2 https://full-teaching- 5000 "CourseTeacherTest#teacherEditCourseValues"'
                 }// EndExecutionStageErrorTJobD
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobe 2'
             }// EndStepsTJobD
           }// EndStageTJobD
           stage('21') {
             steps {
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobf 3'
                 catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                     sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobf 3 https://full-teaching- 5000 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome"'
                 }// EndExecutionStageErrorTJobE
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobf 3'
             }// EndStepsTJobE
           }// EndStageTJobE

           stage('16') {
             steps {
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobs 16'
                 catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                     sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobs 16 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#forumRestOperations"'
                 }// EndExecutionStageErrorTJobK
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobs 16'
             }// EndStepsTJobK
           }// EndStageTJobK

           stage('17') {
             steps {
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobn 11'
                 catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                     sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobn 11 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#filesRestOperations"'
                 }// EndExecutionStageErrorTJobI
                 sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobn 11'
             }// EndStepsTJobI
           }// EndStageTJobI


stage('TEARDOWN-Infrastructure') {
      steps {
        sh '$SCRIPTS_FOLDER/coilifecycles/coi-teardown.sh'
      }// EndStepsTearDownInf
}// EndStageTearDown

stage('PUBLISH COV REPORT') {
      steps {

          sh 'echo "Change directory to the coverage ones"'
          sh 'cd "$EXEC_PATH"'
          sh 'echo "Merging everything into a single exec file"'

          sh 'find "$EXEC_PATH" -type f -name "*.exec" > exec_files.txt'
          sh 'cat ./exec_files.txt'
          sh 'java -jar "$EXEC_PATH/org.jacoco.cli-0.8.13-nodeps.jar" merge --destfile "$EXEC_PATH/merged.exec" $(cat "./exec_files.txt")'

          sh 'echo "Generating report (HTML)!"'
          sh 'java -jar "$EXEC_PATH/org.jacoco.cli-0.8.13-nodeps.jar" report "$EXEC_PATH/merged.exec" \
               --classfiles "$EXEC_PATH/classes" \
               --sourcefiles "$WORKSPACE/coverage/code/java" \
               --html ./jacoco-report \
               --name FullCoverageReport'
          sh 'echo "Generating report (XML)!"'
          sh 'java -jar "$EXEC_PATH/org.jacoco.cli-0.8.13-nodeps.jar" report "$EXEC_PATH/merged.exec" \
              --classfiles "$EXEC_PATH/classes" \
              --sourcefiles "$WORKSPACE/coverage/code/java" \
              --xml ./jacoco-report/coverage.xml \
              --name FullCoverageReport'

          publishHTML(
              allowMissing: true,
              alwaysLinkToLastBuild: true,
              keepAll: false,
              reportDir: "jacoco-report",
              reportFiles: 'index.html',
              reportName: 'FullCoverageReport'
          )


      }// EndStepsTearDownInf
}// EndStageTearDown
  }// EndStagesPipeline
 post { 
      always {
          archiveArtifacts artifacts: 'jacoco-report/coverage.xml', onlyIfSuccessful: true
          archiveArtifacts artifacts: 'artifacts/*.csv', onlyIfSuccessful: true
          archiveArtifacts artifacts: 'target/testlogs/**/*.*', onlyIfSuccessful: false
          archiveArtifacts artifacts: 'target/containerlogs/**/*.*', onlyIfSuccessful: false
      }//EndAlways
 }//EndPostActions
}// EndPipeline 
