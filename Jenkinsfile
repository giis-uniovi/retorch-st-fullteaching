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
        stage('TJobC IdResource: Attenders LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobc 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobc 0 https://full-teaching- 5000 "UserTest#loginTest,FullTeachingEndToEndRESTTests#sessionRestOperations"'
              }// EndExecutionStageErrorTJobC
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobc 0'
          }// EndStepsTJobC
        }// EndStageTJobC
        stage('TJobD IdResource: Configuration LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobd 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {                                         //Course RW                          //Course RW,
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobd 0 https://full-teaching- 5000 "CourseTeacherTest#teacherEditCourseValues"'
              }// EndExecutionStageErrorTJobD
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobd 0'
          }// EndStepsTJobD
        }// EndStageTJobD
        stage('TJobE IdResource: Configuration LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobe 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {                                                          //Course RW, Openvidu RW
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobe 0 https://full-teaching- 5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome"'
              }// EndExecutionStageErrorTJobE
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobe 0'
          }// EndStepsTJobE
        }// EndStageTJobE
        stage('TJobF IdResource: Course LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobf 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {                                                          //Course RW
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobf 0 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#forumRestOperations"'
              }// EndExecutionStageErrorTJobF
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobf 0'
          }// EndStepsTJobF
        }// EndStageTJobF
        stage('TJobG IdResource: Course LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobg 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {                                                          //Course RW (REEMP CONF)
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobg 0 https://full-teaching- 5000 "UnLoggedLinksTests#spiderUnLoggedTest,FullTeachingEndToEndRESTTests#filesRestOperations"'
              }// EndExecutionStageErrorTJobG
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobg 0'
          }// EndStepsTJobG
        }// EndStageTJobG
     }// End Parallel
    }// End Stage
    stage('Stage 1'){
      failFast false
      parallel{
        stage('TJobM IdResource: Information LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobm 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobm 1 https://full-teaching- 5000 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome,FullTeachingLoggedVideoSessionTests#sessionTest"'
              }// EndExecutionStageErrorTJobH
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobm 1'
          }// EndStepsTJobH
        }// EndStageTJobH
        stage('TJobI IdResource: Files LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobi 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobi 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#attendersRestOperations,CourseTeacherTest#teacherCreateAndDeleteCourseTest"'
              }// EndExecutionStageErrorTJobI
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobi 1'
          }// EndStepsTJobI
        }// EndStageTJobI
        stage('TJobJ IdResource: Forum LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobj 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobj 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseInfoRestOperations,LoggedForumTest#forumNewCommentTest"'
              }// EndExecutionStageErrorTJobJ
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobj 1'
          }// EndStepsTJobJ
        }// EndStageTJobJ
        stage('TJobK IdResource: Forum LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobk 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobk 1 https://full-teaching- 5000 "CourseStudentTest#studentCourseMainTest,CourseTeacherTest#teacherCourseMainTest"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobk 1'
          }// EndStepsTJobK
        }// EndStageTJobK
        stage('TJobL IdResource: LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobl 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobl 1 https://full-teaching- 5000 "LoggedForumTest#forumNewEntryTest"'
              }// EndExecutionStageErrorTJobL
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobl 1'
          }// EndStepsTJobL
        }// EndStageTJobL
     }// End Parallel
    }// End Stage
    stage('Stage 2'){
      failFast false
      parallel{
        stage('TJobH IdResource: Session LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobh 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobh 2 https://full-teaching- 5000 "LoggedForumTest#forumNewReply2CommentTest,CourseTeacherTest#teacherDeleteCourseTest"'
              }// EndExecutionStageErrorTJobM
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobh 2'
          }// EndStepsTJobM
        }// EndStageTJobM
        stage('TJobN IdResource: Session LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobn 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobn 2 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseRestOperations,LoggedLinksTests#spiderLoggedTest,LoggedForumTest#forumLoadEntriesTest"'
              }// EndExecutionStageErrorTJobN
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobn 2'
          }// EndStepsTJobN
        }// EndStageTJobN
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
