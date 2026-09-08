# Movie Platform

영화 정보 조회, 상영 일정 확인, 좌석 선택 및 예매, 리뷰, 커뮤니티 기능을 제공하는 웹 애플리케이션입니다.

Spring Boot 기반의 REST API 서버와 React 프론트엔드로 구성되어 있으며, JWT 인증, 좌석 중복 예매 방지, 사용자별 권한 처리, 리뷰/게시글/댓글 CRUD 등을 구현했습니다.

> 현재 영화 정보는 TMDB API를 통해 가져와 개별 저장할 수 있으며, 자동 동기화 기능은 추후 개선 예정입니다.

---

## 1. 프로젝트 개요

### 주요 목표

- 영화 정보를 조회하고 검색할 수 있는 서비스 구현
- 영화별 상영 일정과 좌석 정보 제공
- 좌석 단위 예매 및 예매 취소 기능 구현
- JWT 기반 회원 인증 및 권한 처리
- 영화 리뷰 및 커뮤니티 기능 구현
- React와 Spring Boot를 연결한 풀스택 프로젝트 경험

### 주요 사용자 흐름

```text
회원가입 / 로그인
        ↓
영화 목록 / 검색
        ↓
영화 상세
   ├─ 리뷰 작성
   └─ 예매하기
        ↓
영화관 / 날짜 / 상영시간 선택
        ↓
좌석 선택
        ↓
예매
        ↓
내 예매 조회 / 취소

커뮤니티
   ↓
게시글 작성 / 조회 / 수정 / 삭제
   ↓
댓글 작성 / 수정 / 삭제
```

---

## 2. 주요 기능

### 회원 기능

- 회원가입
- 로그인
- JWT 기반 인증
- 로그인 상태 유지
- 내 정보 조회
- 로그아웃
- 이메일 및 닉네임 중복 검증
- 비밀번호 BCrypt 암호화

### 영화

- 영화 목록 조회
- 영화 제목 검색
- 영화 상세 조회
- TMDB 인기 영화 조회
- TMDB 영화 상세 조회
- TMDB 영화 정보를 자체 DB에 저장

### 영화 리뷰

- 영화별 리뷰 조회
- 리뷰 작성
- 별점 1~5점
- 리뷰 수정
- 리뷰 삭제
- 본인 리뷰만 수정/삭제 가능

### 영화관 / 상영 일정

- 영화관 목록 조회
- 영화관 상세 조회
- 영화관별 상영관 조회
- 상영관 상세 조회
- 영화 / 영화관 / 날짜 조건으로 상영 일정 조회

### 좌석 / 예매

- 상영관별 좌석 조회
- 상영 일정별 좌석 상태 조회
- 좌석 선택
- 여러 좌석 동시 예매
- 예매 금액 계산
- 내 예매 내역 조회
- 예매 취소
- 취소 시 좌석 상태 복구
- 비관적 락(Pessimistic Lock)을 이용한 중복 좌석 예매 방지

### 커뮤니티

- 게시글 목록 조회
- 게시글 상세 조회
- 게시글 작성
- 게시글 수정
- 게시글 삭제
- 조회수 확인
- 본인 게시글만 수정/삭제 가능

### 댓글

- 게시글별 댓글 조회
- 댓글 작성
- 댓글 수정
- 댓글 삭제
- 본인 댓글만 수정/삭제 가능

---

## 3. 기술 스택

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring WebMVC
- Spring Data JPA
- Spring Security
- JWT
- Bean Validation
- Lombok
- Gradle

### Database

- MySQL 9.6
- H2 Database (Test)

### Frontend

- React
- Vite
- JavaScript
- React Router
- Axios
- CSS

### External API

- TMDB API

### Documentation / Test

- Springdoc OpenAPI
- Swagger UI
- JUnit
- Mockito

---

## 4. 시스템 구조

```text
┌──────────────────┐
│      React       │
│      + Vite      │
└────────┬─────────┘
         │ REST API
         ▼
┌──────────────────┐
│   Spring Boot    │
│                  │
│ Controller       │
│ Service          │
│ Repository       │
│ Security / JWT   │
└───────┬──────────┘
        │
        ├──────────────► TMDB API
        │
        ▼
┌──────────────────┐
│      MySQL       │
└──────────────────┘
```

프론트엔드는 `/api` 요청을 Vite Proxy를 통해 Spring Boot 서버로 전달합니다.

```text
React
http://localhost:5173

        ↓ /api

Spring Boot
http://localhost:8080
```

---

## 5. 데이터베이스 주요 엔티티

```text
User
Movie
Theater
Screen
Seat
Schedule
ScheduleSeat
Reservation
ReservationSeat
Review
Post
Comment
```

### 주요 관계

```text
User
 ├─ Reservation
 ├─ Review
 ├─ Post
 └─ Comment

Movie
 ├─ Schedule
 └─ Review

Theater
 └─ Screen
      ├─ Seat
      └─ Schedule

Schedule
 ├─ ScheduleSeat
 └─ Reservation

Reservation
 └─ ReservationSeat

Post
 └─ Comment
```

