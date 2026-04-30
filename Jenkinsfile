pipeline {
  agent {label 'xretorch-agent'}
  environment {
    SELENOID_PRESENT = "TRUE"
    SUT_LOCATION = "$WORKSPACE"
    SCRIPTS_FOLDER = "$WORKSPACE/.retorch/scripts"
  } // EndEnvironment
  options {
    disableConcurrentBuilds()
  } // EndPipOptions
  stages {
    stage('Clean Workspace') {
        steps {
            cleanWs()
        } // EndStepsCleanWS
    } // EndStageCleanWS
    stage('Clone Project') {
        steps {
            checkout scm
        } // EndStepsCloneProject
    } // EndStageCloneProject
    stage('SETUP-Infrastructure') {
        steps {
            sh 'chmod +x -R $SCRIPTS_FOLDER'
            sh '$SCRIPTS_FOLDER/coilifecycles/coi-setup.sh'
        } // EndStepsSETUPINF
    } // EndStageSETUPInf
    stage('Stage 0') {
      failFast false
      parallel {
        stage('tjoba IdResource: attenders executor loginservice openvidumock webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjoba 0 https://full-teaching-tjoba:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjoba 0 https://full-teaching-tjoba:5000 "FullTeachingEndToEndRESTTests#attendersRestOperations"'
            }// EndExecutionStageErrortjoba
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjoba 0'
          }// EndStepstjoba
        }// EndStagetjoba
        stage('tjobb IdResource: configuration executor loginservice openvidumock webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobb 0 https://full-teaching-tjobb:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobb 0 https://full-teaching-tjobb:5000 "FullTeachingEndToEndRESTTests#courseRestOperations,CourseTeacherTest#teacherEditCourseValues"'
            }// EndExecutionStageErrortjobb
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobb 0'
          }// EndStepstjobb
        }// EndStagetjobb
        stage('tjobc IdResource: executor files loginservice openvidumock webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobc 0 https://full-teaching-tjobc:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobc 0 https://full-teaching-tjobc:5000 "FullTeachingEndToEndRESTTests#filesRestOperations"'
            }// EndExecutionStageErrortjobc
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobc 0'
          }// EndStepstjobc
        }// EndStagetjobc
        stage('tjobd IdResource: configuration executor loginservice openvidu webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobd 0 https://full-teaching-tjobd:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobd 0 https://full-teaching-tjobd:5000 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome"'
            }// EndExecutionStageErrortjobd
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobd 0'
          }// EndStepstjobd
        }// EndStagetjobd
        stage('tjobe IdResource: course executor loginservice openvidumock webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobe 0 https://full-teaching-tjobe:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobe 0 https://full-teaching-tjobe:5000 "LoggedLinksTests#spiderLoggedTest,UnLoggedLinksTests#spiderUnloggedTest,CourseStudentTest#studentCourseMainTest,CourseTeacherTest#teacherCourseMainTest,CourseTeacherTest#teacherCreateAndDeleteCourseTest,CourseTeacherTest#teacherDeleteCourseTest"'
            }// EndExecutionStageErrortjobe
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobe 0'
          }// EndStepstjobe
        }// EndStagetjobe
      } // EndParallel
    } // EndStage
    stage('Stage 1') {
      failFast false
      parallel {
        stage('tjobf IdResource: executor information loginservice openvidumock webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobf 1 https://full-teaching-tjobf:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobf 1 https://full-teaching-tjobf:5000 "FullTeachingEndToEndRESTTests#courseInfoRestOperations"'
            }// EndExecutionStageErrortjobf
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobf 1'
          }// EndStepstjobf
        }// EndStagetjobf
        stage('tjobg IdResource: executor forum loginservice openvidumock webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobg 1 https://full-teaching-tjobg:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobg 1 https://full-teaching-tjobg:5000 "LoggedForumTest#forumLoadEntriesTest,LoggedForumTest#forumNewCommentTest,LoggedForumTest#forumNewEntryTest,LoggedForumTest#forumNewReply2CommentTest,FullTeachingEndToEndRESTTests#forumRestOperations"'
            }// EndExecutionStageErrortjobg
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobg 1'
          }// EndStepstjobg
        }// EndStagetjobg
        stage('tjobh IdResource: executor loginservice openvidu session webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobh 1 https://full-teaching-tjobh:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobh 1 https://full-teaching-tjobh:5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome,FullTeachingLoggedVideoSessionTests#sessionTest"'
            }// EndExecutionStageErrortjobh
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobh 1'
          }// EndStepstjobh
        }// EndStagetjobh
        stage('tjobi IdResource: executor loginservice openvidumock session webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobi 1 https://full-teaching-tjobi:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobi 1 https://full-teaching-tjobi:5000 "FullTeachingEndToEndRESTTests#sessionRestOperations"'
            }// EndExecutionStageErrortjobi
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobi 1'
          }// EndStepstjobi
        }// EndStagetjobi
      } // EndParallel
    } // EndStage
    stage('Stage 2') {
      failFast false
      parallel {
        stage('tjobj IdResource: executor loginservice openvidu webbrowser webserver ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobj 2 https://full-teaching-tjobj:5000'
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobj 2 https://full-teaching-tjobj:5000 "UserTest#loginTest"'
            }// EndExecutionStageErrortjobj
            sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobj 2'
          }// EndStepstjobj
        }// EndStagetjobj
      } // EndParallel
    } // EndStage
stage('TEARDOWN-Infrastructure') {
      failFast false
      steps {
          sh '$SCRIPTS_FOLDER/coilifecycles/coi-teardown.sh'
      } // EndStepsTearDownInf
} // EndStageTearDown
} // EndStagesPipeline
post {
    always {
        archiveArtifacts artifacts: 'artifacts/*.csv', onlyIfSuccessful: true
        archiveArtifacts artifacts: 'target/testlogs/**/*.*', onlyIfSuccessful: false
        archiveArtifacts artifacts: 'target/containerlogs/**/*.*', onlyIfSuccessful: false
    }// EndAlways
} // EndPostActions
} // EndPipeline
