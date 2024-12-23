pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-northeast-2'
        TARGET_EC2 = 'ec2-user@15.165.241.239'
        ECR_REPO = '528938155874.dkr.ecr.ap-northeast-2.amazonaws.com/pinpung/develop/backend'
        DOCKER_IMAGE_TAG = 'latest'
        APP_DIR = '/home/ec2-user/app'
    }
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/KTB-7/backend.git'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                    docker build --no-cache -t pinpung-backend:${DOCKER_IMAGE_TAG} .
                    docker tag pinpung-backend:${DOCKER_IMAGE_TAG} ${ECR_REPO}:${DOCKER_IMAGE_TAG}
                    """
                }
            }
        }
        stage('Push to ECR') {
            steps {
                script {
                    sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                    docker push ${ECR_REPO}:${DOCKER_IMAGE_TAG}
                    """
                }
            }
        }
        stage('Deploy to backend EC2') {
            steps {
                sshagent(['ec2-ssh-key']) {
                    script {
                // 환경 변수 가져오기
                        def dbHost = sh(script: "aws ssm get-parameter --name /pinpung/DB_HOST --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def dbUser = sh(script: "aws ssm get-parameter --name /pinpung/DB_USERNAME --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def dbPort = sh(script: "aws ssm get-parameter --name /pinpung/DB_PORT --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def dbName = sh(script: "aws ssm get-parameter --name /pinpung/DB_NAME --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def dbPassword = sh(script: "aws ssm get-parameter --name /pinpung/DB_PASSWORD --query Parameter.Value --output text --with-decryption --region ${AWS_REGION}", returnStdout: true).trim()
                        def openaiKey = sh(script: "aws ssm get-parameter --name /pinpung/OPENAI_KEY --query Parameter.Value --output text --with-decryption --region ${AWS_REGION}", returnStdout: true).trim()
                        def kakaoClientId = sh(script: "aws ssm get-parameter --name /pinpung/KAKAO_CLIENT_ID --query Parameter.Value --output text --with-decryption --region ${AWS_REGION}", returnStdout: true).trim()
                        def redirectUri = sh(script: "aws ssm get-parameter --name /pinpung/REDIRECT_URI --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def s3BucketName = sh(script: "aws ssm get-parameter --name /pinpung/S3_BUCKET_NAME --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def logoutRedirectUri = sh(script: "aws ssm get-parameter --name /pinpung/LOGOUT_REDIRECT_URI --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def fastApiUrl = sh(script: "aws ssm get-parameter --name /pinpung/FASTAPI_URL --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                        def appName = sh(script: "aws ssm get-parameter --name /pinpung/APP_NAME --query Parameter.Value --output text --region ${AWS_REGION}", returnStdout: true).trim()
                // EC2에서 Docker 컨테이너 실행
                sh """
                timeout 300 ssh -t -o StrictHostKeyChecking=no ${TARGET_EC2} <<EOF
# ECR 인증
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
# 기존 컨테이너 삭제
docker stop pinpung-backend || true
docker rm pinpung-backend || true
docker pull ${ECR_REPO}:${DOCKER_IMAGE_TAG}
echo "Running new container..."
docker run -d -p 8080:8080 \
    --memory="512m" \
    --memory-swap="2g" \
    --log-driver=awslogs \
    --log-opt awslogs-region=${AWS_REGION} \
    --log-opt awslogs-group=pinpung-backend-ec2-logs \
    --log-opt awslogs-stream=\$(curl -s http://169.254.169.254/latest/meta-data/instance-id) \
    -e AWS_REGION=${AWS_REGION} \
    -e DB_HOST=${dbHost} \
    -e DB_NAME=${dbName} \
    -e DB_PASSWORD=${dbPassword} \
    -e DB_PORT=${dbPort} \
    -e DB_USERNAME=${dbUser} \
    -e OPENAI_KEY=${openaiKey} \
    -e KAKAO_CLIENT_ID=${kakaoClientId}
    -e REDIRECT_URI=${redirectUri}
    -e S3_BUCKET_NAME=${s3BucketName}
    -e LOGOUT_REDIRECT_URI=${logoutRedirectUri}
    -e FASTAPI_URL=${fastApiUrl}
    -e APP_NAME=${appName}
    --name pinpung-backend ${ECR_REPO}:${DOCKER_IMAGE_TAG}
echo "Deployment completed successfully."
EOF
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
        always {
            script {
                sh "docker system prune -af --volumes"
            }
        }
    }
}
