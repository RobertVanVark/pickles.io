#!groovy
try {
  stage('Build') {
    node {
      checkout scm

      docker.image('maven').inside("-e MAVEN_CONFIG=${pwd tmp: true}/m2repo") {
        withSonarQubeEnv {
          sh "mvn -Dmaven.repo.local=${pwd tmp: true}/m2repo -B clean install sonar:sonar"
        }

      }
      junit '**/target/surefire-reports/*.xml'
    }
  }
  slackSend color: 'good', message: "Build success: ${env.JOB_NAME}!"
} catch (e) {
  slackSend color: 'danger', message: "Build failed: ${env.JOB_NAME}!"
  throw e
}
