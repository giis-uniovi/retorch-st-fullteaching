pipeline {
  agent {label 'xretorch-agent'}
  environment {
    SELENOID_PRESENT = "TRUE"
    SUT_LOCATION = "$WORKSPACE"
    SCRIPTS_FOLDER = "$WORKSPACE/retorchfiles/scripts"
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
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjoba 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjoba 0 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#attendersRestOperations"'
              }// EndExecutionStageErrorTJobA
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjoba 0'
          }// EndStepsTJobA
        }// EndStageTJobA
        stage('TJobB IdResource: Configuration LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobb 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobb 0 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseRestOperations,CourseTeacherTest#teacherEditCourseValues"'
              }// EndExecutionStageErrorTJobB
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobb 0'
          }// EndStepsTJobB
        }// EndStageTJobB
        stage('TJobC IdResource: Configuration LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobc 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobc 0 https://full-teaching- 5000 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome"'
              }// EndExecutionStageErrorTJobC
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobc 0'
          }// EndStepsTJobC
        }// EndStageTJobC
        stage('TJobD IdResource: Course LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobd 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobd 0 https://full-teaching- 5000 "LoggedLinksTests#spiderLoggedTest,UnLoggedLinksTests#spiderUnloggedTest,CourseTeacherTest#teacherDeleteCourseTest"'
              }// EndExecutionStageErrorTJobD
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobd 0'
          }// EndStepsTJobD
        }// EndStageTJobD
        stage('TJobE IdResource: Course LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobe 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobe 0 https://full-teaching- 5000 "CourseStudentTest#studentCourseMainTest,CourseTeacherTest#teacherCourseMainTest,CourseTeacherTest#teacherCreateAndDeleteCourseTest"'
              }// EndExecutionStageErrorTJobE
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobe 0'
          }// EndStepsTJobE
        }// EndStageTJobE
     }// End Parallel
    }// End Stage
    stage('Stage 1'){
      failFast false
      parallel{
        stage('TJobF IdResource: Information LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobf 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobf 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseInfoRestOperations"'
              }// EndExecutionStageErrorTJobF
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobf 1'
          }// EndStepsTJobF
        }// EndStageTJobF
        stage('TJobG IdResource: Files LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobg 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobg 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#filesRestOperations"'
              }// EndExecutionStageErrorTJobG
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobg 1'
          }// EndStepsTJobG
        }// EndStageTJobG
        stage('TJobH IdResource: Forum LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobh 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobh 1 https://full-teaching- 5000 "LoggedForumTest#forumLoadEntriesTest"'
              }// EndExecutionStageErrorTJobH
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobh 1'
          }// EndStepsTJobH
        }// EndStageTJobH
        stage('TJobI IdResource: Forum LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobi 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobi 1 https://full-teaching- 5000 "LoggedForumTest#forumNewCommentTest,LoggedForumTest#forumNewEntryTest,LoggedForumTest#forumNewReply2CommentTest,FullTeachingEndToEndRESTTests#forumRestOperations"'
              }// EndExecutionStageErrorTJobI
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobi 1'
          }// EndStepsTJobI
        }// EndStageTJobI
        stage('TJobJ IdResource: LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobj 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobj 1 https://full-teaching- 5000 "UserTest#loginTest"'
              }// EndExecutionStageErrorTJobJ
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobj 1'
          }// EndStepsTJobJ
        }// EndStageTJobJ
     }// End Parallel
    }// End Stage
    stage('Stage 2'){
      failFast false
      parallel{
        stage('TJobK IdResource: Session LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobk 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobk 2 https://full-teaching- 5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome,FullTeachingLoggedVideoSessionTests#sessionTest"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobk 2'
          }// EndStepsTJobK
        }// EndStageTJobK
        stage('TJobL IdResource: Session LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobl 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobl 2 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#sessionRestOperations"'
              }// EndExecutionStageErrorTJobL
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobl 2'
          }// EndStepsTJobL
        }// EndStageTJobL
     }// End Parallel
    }// End Stage
stage('TEARDOWN-Infrastructure') {
      steps {
        sh '$SCRIPTS_FOLDER/coilifecycles/coi-teardown.sh'
      }// EndStepsTearDownInf
}// EndStageTearDown
  }// EndStagesPipeline
 post { 
      always {
          archiveArtifacts artifacts: 'artifacts/*.csv', onlyIfSuccessful: true
          archiveArtifacts artifacts: 'target/testlogs/**/*.*', onlyIfSuccessful: false
          archiveArtifacts artifacts: 'target/containerlogs/**/*.*', onlyIfSuccessful: false
      }//EndAlways
 }//EndPostActions
}// EndPipeline 
