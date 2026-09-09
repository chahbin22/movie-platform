# Movie Platform

영화 정보 조회부터 상영 일정 확인, 좌석 선택, 예매, 리뷰 및 커뮤니티 기능까지 제공하는 영화 플랫폼 프로젝트입니다.

영화 데이터는 TMDB API를 활용하며, 극장/상영관/좌석/상영 일정과 예매 데이터는 자체 데이터베이스에서 관리합니다.

---

## 1. 프로젝트 개요

기존 영화 서비스의 주요 기능을 직접 구현해보는 것을 목표로 개발한 웹 애플리케이션입니다.

단순한 영화 정보 조회에 그치지 않고 다음 흐름이 하나의 서비스 안에서 동작하도록 구성했습니다.

```text
영화 조회
    ↓
영화 상세
    ↓
극장 / 날짜 선택
    ↓
상영 시간 선택
    ↓
좌석 선택
    ↓
예매
    ↓
내 예매 확인 / 취소
```

추가로 사용자 리뷰와 커뮤니티 기능을 구현했습니다.

---

## 2. 주요 기능

### 회원

- 회원가입
- 로그인
- BCrypt 비밀번호 암호화
- JWT 기반 인증
- 로그인 사용자 정보 조회
- 로그인 상태에 따른 프론트엔드 UI 처리

### 영화

- 전체 영화 조회
- 영화 제목 검색
- 영화 상세 조회
- TMDB 인기 영화 조회
- TMDB 영화 상세 조회
- TMDB 영화 개별 저장
- TMDB 영화 자동 동기화

### TMDB 자동 동기화

Spring Boot 실행 시 TMDB의 영화 데이터를 자동으로 가져와 자체 데이터베이스와 동기화합니다.

```text
TMDB Popular
+
TMDB Now Playing
        ↓
영화 ID 중복 제거
        ↓
영화 상세 정보 조회
        ↓
tmdbMovieId 기준 확인
        ↓
신규 영화 → INSERT
기존 영화 → UPDATE
```

동기화 시 기존 `movieId`를 유지하면서 영화 정보만 갱신하므로 해당 영화와 연결된 리뷰 및 상영 일정 등의 데이터가 유지됩니다.

자동 동기화 시점:

- 서버 시작 시 1회
- 매일 오전 3시
- 시간대: Asia/Seoul

TMDB API 호출 실패가 전체 애플리케이션 실행 실패로 이어지지 않도록 동기화 과정에서 예외를 처리합니다.

---

## 3. 영화 예매

### 극장

- 극장 목록 조회
- 극장 상세 조회

### 상영관

- 극장별 상영관 조회
- 상영관 상세 조회

### 좌석

- 상영관별 좌석 조회
- 좌석 행/번호 관리

### 상영 일정

다음 조건을 이용해 상영 일정을 검색할 수 있습니다.

- 영화
- 극장
- 날짜

### 상영 좌석

각 상영 일정에 대해 실제 상영관 좌석을 기반으로 `ScheduleSeat`을 생성합니다.

```text
Screen
  ↓
Seat

Schedule
  ↓
ScheduleSeat
```

같은 상영 일정의 좌석 데이터가 이미 존재하면 중복 생성하지 않습니다.

### 예매

- 상영 일정 선택
- 좌석 선택
- 여러 좌석 동시 예매
- 총 가격 계산
- 로그인 사용자별 예매 내역 조회
- 예매 취소
- 취소 시 좌석 상태 복구

---

## 4. 동시성 제어

동일한 좌석을 여러 사용자가 동시에 예매하는 문제를 방지하기 위해 JPA의 비관적 락을 사용했습니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

예매 과정에서 선택한 `ScheduleSeat`을 Lock 상태로 조회한 후 좌석 상태를 확인합니다.

```text
AVAILABLE
    ↓
예매 요청
    ↓
PESSIMISTIC_WRITE
    ↓
상태 재확인
    ↓
RESERVED
```

