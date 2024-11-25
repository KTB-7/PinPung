#!/bin/bash
ECR_REPO="528938155874.dkr.ecr.ap-northeast-2.amazonaws.com/pinpung/develop/backend"
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REPO
AWS_REGION="ap-northeast-2"

# Parameter Store에서 환경 변수 가져오기
DB_HOST=$(aws ssm get-parameter --name "/pinpung/DB_HOST" --query "Parameter.Value" --output text --region ap-northeast-2)
DB_NAME=$(aws ssm get-parameter --name "/pinpung/DB_NAME" --query "Parameter.Value" --output text --region ap-northeast-2)
DB_PASSWORD=$(aws ssm get-parameter --name "/pinpung/DB_PASSWORD" --with-decryption --query "Parameter.Value" --output text --region ap-northeast-2)
DB_PORT=$(aws ssm get-parameter --name "/pinpung/DB_PORT" --query "Parameter.Value" --output text --region ap-northeast-2)
DB_USERNAME=$(aws ssm get-parameter --name "/pinpung/DB_USERNAME" --query "Parameter.Value" --output text --region ap-northeast-2)
KAKAO_CLIENT_ID=$(aws ssm get-parameter --name "/pinpung/KAKAO_CLIENT_ID" --with-decryption --query "Parameter.Value" --output text --region ap-northeast-2)
REDIRECT_URI=$(aws ssm get-parameter --name "/pinpung/REDIRECT_URI" --query "Parameter.Value" --output text --region ap-northeast-2)
S3_BUCKET_NAME=$(aws ssm get-parameter --name "/pinpung/S3_BUCKET_NAME" --query "Parameter.Value" --output text --region ap-northeast-2)
LOGOUT_REDIRECT_URI=$(aws ssm get-parameter --name "/pinpung/LOGOUT_REDIRECT_URI" --query "Parameter.Value" --output text --region ap-northeast-2)
OPENAI_KEY=$(aws ssm get-parameter --name "/pinpung/OPENAI_KEY" --with-decryption --query "Parameter.Value" --output text --region ap-northeast-2)

# Docker 컨테이너 실행 시 환경 변수로 전달 및 CloudWatch 로그 드라이버 설정
docker stop pinpung-develop-backend || true && docker rm pinpung-develop-backend || true
docker run -d --name pinpung-develop-backend \
    --log-driver=awslogs \
    --log-opt awslogs-region=$AWS_REGION \
    --log-opt awslogs-group=pinpung-backend-ec2-logs \
    --log-opt awslogs-stream=$(curl -s http://169.254.169.254/latest/meta-data/instance-id) \
    -e AWS_REGION=$AWS_REGION \
    -e DB_HOST=$DB_HOST \
    -e DB_NAME=$DB_NAME \
    -e DB_PASSWORD=$DB_PASSWORD \
    -e DB_PORT=$DB_PORT \
    -e DB_USERNAME=$DB_USERNAME \
    -e KAKAO_CLIENT_ID=$KAKAO_CLIENT_ID \
    -e REDIRECT_URI=$REDIRECT_URI \
    -e S3_BUCKET_NAME=$S3_BUCKET_NAME \
    -e LOGOUT_REDIRECT_URI=$LOGOUT_REDIRECT_URI \
    -e OPENAI_KEY=$OPENAI_KEY \
    -p 8080:8080 ${ECR_REPO}:latest
