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
- Spring Security
- JWT
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
Controller
   ↓
Service
   ↓
Repository
   ↓
Spring Data JPA
   ↓
MySQL

React (예정)
   ↑
REST API
```

---

## Main Features

- 회원가입 / 로그인
- JWT 기반 사용자 인증
- 영화 목록 및 상세 조회
- 영화 검색
- TMDB 영화 정보 조회 및 DB 저장
- 영화관 조회
- 상영관 조회
- 좌석 조회
- 영화 / 영화관 / 날짜 기준 상영 일정 조회
- 좌석 예매
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

### Auth API

회원가입:

```http
POST /api/auth/signup
```

로그인:

```http
POST /api/auth/login
```

로그인 성공 시 JWT Access Token을 반환합니다.

현재 로그인 사용자 조회:

```http
GET /api/users/me
Authorization: Bearer {accessToken}
```

### Theater API

전체 영화관 조회:

```http
GET /api/theaters
```

영화관 상세 조회:

```http
GET /api/theaters/{theaterId}
```

### Screen API

특정 영화관의 상영관 조회:

```http
GET /api/theaters/{theaterId}/screens
```

상영관 상세 조회:

```http
GET /api/screens/{screenId}
```

### Seat API

특정 상영관의 좌석 조회:

```http
GET /api/screens/{screenId}/seats
```

좌석 상세 조회:

```http
GET /api/seats/{seatId}
```

### Schedule API

영화, 영화관, 날짜 기준 상영 일정 조회:

```http
GET /api/schedules?movieId={movieId}&theaterId={theaterId}&date={yyyy-MM-dd}
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

### Movie
- [x] Movie Entity / Repository / Service 구현
- [x] Movie 응답 DTO 적용
- [x] TMDB 외부 API 연동
- [x] TMDB 인기 영화 조회
- [x] TMDB 영화 상세 정보 조회
- [x] TMDB 영화 데이터 MySQL 저장
- [x] TMDB 영화 중복 저장 방지
- [x] 영화 목록 조회
- [x] 영화 상세 조회
- [x] 영화 제목 검색
- [ ] Movie API 기능 고도화

### User / Authentication
- [x] User Entity / UserRole 구현
- [x] UserRepository 구현
- [x] 회원가입 요청 Validation
- [x] 이메일 / 닉네임 중복 검사
- [x] BCrypt 비밀번호 암호화
- [x] 회원가입 API 구현
- [x] 로그인 API 구현
- [x] JWT Access Token 발급
- [x] JWT 검증 및 사용자 식별
- [x] Spring Security 인증 설정
- [x] `GET /api/users/me` 구현
- [x] 공통 예외 처리
- [x] 로그아웃 방식 설계 (클라이언트에서 Access Token 삭제)

### Theater / Reservation Base
- [x] Theater Entity / Repository / Service / Controller 구현
- [x] 영화관 목록 / 상세 조회
- [x] Screen Entity / Repository / Service / Controller 구현
- [x] Theater 1:N Screen 관계 구현
- [x] 영화관별 상영관 조회
- [x] Seat Entity / Repository / Service / Controller 구현
- [x] Screen 1:N Seat 관계 구현
- [x] 상영관별 좌석 조회
- [x] Schedule Entity / Repository / Service / Controller 구현
- [x] Movie 1:N Schedule 관계 구현
- [x] Screen 1:N Schedule 관계 구현
- [x] 영화 / 영화관 / 날짜 기준 상영 일정 조회
- [x] 동일 상영관 / 동일 시작시간 중복 방지
- [ ] ScheduleSeat 구현
- [ ] Reservation 구현
- [ ] ReservationSeat 구현
- [ ] 예매 / 취소 / 내 예매 조회 구현

### Remaining
- [ ] 리뷰 기능 구현
- [ ] 커뮤니티 기능 구현
- [ ] 마이페이지 기능 확장
- [ ] React 프론트엔드 구현
- [ ] 테스트 보강
- [ ] Swagger / OpenAPI 문서화
- [ ] 배포

---

## Current Backend Flow

### Movie Data

```text
TMDB API
   ↓
TmdbClient
   ↓
MovieService
   ↓
MovieRepository
   ↓
MySQL
   ↓
Movie API
```

### Authentication

```text
회원가입 / 로그인
   ↓
AuthController
   ↓
AuthService
   ↓
BCrypt / JWT
   ↓
UserRepository
   ↓
MySQL
```

JWT 인증 요청:

```text
Client
   ↓
Authorization: Bearer JWT
   ↓
JwtAuthenticationFilter
   ↓
JwtProvider
   ↓
SecurityContext
   ↓
GET /api/users/me
```

### Theater / Schedule

```text
Movie ───────────────┐
                     ↓
                  Schedule
                     ↓
Theater ──→ Screen ──┴──→ Seat
```

다음 단계에서는 `ScheduleSeat`를 구현하여 특정 상영 일정에서 각 좌석의 예약 가능 상태와 가격을 관리할 예정입니다.

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
│   │   │   │       ├── config
│   │   │   │       ├── controller
│   │   │   │       ├── dto
│   │   │   │       │   ├── auth
│   │   │   │       │   ├── movie
│   │   │   │       │   ├── schedule
│   │   │   │       │   ├── screen
│   │   │   │       │   ├── seat
│   │   │   │       │   ├── theater
│   │   │   │       │   ├── tmdb
│   │   │   │       │   └── user
│   │   │   │       ├── entity
│   │   │   │       ├── exception
│   │   │   │       ├── repository
│   │   │   │       ├── security
│   │   │   │       └── service
│   │   │   └── resources
│   │   │       └── application.properties
│   │   └── test
│   ├── build.gradle
│   └── settings.gradle
├── .gitignore
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
export JWT_SECRET='YOUR_JWT_SECRET'
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

---

## Security

민감한 정보는 코드에 직접 작성하지 않고 환경변수로 관리합니다.

```properties
spring.datasource.password=${DB_PASSWORD}
tmdb.access-token=${TMDB_ACCESS_TOKEN}
jwt.secret=${JWT_SECRET}
```

실제 DB 비밀번호, TMDB Access Token, JWT Secret은 GitHub에 업로드하지 않습니다.

---

## Next Step

다음 개발 단계:

```text
ScheduleSeat
   ↓
Reservation
   ↓
ReservationSeat
   ↓
예매 / 취소 / 내 예매 조회
```

현재 전체 프로젝트 기준으로는 약 40~45% 정도 진행된 상태이며, 백엔드 기준으로는 절반 이상 구현된 상태입니다.
