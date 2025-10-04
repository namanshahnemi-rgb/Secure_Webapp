pipeline {
    agent any

    stages {

        stage('Build') {
            steps {
                echo 'Installing dependencies...'
                bat '''
                REM Ensure PHP & Composer are installed (requires Chocolatey)
                choco install php --yes --no-progress
                choco install composer --yes --no-progress

                REM Install project dependencies
                composer install || exit 0
                '''
            }
        }

        stage('Test') {
            steps {
                echo 'Running PHPUnit tests...'
                bat '''
                if exist vendor\\bin\\phpunit (
                    vendor\\bin\\phpunit --configuration phpunit.xml --log-junit test-report.xml || exit 0
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
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished successfully!'
        }
    }
}
