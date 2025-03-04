#!/bin/bash
ECR_REPO="528938155874.dkr.ecr.ap-northeast-2.amazonaws.com/pinpung/develop/backend"
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REPO
AWS_REGION="ap-northeast-2"
echo "Logging into ECR repository: $ECR_REPO"

# Parameter Store에서 환경 변수 가져오기
APP_NAME=$(aws ssm get-parameter --name "/pinpung/APP_NAME" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "APP_NAME: $APP_NAME"
DB_HOST=$(aws ssm get-parameter --name "/pinpung/DB_HOST" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "DB_HOST: $DB_HOST"
DB_NAME=$(aws ssm get-parameter --name "/pinpung/DB_NAME" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "DB_NAME: $DB_NAME"
DB_PASSWORD=$(aws ssm get-parameter --name "/pinpung/DB_PASSWORD" --with-decryption --query "Parameter.Value" --output text --region ap-northeast-2)
DB_PORT=$(aws ssm get-parameter --name "/pinpung/DB_PORT" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "DB_PORT: $DB_PORT"
DB_USERNAME=$(aws ssm get-parameter --name "/pinpung/DB_USERNAME" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "DB_USERNAME: $DB_USERNAME"
KAKAO_CLIENT_ID=$(aws ssm get-parameter --name "/pinpung/KAKAO_CLIENT_ID" --with-decryption --query "Parameter.Value" --output text --region ap-northeast-2)
echo "KAKAO_CLIENT_ID: $KAKAO_CLIENT_ID"
REDIRECT_URI=$(aws ssm get-parameter --name "/pinpung/REDIRECT_URI" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "REDIRECT_URI: $REDIRECT_URI"
S3_BUCKET_NAME=$(aws ssm get-parameter --name "/pinpung/S3_BUCKET_NAME" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "S3_BUCKET_NAME: $S3_BUCKET_NAME"
LOGOUT_REDIRECT_URI=$(aws ssm get-parameter --name "/pinpung/LOGOUT_REDIRECT_URI" --query "Parameter.Value" --output text --region ap-northeast-2)
echo "LOGOUT_REDIRECT_URI: $LOGOUT_REDIRECT_URI"
OPENAI_KEY=$(aws ssm get-parameter --name "/pinpung/OPENAI_KEY" --with-decryption --query "Parameter.Value" --output text --region $AWS_REGION)
FASTAPI_URL=$(aws ssm get-parameter --name "/pinpung/FASTAPI_URL" --with-decryption --query "Parameter.Value" --output text --region ap-northeast-2)
echo "FASTAPI_URL: $FASTAPI_URL"
# 최신 이미지 가져오기
docker pull ${ECR_REPO}:latest || { echo "Docker pull failed"; exit 1; }

# 이미지 태그 변경
docker tag ${ECR_REPO}:latest pinpung-backend:latest

# Docker 컨테이너 실행 시 환경 변수로 전달 및 CloudWatch 로그 드라이버 설정
docker stop pinpung-backend || true && docker rm pinpung-backend || true
docker run -d --name pinpung-backend \
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
    -e FASTAPI_URL=$FASTAPI_URL \
    -e APP_NAME=$APP_NAME \
    -p 8080:8080 pinpung-backend:latest
