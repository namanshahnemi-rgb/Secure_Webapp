pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN') // Jenkins secret text credential
    }

    stages {

        stage('Build PHP') {
            steps {
                echo 'Installing PHP & PHPUnit...'
                bat '''
                REM Install PHP via Chocolatey (if needed)
                choco install php --yes --force

                REM Download PHPUnit
                curl -L -o phpunit.phar https://phar.phpunit.de/phpunit-9.phar

                REM Verify installation
                C:\\tools\\php84\\php.exe phpunit.phar --version
                '''
            }
        }

        stage('Test PHP') {
            steps {
                echo 'Running PHPUnit tests...'
                bat '''
                REM Run PHPUnit tests and generate XML report
                C:\\tools\\php84\\php.exe phpunit.phar --log-junit test-report.xml || exit 0
                '''
            }
            post {
                always {
                    script {
                        if (fileExists('test-report.xml')) {
                            junit 'test-report.xml'
                        } else {
                            echo "No PHP test report found, skipping junit publish."
                        }
                    }
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo 'Running SonarCloud analysis using Maven wrapper...'
                withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
                    bat """
                    REM Use Maven wrapper to run SonarCloud scan
                    .\\mvnw clean verify sonar:sonar ^
                      -Dsonar.projectKey=Secure_Webapp ^
                      -Dsonar.organization=namanshahnemi-rgb ^
                      -Dsonar.host.url=https://sonarcloud.io ^
                      -Dsonar.login=%SONAR_TOKEN%
                    """
                }
            }
        }

        stage('Security') {
            steps {
                echo 'Running Trivy scan...'
                bat '''
                REM Download Trivy
                curl -LO https://github.com/aquasecurity/trivy/releases/latest/download/trivy_0.50.0_windows-64bit.zip

                REM Extract Trivy
                powershell -Command "Expand-Archive trivy_0.50.0_windows-64bit.zip -DestinationPath . -Force"

                REM Run Trivy scan
                .\\trivy.exe fs --severity HIGH,CRITICAL --exit-code 0 .
                '''
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished!'
        }
    }
}
