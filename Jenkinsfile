pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-northeast-2'
        AWS_ACCOUNT_ID = '528938155874'
        ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/pinpung/develop/backend"
        JAVA_HOME = '/usr/lib/jvm/java-17-amazon-corretto.x86_64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Fetch Parameters from Parameter Store') {
            steps {
                script {
                    def parameterNames = [
                        '/pinpung/DB_HOST',
                        '/pinpung/DB_NAME',
                        '/pinpung/DB_PASSWORD',
                        '/pinpung/DB_PORT',
                        '/pinpung/DB_USERNAME',
                        '/pinpung/KAKAO_CLIENT_ID',
                        '/pinpung/REDIRECT_URI',
                        '/pinpung/S3_BUCKET_NAME'
                    ]
                    def params = parameterNames.join(',')
                    def result = sh(script: "aws ssm get-parameters --names ${params} --with-decryption --query 'Parameters[*].{Name:Name,Value:Value}' --region ${AWS_REGION} --output json", returnStdout: true).trim()
                    def paramConfig = new groovy.json.JsonSlurper().parseText(result)

                    // withEnv에서 사용할 key=value 형태로 설정
                    envVars = paramConfig.collect { item ->
                        def key = item.Name.tokenize('/').last()
                        return "${key}=${item.Value}"
                    }
                    echo "Fetched environment variables"
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                withEnv(envVars) {
                    script {
                        sh """
                        docker build -t pinpung-develop-backend:latest .
                        docker tag pinpung-develop-backend:latest ${ECR_REPO}:latest
                        """
                    }
                }
            }
        }
        stage('Push to ECR') {
            steps {
                withEnv(envVars) {
                    script {
                        sh """
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                        docker push ${ECR_REPO}:latest
                        """
                    }
                }
            }
        }
        stage('Deploy to EC2 with Docker Run') {
            steps {
                withEnv(envVars) {
                    script {
                        sh """
                        docker stop pinpung-develop-backend || true && docker rm pinpung-develop-backend || true
                        docker run -d --name pinpung-develop-backend \
                        -e DB_HOST=${env.DB_HOST} \
                        -e DB_NAME=${env.DB_NAME} \
                        -e DB_PASSWORD=${env.DB_PASSWORD} \
                        -e DB_PORT=${env.DB_PORT} \
                        -e DB_USERNAME=${env.DB_USERNAME} \
                        -e KAKAO_CLIENT_ID=${env.KAKAO_CLIENT_ID} \
                        -e REDIRECT_URI=${env.REDIRECT_URI} \
                        -e S3_BUCKET_NAME=${env.S3_BUCKET_NAME} \
                        -p 8080:8080 ${ECR_REPO}:latest
                        """
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed.'
        }
    }
}
