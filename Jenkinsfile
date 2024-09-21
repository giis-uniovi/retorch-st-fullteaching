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
        stage('TJobC IdResource: Attenders LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobc 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobc 0 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#attendersRestOperations"'
              }// EndExecutionStageErrorTJobC
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobc 0'
          }// EndStepsTJobC
        }// EndStageTJobC
        stage('TJobD IdResource: Configuration LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobd 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobd 0 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseRestOperations,CourseTeacherTest#teacherEditCourseValues"'
              }// EndExecutionStageErrorTJobD
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobd 0'
          }// EndStepsTJobD
        }// EndStageTJobD
        stage('TJobE IdResource: Configuration LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobe 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobe 0 https://full-teaching- 5000 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome"'
              }// EndExecutionStageErrorTJobE
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobe 0'
          }// EndStepsTJobE
        }// EndStageTJobE
        stage('TJobF IdResource: Course LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobf 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobf 0 https://full-teaching- 5000 "LoggedLinksTests#spiderLoggedTest,UnLoggedLinksTests#spiderUnloggedTest,CourseTeacherTest#teacherDeleteCourseTest"'
              }// EndExecutionStageErrorTJobF
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobf 0'
          }// EndStepsTJobF
        }// EndStageTJobF

     }// End Parallel
    }// End Stage
    stage('Stage 1'){
      failFast false
      parallel{
        stage('TJobH IdResource: Information LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobh 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobh 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseInfoRestOperations"'
              }// EndExecutionStageErrorTJobH
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobh 1'
          }// EndStepsTJobH
        }// EndStageTJobH
        stage('TJobI IdResource: Files LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobi 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobi 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#filesRestOperations"'
              }// EndExecutionStageErrorTJobI
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobi 1'
          }// EndStepsTJobI
        }// EndStageTJobI
        stage('TJobJ IdResource: Forum LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobj 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobj 1 https://full-teaching- 5000 "LoggedForumTest#forumLoadEntriesTest"'
              }// EndExecutionStageErrorTJobJ
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobj 1'
          }// EndStepsTJobJ
        }// EndStageTJobJ
        stage('TJobK IdResource: Forum LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobk 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobk 1 https://full-teaching- 5000 "LoggedForumTest#forumNewCommentTest,LoggedForumTest#forumNewEntryTest,LoggedForumTest#forumNewReply2CommentTest,FullTeachingEndToEndRESTTests#forumRestOperations"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobk 1'
          }// EndStepsTJobK
        }// EndStageTJobK
     }// End Parallel
    }// End Stage
    stage('Stage 2'){
      failFast false
      parallel{
        stage('TJobM IdResource: Session LoginService OpenVidu ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobm 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobm 2 https://full-teaching- 5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome,FullTeachingLoggedVideoSessionTests#sessionTest"'
              }// EndExecutionStageErrorTJobM
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobm 2'
          }// EndStepsTJobM
        }// EndStageTJobM
        stage('TJobN IdResource: Session LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobn 2'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobn 2 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#sessionRestOperations"'
              }// EndExecutionStageErrorTJobN
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobn 2'
          }// EndStepsTJobN
        }// EndStageTJobN
        stage('TJobG IdResource: Course LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobg 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobg 0 https://full-teaching- 5000 "CourseStudentTest#studentCourseMainTest,CourseTeacherTest#teacherCourseMainTest,CourseTeacherTest#teacherCreateAndDeleteCourseTest"'
              }// EndExecutionStageErrorTJobG
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobg 0'
          }// EndStepsTJobG
        }// EndStageTJobG
        stage('TJobL IdResource: LoginService OpenViduMock ') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobl 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobl 1 https://full-teaching- 5000 "UserTest#loginTest"'
              }// EndExecutionStageErrorTJobL
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobl 1'
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
