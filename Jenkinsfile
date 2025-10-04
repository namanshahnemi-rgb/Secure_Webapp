pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                echo 'Installing PHP & PHPUnit...'
                bat '''
                choco install php --yes --force
                curl -L -o phpunit.phar https://phar.phpunit.de/phpunit-9.phar
                C:\\tools\\php84\\php.exe phpunit.phar --version
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Running PHPUnit tests...'
                bat '''
                C:\\tools\\php84\\php.exe phpunit.phar --log-junit test-report.xml || exit 0
                '''
            }
            post {
                always {
                    script {
                        if (fileExists('test-report.xml')) {
                            junit 'test-report.xml'
                        } else {
                            echo "No test report found, skipping junit publish."
                        }
                    }
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
