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
                    docker build -t pinpung-develop-backend:latest .
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
        stage('Generate and Upload AppSpec.yml to S3') {
            steps {
                script {
                    // appspec.yml 및 start_docker.sh 파일 생성 및 S3 업로드
                    writeFile file: 'appspec.yml', text: """
                    version: 0.0
                    os: linux
                    files:
                      - source: /
                        destination: /home/ec2-user/deploy
                    hooks:
                      ApplicationStart:
                        - location: scripts/start_docker.sh
                          timeout: 300
                          runas: ec2-user
                    """
                    writeFile file: 'scripts/start_docker.sh', text: '''
                    #!/bin/bash
                    # 데이터베이스 환경 변수 설정 및 Docker 실행
                    '''
                    sh """
                    aws s3 cp appspec.yml s3://${S3_BUCKET}/appspec.yml --region ${AWS_REGION}
                    aws s3 cp scripts/start_docker.sh s3://${S3_BUCKET}/scripts/start_docker.sh --region ${AWS_REGION}
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
                        --s3-location bucket=${S3_BUCKET},key=appspec.yml,bundleType=YAML \
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
    }
}
