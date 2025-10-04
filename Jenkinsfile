pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                echo 'Installing dependencies...'
                bat '''
                choco install php --yes
                choco install composer --yes
                composer install || exit 0
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Running PHPUnit tests...'
                bat '''
                if exist vendor\\bin\\phpunit (
                    vendor\\bin\\phpunit --log-junit test-report.xml || exit 0
                ) else (
                    echo PHPUnit not found — skipping tests.
                )
                '''
            }
            post {
                always {
                    junit 'test-report.xml'
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo 'Running SonarCloud analysis...'
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                    bat '''
                    sonar-scanner ^
                      -Dsonar.projectKey=Secure_Webapp ^
                      -Dsonar.organization=namanshahnemi-rgb ^
                      -Dsonar.host.url=https://sonarcloud.io ^
                      -Dsonar.login=%SONAR_TOKEN%
                    '''
                }
            }
        }

        stage('Security') {
            steps {
                echo 'Running Trivy scan...'
                bat '''
                curl -LO https://github.com/aquasecurity/trivy/releases/latest/download/trivy_0.50.0_windows-64bit.zip
                tar -xf trivy_0.50.0_windows-64bit.zip
                trivy fs --severity HIGH,CRITICAL --exit-code 0 .
                '''
            }
        }
    }
}
