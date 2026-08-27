# Movie Platform

영화 검색, 상영 정보 조회, 좌석 예매, 리뷰 및 커뮤니티 기능을 제공하는 영화 플랫폼 프로젝트입니다.

여러 영화관의 상영 정보를 한눈에 비교하고, 영화 관람 이후에는 리뷰와 커뮤니티를 통해 다른 사용자들과 의견을 나눌 수 있는 서비스를 목표로 합니다.

> 현재는 백엔드 기능을 중심으로 개발 중이며, 실제 영화관 예매 시스템 연동이 아닌 자체 데이터를 기반으로 기능을 구현하고 있습니다.  
> 영화 정보는 TMDB API를 활용하여 조회한 뒤 필요한 데이터를 자체 MySQL 데이터베이스에 저장하여 사용합니다.

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

### External API
- TMDB API

### Tools
- Git / GitHub
- MySQL Workbench
- VS Code
- Postman (예정)
- Swagger / OpenAPI (예정)

---

## Architecture

```text
TMDB API
   ↓
Spring Boot
   ↓
Service
   ↓
Spring Data JPA
   ↓
MySQL
   ↑
REST API
   ↑
React (예정)
```

---

## Main Features

- 회원가입 / 로그인
- 영화 목록 및 상세 조회
- 영화 검색
- TMDB 영화 정보 조회 및 DB 저장
- 영화관 및 상영시간 조회
- 좌석 조회 및 예매
- 예매 취소 및 예매 내역 조회
- 영화 리뷰 및 평점
- 커뮤니티 게시글 / 댓글
- 마이페이지

---

## API

### Hello API

```http
GET /hello
```

Response:

```text
Hello
```

### Movie API

전체 영화 조회:

```http
GET /api/movies
```

영화 상세 조회:

```http
GET /api/movies/{movieId}
```

영화 제목 검색:

```http
GET /api/movies?keyword={keyword}
```

### TMDB API

TMDB 인기 영화 조회:

```http
GET /api/tmdb/popular
```

TMDB 영화 상세 조회:

```http
GET /api/tmdb/movies/{movieId}
```

TMDB 영화 정보를 자체 DB에 저장:

```http
POST /api/tmdb/movies/{movieId}/save
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
- [x] Movie Entity / Repository / Service 구현
- [x] TMDB 외부 API 연동
- [x] TMDB 인기 영화 조회
- [x] TMDB 영화 상세 정보 조회
- [x] TMDB 영화 데이터 MySQL 저장
- [x] TMDB 영화 중복 저장 방지
- [x] Movie API 기본 구현
- [x] 영화 목록 조회
- [x] 영화 상세 조회
- [x] 영화 제목 검색
- [x] Movie 응답 DTO 적용
- [ ] Movie API 기능 고도화
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
│   │   │   │       ├── client
│   │   │   │       │   └── TmdbClient.java
│   │   │   │       ├── controller
│   │   │   │       │   ├── HelloController.java
│   │   │   │       │   ├── MovieController.java
│   │   │   │       │   └── TmdbController.java
│   │   │   │       ├── dto
│   │   │   │       │   ├── movie
│   │   │   │       │   │   └── MovieResponseDto.java
│   │   │   │       │   └── tmdb
│   │   │   │       │       ├── TmdbGenreDto.java
│   │   │   │       │       ├── TmdbMovieDetailDto.java
│   │   │   │       │       ├── TmdbMovieDto.java
│   │   │   │       │       └── TmdbMovieResponse.java
│   │   │   │       ├── entity
│   │   │   │       │   └── Movie.java
│   │   │   │       ├── repository
│   │   │   │       │   └── MovieRepository.java
│   │   │   │       └── service
│   │   │   │           └── MovieService.java
│   │   │   └── resources
│   │   │       └── application.properties
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
export TMDB_ACCESS_TOKEN='YOUR_TMDB_ACCESS_TOKEN'
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

영화 목록 API:

```text
http://localhost:8080/api/movies
```

TMDB 인기 영화 API:

```text
http://localhost:8080/api/tmdb/popular
```

---

## Security

민감한 정보는 코드에 직접 작성하지 않고 환경변수로 관리합니다.

```properties
spring.datasource.password=${DB_PASSWORD}
tmdb.access-token=${TMDB_ACCESS_TOKEN}
```

실제 DB 비밀번호 및 TMDB Access Token은 GitHub에 업로드하지 않습니다.
