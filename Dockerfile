# 1. 베이스 이미지 설정
FROM eclipse-temurin:17-jdk-jammy

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 변경 빈도가 낮은 Gradle 관련 파일 복사
COPY gradlew settings.gradle build.gradle /app/
COPY gradle /app/gradle

# 4. Gradle wrapper 실행 권한 부여
RUN chmod +x ./gradlew

# 5. 의존성 다운로드
RUN ./gradlew dependencies --no-daemon

# 6. 소스 코드 전체 복사 (변경 빈도가 잦음)
COPY . .

# 7. 테스트를 제외하고 Gradle 빌드 실행
RUN ./gradlew clean build -x test --no-daemon

# 8. JAVA_OPTS 환경 변수 설정 (메모리 제한)
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 9. 빌드된 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/build/libs/pinpung-0.0.1-SNAPSHOT.jar", "$JAVA_OPTS"]

# 10. 포트 노출
EXPOSE 8080

