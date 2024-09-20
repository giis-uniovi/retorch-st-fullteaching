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
        stage('18') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobc 0'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobc 0 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#attendersRestOperations"'
              }// EndExecutionStageErrorTJobC
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobc 0'
          }// EndStepsTJobC
        }// EndStageTJobC
        stage('13') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobd 1'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobd 1 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseRestOperations"'
              }// EndExecutionStageErrorTJobD
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobd 1'
          }// EndStepsTJobD
        }// EndStageTJobD
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
        stage('2') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobg 4'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobg 4 https://full-teaching- 5000 "LoggedLinksTests#spiderLoggedTest"'
              }// EndExecutionStageErrorTJobF
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobg 4'
          }// EndStepsTJobF
        }// EndStageTJobF
        stage('3') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobh 5'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobh 5 https://full-teaching- 5000 "UnLoggedLinksTests#spiderUnloggedTest"'
              }// EndExecutionStageErrorTJobF
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobh 5'
          }// EndStepsTJobF
        }// EndStageTJobF
        stage('8') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobi 6'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobi 6 https://full-teaching- 5000 "CourseTeacherTest#teacherDeleteCourseTest"'
              }// EndExecutionStageErrorTJobF
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobi 6'
          }// EndStepsTJobF
        }// EndStageTJobF
        stage('4') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobj 7'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobj 7 https://full-teaching- 5000 "CourseStudentTest#studentCourseMainTest"'
              }// EndExecutionStageErrorTJobG
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobj 7'
          }// EndStepsTJobG
        }// EndStageTJobG
        stage('5') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobk 8'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobk 8 https://full-teaching- 5000 "CourseTeacherTest#teacherCourseMainTest"'
              }// EndExecutionStageErrorTJobG
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobk 8'
          }// EndStepsTJobG
        }// EndStageTJobG

        stage('6') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobl 9'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobl 9 https://full-teaching- 5000 "CourseTeacherTest#teacherCreateAndDeleteCourseTest"'
              }// EndExecutionStageErrorTJobG
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobl 9'
          }// EndStepsTJobG
        }// EndStageTJobG
        stage('14') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobm 10'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobm 10 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#courseInfoRestOperations"'
              }// EndExecutionStageErrorTJobH
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobm 10'
          }// EndStepsTJobH
        }// EndStageTJobH
        stage('17') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobn 11'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobn 11 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#filesRestOperations"'
              }// EndExecutionStageErrorTJobI
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobn 11'
          }// EndStepsTJobI
        }// EndStageTJobI
        stage('9') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobo 12'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobo 12 https://full-teaching- 5000 "LoggedForumTest#forumLoadEntriesTest"'
              }// EndExecutionStageErrorTJobJ
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobo 12'
          }// EndStepsTJobJ
        }// EndStageTJobJ
        stage('11') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobp 13'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobp 13 https://full-teaching- 5000 "LoggedForumTest#forumNewCommentTest"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobp 13'
          }// EndStepsTJobK
        }// EndStageTJobK
        stage('10') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobq 14'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobq 14 https://full-teaching- 5000 "LoggedForumTest#forumNewEntryTest"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobq 14'
          }// EndStepsTJobK
        }// EndStageTJobK
        stage('12') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobr 15'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobr 15 https://full-teaching- 5000 "LoggedForumTest#forumNewReply2CommentTest"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobr 15'
          }// EndStepsTJobK
        }// EndStageTJobK
        stage('16') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobs 16'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobs 16 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#forumRestOperations"'
              }// EndExecutionStageErrorTJobK
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobs 16'
          }// EndStepsTJobK
        }// EndStageTJobK
        stage('1') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobt 17'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobt 17 https://full-teaching- 5000 "UserTest#loginTest"'
              }// EndExecutionStageErrorTJobL
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobt 17'
          }// EndStepsTJobL
        }// EndStageTJobL
        stage('20') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobu 18'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobu 18 https://full-teaching- 5000 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome"'
              }// EndExecutionStageErrorTJobM
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobu 18'
          }// EndStepsTJobM
                  }// EndStageTJobM
        stage('19') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobv 19'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobv 19 https://full-teaching- 5000 "FullTeachingLoggedVideoSessionTests#sessionTest"'
              }// EndExecutionStageErrorTJobM
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobv 19'
          }// EndStepsTJobM
        }// EndStageTJobM
        stage('15') {
          steps {
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-setup.sh tjobw 20'
              catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                  sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-testexecution.sh tjobw 20 https://full-teaching- 5000 "FullTeachingEndToEndRESTTests#sessionRestOperations"'
              }// EndExecutionStageErrorTJobN
              sh '$SCRIPTS_FOLDER/tjoblifecycles/tjob-teardown.sh tjobw 20'
          }// EndStepsTJobN
        }// EndStageTJobN
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