이를 통해 동일 좌석에 대한 중복 예매를 방지합니다.

---

## 5. 리뷰

영화별 리뷰 기능을 제공합니다.

- 리뷰 조회
- 리뷰 작성
- 리뷰 수정
- 리뷰 삭제
- 평점 1~5
- 본인이 작성한 리뷰만 수정/삭제 가능
- 한 사용자당 한 영화에 하나의 리뷰 작성

---

## 6. 커뮤니티

### 게시글

- 게시글 목록
- 게시글 상세
- 게시글 작성
- 게시글 수정
- 게시글 삭제
- 조회수
- 작성자 권한 확인

### 댓글

- 댓글 조회
- 댓글 작성
- 댓글 수정
- 댓글 삭제
- 작성자 권한 확인

---

## 7. 데모 데이터

실제 극장 API와 연결하지 않고 자체 데모 데이터를 사용합니다.

서버 실행 시 다음 데이터를 초기화합니다.

```text
데모 극장
    ↓
상영관
    ↓
좌석
    ↓
상영 일정
    ↓
ScheduleSeat
```

현재 데모 데이터 구성:

- 데모 극장 5개
- 극장별 상영관 2개
- 상영관별 좌석 30개
- TMDB에서 저장된 영화 일부 사용
- 오늘부터 이후 날짜의 상영 일정 생성
- 평일/주말 가격 구분

데모 데이터는 기존 데이터를 확인한 후 생성하여 서버를 재실행하더라도 동일한 데이터가 계속 중복 생성되지 않도록 처리했습니다.

> 실제 CGV, 롯데시네마, 메가박스의 실시간 상영 정보 및 예매 시스템과 연결된 프로젝트는 아닙니다.

---

## 8. 기술 스택

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- Gradle
- Lombok
- Bean Validation
- RestClient

### Database

- MySQL

### Frontend

- React
- Vite
- JavaScript
- React Router
- Axios
- CSS

### External API

- TMDB API

### API Documentation

- Swagger
- Springdoc OpenAPI

---

## 9. 시스템 구조

```text
                    ┌───────────────┐
                    │    React      │
                    │    Vite       │
                    └───────┬───────┘
                            │
                            │ HTTP / JSON
                            ▼
                    ┌───────────────┐
                    │  Spring Boot  │
                    │     REST      │
                    └───────┬───────┘
                            │
             ┌──────────────┴──────────────┐
             │                             │
             ▼                             ▼
      ┌─────────────┐               ┌─────────────┐
      │    MySQL    │               │  TMDB API   │
      └─────────────┘               └─────────────┘
```

---

## 10. 주요 엔티티

```text
User

Movie
 ├── Review
 └── Schedule

Theater
 └── Screen
      ├── Seat
      └── Schedule

Schedule
 └── ScheduleSeat

Reservation
 └── ReservationSeat

Post
 └── Comment
```

주요 테이블:

- users
- movies
- theaters
- screens
- seats
- schedules
- schedule_seats
- reservations
- reservation_seats
- reviews
- posts
- comments

---

## 11. Backend API

### Auth

| Method | API | 기능 |
|---|---|---|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| GET | `/api/users/me` | 내 정보 |

### Movie

| Method | API | 기능 |
|---|---|---|
| GET | `/api/movies` | 영화 목록 / 검색 |
| GET | `/api/movies/{movieId}` | 영화 상세 |

### TMDB

| Method | API | 기능 |
|---|---|---|
| GET | `/api/tmdb/popular` | TMDB 인기 영화 |
| GET | `/api/tmdb/movies/{movieId}` | TMDB 영화 상세 |
| POST | `/api/tmdb/movies/{movieId}/save` | TMDB 영화 DB 저장 |

### Theater

| Method | API | 기능 |
|---|---|---|
| GET | `/api/theaters` | 극장 목록 |
| GET | `/api/theaters/{theaterId}` | 극장 상세 |

