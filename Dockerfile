# Base image 설정 (Java 17을 사용하는 경우)
FROM eclipse-temurin:17-jdk-jammy

# 작업 디렉토리 설정
WORKDIR /app

# 소스 코드 전체 복사
COPY . .

# gradlew에 실행 권한 부여
RUN chmod +x ./gradlew

# 테스트 제외한 Gradle 빌드 실행
RUN ./gradlew build -x test

# JAVA_OPTS 환경 변수 설정 (메모리 제한)
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 빌드된 JAR 파일을 바로 실행
ENTRYPOINT ["java", "-jar", "/app/build/libs/pinpung-0.0.1-SNAPSHOT.jar"]

# 포트 노출 (Spring Boot 기본 포트는 8080)
EXPOSE 8080