---

## 6. API

### Auth / User

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/auth/signup` | 회원가입 |
| POST | `/api/auth/login` | 로그인 |
| GET | `/api/users/me` | 내 정보 조회 |

### Movie

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/movies` | 영화 목록 / 검색 |
| GET | `/api/movies/{movieId}` | 영화 상세 |

### TMDB

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/tmdb/popular` | TMDB 인기 영화 |
| GET | `/api/tmdb/movies/{movieId}` | TMDB 영화 상세 |
| POST | `/api/tmdb/movies/{movieId}/save` | TMDB 영화 DB 저장 |

### Theater / Screen

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/theaters` | 영화관 목록 |
| GET | `/api/theaters/{id}` | 영화관 상세 |
| GET | `/api/theaters/{id}/screens` | 상영관 목록 |
| GET | `/api/screens/{id}` | 상영관 상세 |

### Seat

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/screens/{id}/seats` | 상영관 좌석 조회 |
| GET | `/api/seats/{id}` | 좌석 상세 |

### Schedule

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/schedules` | 상영 일정 검색 |
| POST | `/api/schedules/{scheduleId}/seats/initialize` | 상영 일정 좌석 초기화 |
| GET | `/api/schedules/{scheduleId}/seats` | 상영 일정별 좌석 조회 |

### Reservation

| Method | Endpoint | 설명 |
|---|---|---|
| POST | `/api/reservations` | 예매 |
| GET | `/api/reservations/me` | 내 예매 조회 |
| PATCH | `/api/reservations/{reservationId}/cancel` | 예매 취소 |

### Review

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/movies/{movieId}/reviews` | 리뷰 목록 |
| POST | `/api/movies/{movieId}/reviews` | 리뷰 작성 |
| PATCH | `/api/movies/{movieId}/reviews/{reviewId}` | 리뷰 수정 |
| DELETE | `/api/movies/{movieId}/reviews/{reviewId}` | 리뷰 삭제 |

### Post

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/posts` | 게시글 목록 |
| POST | `/api/posts` | 게시글 작성 |
| GET | `/api/posts/{postId}` | 게시글 상세 |
| PATCH | `/api/posts/{postId}` | 게시글 수정 |
| DELETE | `/api/posts/{postId}` | 게시글 삭제 |

### Comment

| Method | Endpoint | 설명 |
|---|---|---|
| GET | `/api/posts/{postId}/comments` | 댓글 목록 |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 |
| PATCH | `/api/posts/{postId}/comments/{commentId}` | 댓글 수정 |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | 댓글 삭제 |

---

## 7. 인증 및 보안

### JWT 인증

로그인 성공 시 서버에서 Access Token을 발급합니다.

```text
로그인
  ↓
JWT 발급
  ↓
Frontend localStorage 저장
  ↓
Axios Interceptor
  ↓
Authorization: Bearer {token}
  ↓
Spring Security / JWT Filter
```

인증이 필요한 API 요청에는 Axios Interceptor가 자동으로 JWT를 추가합니다.

### 보호되는 주요 기능

- 내 정보 조회
- 예매 및 예매 취소
- 리뷰 작성 / 수정 / 삭제
- 게시글 작성 / 수정 / 삭제
- 댓글 작성 / 수정 / 삭제

리뷰, 게시글, 댓글 수정 및 삭제는 작성자 본인만 가능합니다.

---

## 8. 좌석 중복 예매 방지

같은 좌석을 여러 사용자가 동시에 예매할 수 있는 문제를 방지하기 위해 예매 처리 과정에서 비관적 락을 사용합니다.

```text
예매 요청
   ↓
ScheduleSeat 조회
   ↓
PESSIMISTIC_WRITE Lock
   ↓
좌석 상태 검증
   ↓
AVAILABLE
   ↓
Reservation 생성
   ↓
ScheduleSeat → RESERVED
```

이미 예약된 좌석이면 예매 요청을 거부합니다.

예매 취소 시:

```text
Reservation
RESERVED → CANCELED

ScheduleSeat
RESERVED → AVAILABLE
```

로 변경되어 해당 좌석을 다시 선택할 수 있습니다.

---

## 9. 예외 처리

공통 예외 클래스를 사용해 서비스 계층의 오류를 일관된 형태로 처리합니다.

```text
NotFoundException
ConflictException
ForbiddenException
```

`GlobalExceptionHandler`에서 예외를 처리하며 `ErrorResponse` 형태로 클라이언트에 반환합니다.

---

## 10. Swagger

API 문서는 Springdoc OpenAPI를 사용합니다.

서버 실행 후:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Swagger에서 JWT Bearer 인증을 적용해 인증이 필요한 API도 테스트할 수 있습니다.

---

## 11. 테스트

주요 서비스 로직에 대해 테스트 코드를 작성했습니다.

```text
MovieServiceTest
ReviewServiceTest
PostServiceTest
CommentServiceTest
ReservationServiceTest
AuthServiceTest
JwtProviderTest
```

