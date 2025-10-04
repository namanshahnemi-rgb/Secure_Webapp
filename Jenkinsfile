pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('SONAR_TOKEN') // Make sure this credential exists in Jenkins
    }

    stages {

        stage('Build') {
            steps {
                echo 'Installing PHP & PHPUnit...'
                bat '''
                REM Install PHP via Chocolatey
                choco install php --yes --force

                REM Download PHPUnit
                curl -L -o phpunit.phar https://phar.phpunit.de/phpunit-9.phar

                REM Verify installation
                C:\\tools\\php84\\php.exe phpunit.phar --version
                '''
            }
        }

        stage('Test') {
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
                            echo "No test report found, skipping junit publish."
                        }
                    }
                }
            }
        }

       stage('Code Quality') {
    steps {
        echo 'Running SonarCloud analysis...'
        // Use the Sonar token stored in Jenkins credentials
        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
            bat """
            mvn clean verify sonar:sonar ^
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
