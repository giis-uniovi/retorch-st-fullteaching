pipeline {
  agent {label 'slave-xretorch'}
  environment {
        ET_EUS_API = "http://selenoid:4444/wd/hub"
        CURRENT_DATE = " date +\'[%m-%d-%y] %T - \'"
        //Used to avoid the UnixHTTPConnectionPool(host='localhost', port=None): Read timed out and
        DOCKER_CLIENT_TIMEOUT = 120
        COMPOSE_HTTP_TIMEOUT = 120
    }//EndEnvironment
  options {
      disableConcurrentBuilds()
    }//EndPipOptions
  stages{
    stage('SETUP-Infrastructure') {
        steps{
           //Change the permisions to the scripts folder the 
            sh 'chmod +x -R ./retorchfiles/scripts'
            sh './retorchfiles/scripts/coilifecycles/coi-setup.sh'
        }// EndStepsSETUPINF
      }//EndStageSETUPInf
    stage('Stage 0'){
      failFast false
      parallel{
        stage('TJobC IdResource: Attenders LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobc 0'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobc 0 "FullTeachingEndToEndRESTTests#attendersRestOperations"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobc 0'
            }//EndExecutionStageErrorTJobC
          }//EndStepsTJobC
        }//EndStageTJobC
        stage('TJobD IdResource: Configuration LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobd 0'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobd 0 "FullTeachingEndToEndRESTTests#courseRestOperations,CourseTeacherTest#teacherEditCourseValues"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobd 0'
            }//EndExecutionStageErrorTJobD
          }//EndStepsTJobD
        }//EndStageTJobD
        stage('TJobE IdResource: Configuration LoginService OpenVidu ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobe 0'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobe 0 "FullTeachingEndToEndEChatTests#oneToOneChatInSessionChrome"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobe 0'
            }//EndExecutionStageErrorTJobE
          }//EndStepsTJobE
        }//EndStageTJobE
        stage('TJobF IdResource: Course LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobf 0'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobf 0 "LoggedLinksTests#spiderLoggedTest,UnLoggedLinksTests#spiderUnloggedTest,CourseTeacherTest#teacherDeleteCourseTest"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobf 0'
            }//EndExecutionStageErrorTJobF
          }//EndStepsTJobF
        }//EndStageTJobF
        stage('TJobG IdResource: Course LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobg 0'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobg 0 "CourseStudentTest#studentCourseMainTest,CourseTeacherTest#teacherCourseMainTest,CourseTeacherTest#teacherCreateAndDeleteCourseTest"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobg 0'
            }//EndExecutionStageErrorTJobG
          }//EndStepsTJobG
        }//EndStageTJobG
     }//End Parallel
    }//End Stage
    stage('Stage 1'){
      failFast false
      parallel{
        stage('TJobH IdResource: Information LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobh 1'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobh 1 "FullTeachingEndToEndRESTTests#courseInfoRestOperations"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobh 1'
            }//EndExecutionStageErrorTJobH
          }//EndStepsTJobH
        }//EndStageTJobH
        stage('TJobI IdResource: Files LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobi 1'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobi 1 "FullTeachingEndToEndRESTTests#filesRestOperations"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobi 1'
            }//EndExecutionStageErrorTJobI
          }//EndStepsTJobI
        }//EndStageTJobI
        stage('TJobJ IdResource: Forum LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobj 1'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobj 1 "LoggedForumTest#forumLoadEntriesTest"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobj 1'
            }//EndExecutionStageErrorTJobJ
          }//EndStepsTJobJ
        }//EndStageTJobJ
        stage('TJobK IdResource: Forum LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobk 1'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobk 1 "LoggedForumTest#forumNewCommentTest,LoggedForumTest#forumNewEntryTest,LoggedForumTest#forumNewReply2CommentTest,FullTeachingEndToEndRESTTests#forumRestOperations"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobk 1'
            }//EndExecutionStageErrorTJobK
          }//EndStepsTJobK
        }//EndStageTJobK
        stage('TJobL IdResource: LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobl 1'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobl 1 "UserTest#loginTest"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobl 1'
            }//EndExecutionStageErrorTJobL
          }//EndStepsTJobL
        }//EndStageTJobL
     }//End Parallel
    }//End Stage
    stage('Stage 2'){
      failFast false
      parallel{
        stage('TJobM IdResource: Session LoginService OpenVidu ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobm 2'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobm 2 "FullTeachingTestEndToEndVideoSessionTests#oneToOneVideoAudioSessionChrome,FullTeachingLoggedVideoSessionTests#sessionTest"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobm 2'
            }//EndExecutionStageErrorTJobM
          }//EndStepsTJobM
        }//EndStageTJobM
        stage('TJobN IdResource: Session LoginService OpenViduMock ') {
          steps {
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-setup.sh tjobn 2'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-testexecution.sh tjobn 2 "FullTeachingEndToEndRESTTests#sessionRestOperations"'
              sh 'retorchfiles/scripts/tjoblifecycles/tjob-teardown.sh tjobn 2'
            }//EndExecutionStageErrorTJobN
          }//EndStepsTJobN
        }//EndStageTJobN
     }//End Parallel
    }//End Stage
    stage('TEARDOWN-Infrastructure'){
      stages{
        stage('PublishReports'){  
          steps{
            sh 'retorchfiles/scripts/coilifecycles/coi-teardown.sh'
            sh 'retorchfiles/scripts/saveTJobLifecycledata.sh'
            publishHTML (target : [allowMissing: false,
              alwaysLinkToLastBuild: true,
              keepAll: true,
              reportDir: 'target/site/',
              reportFiles: 'surefire-report.html',
              reportName: 'Test Execution Report'])

          }//EndStepsPublishReports
        }//EndStagePublishReports
      }//EndStagesINFTearDown

    }//EndStageTearDown
}//EndStagesPipeline
 post { 
      always {
          archiveArtifacts artifacts: 'artifacts/*.csv', onlyIfSuccessful: true      }//EndAlways
      cleanup { 
          cleanWs()
          sh """(eval \$CURRENT_DATE ; echo "Cleaning Environment ") | cat |tr '\n' ' ' """
          echo 'Switch off all containers...'
          sh """docker stop \$(docker ps | grep tjob | awk '{print \$1}') || echo 'All the containers are stopped!'""" 
          sh """docker rm --volumes \$(docker ps -a | grep tjob | awk '{print \$1}') || echo 'All the containers are removed!'""" 
      }//EndCleanUp
 }//EndPostActions
}//EndPipeline 