테스트 환경에서는 H2 Database를 사용합니다.

```bash
cd backend
./gradlew clean test
```

---

## 12. Frontend 주요 화면

```text
/
├─ 영화 목록
├─ 영화 검색
└─ 영화 카드

/movies/:movieId
├─ 영화 상세
└─ 리뷰

/movies/:movieId/schedules
└─ 상영 일정 선택

/schedules/:scheduleId/seats
├─ 좌석 선택
└─ 예매

/my/reservations
├─ 내 예매
└─ 예매 취소

/posts
└─ 커뮤니티 목록

/posts/new
└─ 게시글 작성

/posts/:postId
├─ 게시글 상세
└─ 댓글

/posts/:postId/edit
└─ 게시글 수정

/login
└─ 로그인

/signup
└─ 회원가입
```

---

## 13. 프로젝트 구조

```text
movie-platform/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   └── test/
│   ├── build.gradle
│   └── ...
│
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   ├── components/
│   │   ├── context/
│   │   ├── pages/
│   │   ├── App.jsx
│   │   ├── main.jsx
│   │   └── index.css
│   ├── vite.config.js
│   └── package.json
│
└── README.md
```

---

## 14. 실행 방법

### 요구 사항

- Java 21
- MySQL
- Node.js
- npm

### Database

```sql
CREATE DATABASE movie_platform
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### Backend 환경 변수

다음 환경 변수가 필요합니다.

```text
DB_PASSWORD
TMDB_ACCESS_TOKEN
JWT_SECRET
```

실제 비밀값은 저장소에 커밋하지 않습니다.

macOS / Linux 예:

```bash
export DB_PASSWORD='YOUR_DB_PASSWORD'
export TMDB_ACCESS_TOKEN='YOUR_TMDB_ACCESS_TOKEN'
export JWT_SECRET='YOUR_JWT_SECRET'
```

### Backend 실행

```bash
cd backend
./gradlew bootRun
```

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

Backend:

```text
http://localhost:8080
```

---

## 15. 현재 구현 범위

### 완료

- Spring Boot REST API
- React Frontend
- MySQL 연동
- JWT 인증
- 회원가입 / 로그인
- 영화 목록 / 검색 / 상세
- TMDB 영화 조회 및 개별 저장
- 영화관 / 상영관 / 좌석
- 상영 일정 조회
- 좌석 선택 및 예매
- 내 예매 조회 및 취소
- 리뷰 CRUD
- 게시글 CRUD
- 댓글 CRUD
- 작성자 권한 검증
- 공통 예외 처리
- Swagger
- 핵심 서비스 테스트
- 반응형 UI

### 현재 제한 사항

현재 영화관, 상영관, 상영 일정 데이터는 실제 영화관 시스템과 연동된 데이터가 아닌 프로젝트용 데이터입니다.

TMDB 영화 역시 현재는 API에서 조회한 영화를 개별적으로 자체 DB에 저장하는 방식입니다.

실제 영화관의 실시간 상영시간표 및 결제 시스템과는 연동되어 있지 않습니다.

---

## 16. 핵심 구현 포인트

### 자체 DB와 외부 영화 API 분리

TMDB 데이터를 프론트엔드에서 직접 사용하는 대신 필요한 영화를 자체 `Movie` 엔티티에 저장하여 리뷰, 상영 일정, 예매 데이터와 연결할 수 있도록 구성했습니다.

### JWT 기반 Stateless 인증

Spring Security와 JWT를 사용해 서버 세션 없이 인증하며, Axios Interceptor를 통해 인증이 필요한 요청에 토큰을 자동으로 추가합니다.

### 좌석 단위 동시성 제어

예매 과정에서 `PESSIMISTIC_WRITE`를 적용하여 동일 좌석의 중복 예매 가능성을 방지했습니다.

### 작성자 권한 검증

리뷰, 게시글, 댓글은 프론트엔드에서 본인에게만 수정/삭제 UI를 노출하고, 백엔드에서도 실제 작성자를 검증합니다.

### Backend / Frontend 분리

Spring Boot는 REST API와 비즈니스 로직을 담당하고 React는 사용자 인터페이스를 담당하도록 구성했습니다.

---

## 17. 향후 개선

- TMDB 영화 자동 동기화
- 한국 현재 상영작 및 인기 영화 자동 수집
- 영화 데이터 주기적 업데이트
- 영화관 / 상영관 데모 데이터 확장
- 상영 일정 자동 생성
- Refresh Token 적용
- Access Token 만료 처리 개선
- 관리자 기능
- 영화 검색 및 필터 고도화
- 실제 결제 기능
- 테스트 범위 확대
- 배포 및 CI/CD 구축

---

## 18. 다음 개발 예정

다음 단계에서는 TMDB 영화 데이터를 자동으로 동기화하는 기능을 추가할 예정입니다.

```text
TMDB
  ↓
Spring Boot Sync Service
  ↓
Movie DB
  ↓
React
```

현재 README에는 아직 구현하지 않은 자동 동기화 기능을 완료 기능으로 포함하지 않았습니다.
