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
                        '/pinpung/DB_ENDPOINT',
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

                    paramConfig.each { item ->
                        def key = item.Name.tokenize('/').last()
                        env[key] = item.Value
                    }
                }
            }
        }
        stage('Build & Test') {
            steps {
                sh './gradlew clean build -x test'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    // Docker 이미지를 ECR 태그로 빌드
                    sh """
                    docker build -t pinpung-develop-backend:latest .
                    docker tag pinpung-develop-backend:latest ${ECR_REPO}:latest
                    """
                }
            }
        }
        stage('Push to ECR') {
            steps {
                script {
                    // ECR에 로그인 및 이미지 푸시
                    sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                    docker push ${ECR_REPO}:latest
                    """
                }
            }
        }
        stage('Deploy to EC2 with Docker Run') {
            steps {
                script {
                    // EC2에서 Docker 컨테이너 실행 시 환경 변수 전달
                    sh """
                    docker stop pinpung-develop-backend || true && docker rm pinpung-develop-backend || true
                    docker run -d --name pinpung-develop-backend \
                    -e DB_HOST=${DB_ENDPOINT} \
                    -e DB_NAME=${DB_NAME} \
                    -e DB_PASSWORD=${DB_PASSWORD} \
                    -e DB_PORT=${DB_PORT} \
                    -e DB_USERNAME=${DB_USERNAME} \
                    -e KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID} \
                    -e REDIRECT_URI=${REDIRECT_URI} \
                    -e S3_BUCKET_NAME=${S3_BUCKET_NAME} \
                    -p 8080:8080 ${ECR_REPO}:latest
                    """
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
