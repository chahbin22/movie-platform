# Movie Platform

영화 검색, 상영 정보 조회, 좌석 예매, 리뷰 및 커뮤니티 기능을 제공하는 영화 플랫폼 프로젝트입니다.

여러 영화관의 상영 정보를 한눈에 비교하고, 영화 관람 이후에는 리뷰와 커뮤니티를 통해 다른 사용자들과 의견을 나눌 수 있는 서비스를 목표로 합니다.

> 현재는 백엔드 기능을 중심으로 개발 중이며, 실제 영화관 예매 시스템 연동이 아닌 자체 데이터를 기반으로 기능을 구현하고 있습니다.

---

## Tech Stack

### Backend
- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- MySQL
- Gradle
- Lombok
- Validation

### Frontend
- React (예정)

### Tools
- Git / GitHub
- MySQL Workbench
- VS Code
- Postman (예정)
- Swagger / OpenAPI (예정)

---

## Architecture

```text
React
  ↓
REST API
  ↓
Spring Boot
  ↓
Spring Data JPA
  ↓
MySQL
```

---

## Main Features

- 회원가입 / 로그인
- 영화 목록 및 상세 조회
- 영화 검색
- 영화관 및 상영시간 조회
- 좌석 조회 및 예매
- 예매 취소 및 예매 내역 조회
- 영화 리뷰 및 평점
- 커뮤니티 게시글 / 댓글
- 마이페이지

---

## API

현재 구현된 테스트 API:

### Hello API

```http
GET /hello
```

Response:

```text
Hello
```

---

## Current Progress

- [x] 프로젝트 기획
- [x] 사용자 시나리오 설계
- [x] 화면 구조 설계
- [x] ERD 설계
- [x] REST API 설계
- [x] Spring Boot 프로젝트 초기 설정
- [x] MySQL 연동
- [x] Git / GitHub 연동
- [x] 기본 Controller 테스트 (`GET /hello`)
- [ ] Movie API 구현
- [ ] 회원 인증 구현
- [ ] 예매 기능 구현
- [ ] 리뷰 / 커뮤니티 구현
- [ ] React 프론트엔드 구현
- [ ] 테스트 및 예외 처리
- [ ] 배포

---

## Project Structure

```text
movie-platform
├── backend
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com.movieplatform.backend
│   │   │   │       └── controller
│   │   │   └── resources
│   │   └── test
│   ├── build.gradle
│   └── settings.gradle
└── README.md
```

---

## Run Backend

MySQL 서버 실행 후 환경변수를 설정합니다.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
export DB_PASSWORD='YOUR_MYSQL_PASSWORD'
```

백엔드 실행:

```bash
cd backend
./gradlew bootRun
```

서버 기본 주소:

```text
http://localhost:8080
```

테스트 API:

```text
http://localhost:8080/hello
```