### Screen

| Method | API | 기능 |
|---|---|---|
| GET | `/api/theaters/{theaterId}/screens` | 극장별 상영관 |
| GET | `/api/screens/{screenId}` | 상영관 상세 |

### Seat

| Method | API | 기능 |
|---|---|---|
| GET | `/api/screens/{screenId}/seats` | 상영관 좌석 |
| GET | `/api/seats/{seatId}` | 좌석 상세 |

### Schedule

| Method | API | 기능 |
|---|---|---|
| GET | `/api/schedules` | 상영 일정 검색 |
| POST | `/api/schedules/{scheduleId}/seats/initialize` | 상영 좌석 초기화 |
| GET | `/api/schedules/{scheduleId}/seats` | 상영 좌석 조회 |

### Reservation

| Method | API | 기능 |
|---|---|---|
| POST | `/api/reservations` | 예매 |
| GET | `/api/reservations/me` | 내 예매 |
| PATCH | `/api/reservations/{reservationId}/cancel` | 예매 취소 |

### Review

| Method | API | 기능 |
|---|---|---|
| GET | `/api/movies/{movieId}/reviews` | 리뷰 조회 |
| POST | `/api/movies/{movieId}/reviews` | 리뷰 작성 |
| PATCH | `/api/movies/{movieId}/reviews/{reviewId}` | 리뷰 수정 |
| DELETE | `/api/movies/{movieId}/reviews/{reviewId}` | 리뷰 삭제 |

### Community

| Method | API | 기능 |
|---|---|---|
| GET | `/api/posts` | 게시글 목록 |
| POST | `/api/posts` | 게시글 작성 |
| GET | `/api/posts/{postId}` | 게시글 상세 |
| PATCH | `/api/posts/{postId}` | 게시글 수정 |
| DELETE | `/api/posts/{postId}` | 게시글 삭제 |
| GET | `/api/posts/{postId}/comments` | 댓글 목록 |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 |
| PATCH | `/api/posts/{postId}/comments/{commentId}` | 댓글 수정 |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | 댓글 삭제 |

---

## 12. 인증 구조

로그인 성공 시 서버에서 JWT Access Token을 발급합니다.

```text
Login
  ↓
JWT 발급
  ↓
Frontend LocalStorage
  ↓
Axios Interceptor
  ↓
Authorization: Bearer TOKEN
  ↓
JwtAuthenticationFilter
```

인증이 필요한 주요 기능:

- 내 정보
- 예매
- 내 예매
- 예매 취소
- 리뷰 작성/수정/삭제
- 게시글 작성/수정/삭제
- 댓글 작성/수정/삭제

---

## 13. 예외 처리

공통 예외 처리를 위해 `GlobalExceptionHandler`를 사용합니다.

주요 예외:

- NotFoundException
- ConflictException
- ForbiddenException
- Validation 오류

REST API에서 일관된 형태로 오류 응답을 반환하도록 구성했습니다.

---

## 14. Swagger

Backend 실행 후 다음 주소에서 API를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Swagger에서 JWT 인증이 필요한 API를 테스트할 수 있습니다.

---

## 15. 프로젝트 구조

```text
movie-platform
├── backend
│   └── src
│       ├── main
│       │   └── java
│       │       └── com.movieplatform.backend
│       │           ├── client
│       │           ├── config
│       │           ├── controller
│       │           ├── dto
│       │           ├── entity
│       │           ├── exception
│       │           ├── repository
│       │           ├── scheduler
│       │           ├── security
│       │           └── service
│       │
│       └── test
│
└── frontend
    └── src
        ├── api
        ├── components
        ├── context
        └── pages
```

---

## 16. Frontend 주요 페이지

