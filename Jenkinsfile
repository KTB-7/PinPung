pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-northeast-2'
        AWS_ACCOUNT_ID = '528938155874'
        S3_BUCKET = 'pinpung-develop-codedeploy-configs'
        ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/pinpung/develop/backend"
        CODE_DEPLOY_APP_NAME = 'pinpung-develop-backend'
        CODE_DEPLOY_GROUP = 'pinpung-develop-backend-deploy-group'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    sh """
                    docker build --no-cache -t pinpung-develop-backend:latest .
                    docker tag pinpung-develop-backend:latest ${ECR_REPO}:latest
                    """
                }
            }
        }
        stage('Push to ECR') {
            steps {
                script {
                    sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                    docker push ${ECR_REPO}:latest
                    """
                }
            }
        }
        stage('Prepare Deployment Bundle') {
            steps {
                script {
                    sh """
                    zip -r deploy_bundle.zip appspec.yml scripts/
                    """
                }
            }
        }
        stage('Upload Deployment Bundle to S3') {
            steps {
                script {
                    sh """
                    aws s3 cp deploy_bundle.zip s3://${S3_BUCKET}/deploy_bundle.zip --region ${AWS_REGION}
                    """
                }
            }
        }
        stage('Trigger CodeDeploy') {
            steps {
                script {
                    // CodeDeploy 트리거
                    sh """
                    aws deploy create-deployment \
                        --application-name ${CODE_DEPLOY_APP_NAME} \
                        --deployment-group-name ${CODE_DEPLOY_GROUP} \
                        --s3-location bucket=${S3_BUCKET},key=deploy_bundle.zip,bundleType=zip \
                        --region ${AWS_REGION}
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
        always {
            script {
                // 빌드 후 Docker 리소스 완전 정리
                sh "docker system prune -af --volumes"
            }
        }
    }
}
