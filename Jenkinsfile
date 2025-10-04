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
        echo 'Running SonarCloud analysis (PHP)...'
        withCredentials([string(credentialsId: 'SONAR_TOKEN', variable: 'SONAR_TOKEN')]) {
            bat '''
            sonar-scanner ^
              -Dsonar.projectKey=Secure_Webapp ^
              -Dsonar.organization=namanshahnemi-rgb ^
              -Dsonar.host.url=https://sonarcloud.io ^
              -Dsonar.sources=. ^
              -Dsonar.php.coverage.reportPaths=test-report.xml ^
              -Dsonar.login=%SONAR_TOKEN%
            '''
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
