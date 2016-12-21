stage('Build') {
node {
  checkout scm
 
  docker.image('maven').inside("-e MAVEN_CONFIG=${pwd tmp: true}/m2repo") {
    withSonarQubeEnv {
      sh "mvn -Dmaven.repo.local=${pwd tmp: true}/m2repo -B clean package sonar:sonar"
    }
    
  }
  //junit 'target/surefire-reports/*.xml'
}
}
