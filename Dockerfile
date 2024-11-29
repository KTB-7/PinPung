# 1. 베이스 이미지 설정
FROM eclipse-temurin:17-jdk-jammy

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 소스 코드 전체 복사 (Gradle 관련 파일 포함)
COPY . .

# 4. Gradle wrapper 실행 권한 부여 및 캐시 방지
RUN chmod +x ./gradlew && \
    ./gradlew clean build -x test --no-daemon && \
    rm -rf ~/.gradle/caches

# 5. JAVA_OPTS 환경 변수 설정 (메모리 제한)
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 6. 빌드된 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/build/libs/pinpung-0.0.1-SNAPSHOT.jar"]

# 7. 포트 노출
EXPOSE 8080