```text
/
├── 영화 목록
│
├── /movies/:movieId
│   └── 영화 상세 / 리뷰
│
├── /movies/:movieId/schedules
│   └── 상영 일정
│
├── /schedules/:scheduleId/seats
│   └── 좌석 선택 / 예매
│
├── /my/reservations
│   └── 내 예매
│
├── /posts
│   └── 커뮤니티
│
├── /posts/new
│   └── 게시글 작성
│
├── /posts/:postId
│   └── 게시글 상세 / 댓글
│
├── /posts/:postId/edit
│   └── 게시글 수정
│
├── /login
└── /signup
```

---

## 17. 실행 방법

### 요구 사항

- Java 21
- MySQL
- Node.js
- npm

### Database

MySQL에서 데이터베이스를 생성합니다.

```sql
CREATE DATABASE movie_platform
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### Backend 환경변수

다음 환경변수가 필요합니다.

```text
DB_PASSWORD
TMDB_ACCESS_TOKEN
JWT_SECRET
```

민감한 값은 GitHub에 업로드하지 않습니다.

예:

```bash
export DB_PASSWORD='YOUR_DB_PASSWORD'
export TMDB_ACCESS_TOKEN='YOUR_TMDB_READ_ACCESS_TOKEN'
export JWT_SECRET='YOUR_JWT_SECRET'
```

JWT Secret은 로컬 개발 환경에서 다음과 같이 생성할 수 있습니다.

```bash
export JWT_SECRET="$(openssl rand -base64 32)"
```

### Backend 실행

```bash
cd backend

export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"

./gradlew bootRun
```

Backend:

```text
http://localhost:8080
```

### Frontend 실행

다른 터미널에서:

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

개발 환경에서는 Vite Proxy를 통해 `/api` 요청을 Spring Boot의 `localhost:8080`으로 전달합니다.

---

## 18. 테스트

### Backend

```bash
cd backend
./gradlew clean test
```

### Frontend Build

```bash
cd frontend
npm run build
```

---

## 19. 보안

프로젝트에서는 다음 정보를 Git 저장소에 직접 저장하지 않습니다.

```text
DB 비밀번호
TMDB Access Token
JWT Secret
JWT Access Token
```

환경변수를 통해 주입하도록 구성했습니다.

---

## 20. 현재 한계

현재 프로젝트는 포트폴리오 및 개발 학습을 목적으로 한 서비스입니다.

따라서 다음 기능은 실제 상용 서비스와 차이가 있습니다.

- 실제 영화관 실시간 상영정보 연동 없음
- 실제 CGV / 롯데시네마 / 메가박스 예매 연동 없음
- 실제 결제 시스템 없음
- 데모 극장 및 상영 일정 데이터 사용
- JWT Access Token 중심 인증
- 현재 로컬 MySQL 환경 중심

---

## 21. 향후 개선

- Backend 배포
- Frontend 배포
- 운영용 MySQL 연결
- 개발/운영 환경 설정 분리
- CORS 설정
- Frontend API URL 환경변수 분리
- Refresh Token 도입
- 예매 결제 기능
- 영화 정렬 / 필터링
- 영화 찜 기능
- 사용자 프로필
- 커뮤니티 페이지네이션
- 영화 목록 페이지네이션
- 이미지 업로드
- CI/CD
- Docker 적용

---

## 22. 핵심 구현 포인트

이 프로젝트에서 중점적으로 구현한 부분은 다음과 같습니다.

1. Spring Security와 JWT를 이용한 인증 구조
2. TMDB API를 이용한 외부 데이터 연동
3. TMDB 영화 자동 동기화 및 중복 방지
4. JPA 연관관계를 이용한 영화 예매 데이터 모델링
5. PESSIMISTIC_WRITE를 이용한 좌석 중복 예매 방지
6. 실제 상영관 좌석을 기반으로 한 ScheduleSeat 구조
7. 예매 및 취소에 따른 좌석 상태 관리
8. 리뷰 작성자 권한 처리
9. 게시글/댓글 작성자 권한 처리
10. React와 Spring Boot REST API 연동