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
    stage('Stage 0'){
      failFast false
      parallel{
        stage('TJobA IdResource: Attenders LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobc 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobc 0 https://full-teaching- 5000 "UserTest#loginTest,UnLoggedLinksTests#spiderUnloggedTest,CourseTeacherTest#teacherEditCourseValues,FullTeachingEndToEndRESTTests#forumRestOperations"'
              }// EndExecutionStageErrorTJobC
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobc 0'
          }// EndStepsTJobC
        }// EndStageTJobC
        stage('TJobB IdResource: Configuration LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobe 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobe 0 https://full-teaching- 5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome"'
              }// EndExecutionStageErrorTJobE
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobe 0'
          }// EndStepsTJobE
        }// EndStageTJobE
     }// End Parallel
    }// End Stage
    stage('Stage 2'){
      failFast false
      parallel{
        stage('TJobC IdResource: Session LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobm 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobm 2 https://full-teaching- 5000 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome"'
              }// EndExecutionStageErrorTJobM
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobm 2'
          }// EndStepsTJobM
        }// EndStageTJobM
     }// End Parallel
    }// End Stage
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
