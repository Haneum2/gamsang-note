# Render 배포용: React 빌드 -> Spring Boot 에 포함 -> 하나의 JAR 로 실행 (서비스 1개)
# 빌드 위치는 이 파일이 있는 폴더(저장소 루트)다.

# 1단계: 프론트 빌드
FROM node:22-alpine AS web
WORKDIR /web
COPY gamsang-note-web/package*.json ./
RUN npm install
COPY gamsang-note-web/ ./
RUN npm run build

# 2단계: 백엔드 빌드 (React 결과물을 static 폴더에 넣는다)
FROM maven:3.9-eclipse-temurin-17 AS api
WORKDIR /api
COPY gamsang-note/pom.xml ./
RUN mvn -q -B dependency:go-offline
COPY gamsang-note/src ./src
COPY --from=web /web/dist ./src/main/resources/static
RUN mvn -q -B -DskipTests package

# 3단계: 실행
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=api /api/target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
# 무료 인스턴스 메모리(512MB)에 맞춘다
ENV JAVA_TOOL_OPTIONS="-Xmx300m"
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
