#!groovy
pipeline {
  agent { docker 'maven' }
  stages {
    stage('Build') {
      steps {
        withSonarQubeEnv('default') {
          sh "mvn -B clean install sonar:sonar"
        }
        junit 'target/surefire-reports/*.xml'
      }
    }    
  }
  post {
    success {
      slackSend color: 'good', message: "Build success: ${env.JOB_NAME}!"
    }
    failure {
      slackSend color: 'danger', message: "Build failed: ${env.JOB_NAME}!"
    }
  }
}
