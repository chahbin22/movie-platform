# Movie Platform

영화 정보 탐색, 상영 정보 확인, 좌석 조회 및 예매, 리뷰, 커뮤니티 기능을 하나의 서비스로 제공하는 영화 플랫폼 프로젝트입니다.

## 1. 프로젝트 소개

영화를 보는 것뿐만 아니라 다른 사람들과 감상과 의견을 나누는 과정에서도 새로운 관점을 얻을 수 있다는 점에서 영화 커뮤니티 서비스를 만들고자 했습니다.

또한 CGV, 롯데시네마 등 영화관별로 예매 사이트가 나뉘어 있어 상영 정보를 한눈에 비교하기 어렵다는 점에서, 여러 영화관의 정보를 비교하고 보다 편리하게 예매할 수 있는 서비스를 구현하는 것을 목표로 했습니다.

현재는 **백엔드 1차 기능 구현을 완료한 상태**이며, 이후 예외 처리 고도화, Swagger 문서화, 테스트 보강 및 React 프론트엔드 구현을 진행할 예정입니다.

---

## 2. 주요 기능

### 회원 / 인증

- 회원가입
- 로그인
- BCrypt 비밀번호 암호화
- JWT Access Token 발급
- JWT 기반 인증
- 내 정보 조회

### 영화

- TMDB API 연동
- 인기 영화 조회
- TMDB 영화 상세 조회
- TMDB 영화 데이터 DB 저장
- 영화 목록 조회
- 영화 상세 조회
- 영화 제목 검색

### 영화관 / 상영관 / 좌석

- 영화관 목록 및 상세 조회
- 상영관 조회
- 상영관별 좌석 조회
- 좌석 타입 관리

### 상영 일정

- 영화 / 영화관 / 날짜별 상영 일정 조회
- 상영 일정별 좌석 상태 관리
- 상영 일정 좌석 초기화
- 좌석별 가격 및 예약 상태 관리

### 예매

- 로그인 사용자 예매 생성
- 여러 좌석 동시 선택
- 예매 금액 계산
- Reservation / ReservationSeat 저장
- 예매 완료 시 좌석 상태 `AVAILABLE -> RESERVED`
- DB 비관적 락(`PESSIMISTIC_WRITE`)을 이용한 동시 예매 방지
- 내 예매 목록 조회
- 예매 취소
- 취소 시 좌석 상태 `RESERVED -> AVAILABLE`

### 리뷰

- 영화별 리뷰 작성
- 영화별 리뷰 목록 조회
- 리뷰 수정
- 리뷰 삭제
- 평점 1~5 검증
- 한 사용자가 같은 영화에 리뷰를 중복 작성하지 못하도록 제한
- 리뷰 수정/삭제 시 작성자 확인

### 커뮤니티

- 게시글 작성
- 게시글 목록 조회
- 게시글 상세 조회
- 게시글 조회수 증가
- 게시글 수정
- 게시글 삭제
- 게시글 수정/삭제 시 작성자 확인
- 댓글 작성
- 게시글별 댓글 조회
- 댓글 수정
- 댓글 삭제
- 댓글 수정/삭제 시 작성자 확인

---

## 3. 기술 스택

### Backend

- Java 21
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- Spring Security
- JWT (`jjwt`)
- Bean Validation
- Lombok
- Gradle

### Database

- MySQL

### External API

- TMDB API

### Frontend

- React 예정

---

## 4. 시스템 구조

```text
React Frontend
      |
      | REST API
      v
Spring Boot Backend
      |
      +-------------------+
      |                   |
      v                   v
Spring Data JPA        TMDB API
      |
      v
    MySQL
```

---

## 5. 주요 데이터 구조

```text
User
 ├─ Reservation
 │    └─ ReservationSeat
 │          └─ ScheduleSeat
 │                └─ Seat
 │
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
 └─ ScheduleSeat
```

### 주요 테이블

- `users`
- `movies`
- `theaters`
- `screens`
- `seats`
- `schedules`
- `schedule_seats`
- `reservations`
- `reservation_seats`
- `reviews`
- `posts`
- `comments`

---

## 6. 주요 API

### Auth

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | X |
| POST | `/api/auth/login` | 로그인 / JWT 발급 | X |

### User

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/users/me` | 내 정보 조회 | JWT |

### TMDB / Movie

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/tmdb/popular` | TMDB 인기 영화 조회 | X |
| GET | `/api/tmdb/movies/{movieId}` | TMDB 영화 상세 조회 | X |
| POST | `/api/tmdb/movies/{movieId}/save` | TMDB 영화 DB 저장 | X |
| GET | `/api/movies` | 영화 목록 조회 | X |
| GET | `/api/movies?keyword={keyword}` | 영화 검색 | X |
| GET | `/api/movies/{movieId}` | 영화 상세 조회 | X |

### Theater / Screen / Seat

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/theaters` | 영화관 목록 조회 | X |
| GET | `/api/theaters/{theaterId}` | 영화관 상세 조회 | X |
| GET | `/api/theaters/{theaterId}/screens` | 영화관의 상영관 조회 | X |
| GET | `/api/screens/{screenId}` | 상영관 상세 조회 | X |
| GET | `/api/screens/{screenId}/seats` | 상영관 좌석 조회 | X |
| GET | `/api/seats/{seatId}` | 좌석 상세 조회 | X |

### Schedule

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/schedules?movieId={movieId}&theaterId={theaterId}&date={date}` | 상영 일정 조회 | X |
| POST | `/api/schedules/{scheduleId}/seats/initialize` | 상영 좌석 초기화 | X |
| GET | `/api/schedules/{scheduleId}/seats` | 상영별 좌석 상태 조회 | X |

### Reservation

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/reservations` | 예매 생성 | JWT |
| GET | `/api/reservations/me` | 내 예매 조회 | JWT |
| PATCH | `/api/reservations/{reservationId}/cancel` | 예매 취소 | JWT |

예매 생성 요청 예시:

```json
{
  "scheduleId": 1,
  "scheduleSeatIds": [1, 2]
}
```

### Review

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/movies/{movieId}/reviews` | 영화 리뷰 조회 | X |
| POST | `/api/movies/{movieId}/reviews` | 리뷰 작성 | JWT |
| PATCH | `/api/movies/{movieId}/reviews/{reviewId}` | 리뷰 수정 | JWT |
| DELETE | `/api/movies/{movieId}/reviews/{reviewId}` | 리뷰 삭제 | JWT |

리뷰 작성 요청 예시:

```json
{
  "rating": 5,
  "content": "재밌게 본 영화입니다."
}
```

### Community Post

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/posts` | 게시글 목록 조회 | X |
| GET | `/api/posts/{postId}` | 게시글 상세 조회 | X |
| POST | `/api/posts` | 게시글 작성 | JWT |
| PATCH | `/api/posts/{postId}` | 게시글 수정 | JWT |
| DELETE | `/api/posts/{postId}` | 게시글 삭제 | JWT |

### Comment

| Method | Endpoint | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/posts/{postId}/comments` | 게시글 댓글 조회 | X |
| POST | `/api/posts/{postId}/comments` | 댓글 작성 | JWT |
| PATCH | `/api/posts/{postId}/comments/{commentId}` | 댓글 수정 | JWT |
| DELETE | `/api/posts/{postId}/comments/{commentId}` | 댓글 삭제 | JWT |

---

## 7. 예매 처리 흐름

```text
로그인 사용자
     |
     v
상영 일정 선택
     |
     v
ScheduleSeat 선택
     |
     v
선택 좌석 PESSIMISTIC_WRITE Lock
     |
     v
AVAILABLE 상태 확인
     |
     v
Reservation 생성
     |
     v
ReservationSeat 생성
     |
     v
ScheduleSeat 상태 변경
AVAILABLE -> RESERVED
```

예매 취소 시에는 반대로 좌석 상태를 다시 `AVAILABLE`로 변경합니다.

---

## 8. JWT 인증 흐름

```text
회원 로그인
    |
    v
이메일 / 비밀번호 확인
    |
    v
JWT Access Token 발급
    |
    v
클라이언트 저장
    |
    v
Authorization: Bearer {token}
    |
    v
JwtAuthenticationFilter
    |
    v
SecurityContext에 userId 저장
    |
    v
인증이 필요한 API 접근
```

현재 Refresh Token은 구현하지 않았습니다.

---

## 9. 환경 변수

보안상 DB 비밀번호, TMDB Access Token, JWT Secret은 코드에 직접 작성하지 않습니다.

`application.properties` 예시:

```properties
spring.application.name=backend

spring.datasource.url=jdbc:mysql://localhost:3306/movie_platform?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

tmdb.access-token=${TMDB_ACCESS_TOKEN}

jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
```

실행 전 환경 변수 설정:

```bash
export DB_PASSWORD='YOUR_DB_PASSWORD'
export TMDB_ACCESS_TOKEN='YOUR_TMDB_ACCESS_TOKEN'
export JWT_SECRET='YOUR_JWT_SECRET'
```

JWT Secret 생성 예시:

```bash
openssl rand -base64 32
```

> 실제 비밀번호, TMDB Token, JWT Secret은 GitHub에 커밋하지 않습니다.

---

## 10. 실행 방법

### MySQL 실행

```bash
brew services start mysql
```

### Database 생성

```sql
CREATE DATABASE movie_platform
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### Java 21 설정

macOS에서 필요할 경우:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

### Backend 실행

```bash
cd backend
./gradlew bootRun
```

기본 실행 주소:

```text
http://localhost:8080
```

---

## 11. 프로젝트 구조

```text
movie-platform/
├── backend/
│   ├── src/main/java/com/movieplatform/backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   │   ├── comment/
│   │   │   ├── post/
│   │   │   ├── reservation/
│   │   │   ├── review/
│   │   │   └── schedule/
│   │   ├── entity/
│   │   ├── exception/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   │
│   └── src/main/resources/
│       └── application.properties
│
└── README.md
```

---

## 12. 현재 개발 상태

### Backend 1차 기능 구현 완료

- [x] Spring Boot 프로젝트 구성
- [x] MySQL 연결
- [x] TMDB API 연동
- [x] 영화 저장 / 조회 / 검색
- [x] 회원가입
- [x] BCrypt 비밀번호 암호화
- [x] 로그인
- [x] JWT 인증
- [x] 내 정보 조회
- [x] Theater
- [x] Screen
- [x] Seat
- [x] Schedule
- [x] ScheduleSeat
- [x] 예매 생성
- [x] 예매 동시성 처리
- [x] 내 예매 조회
- [x] 예매 취소
- [x] 리뷰 CRUD
- [x] 게시글 CRUD
- [x] 댓글 CRUD

### 다음 작업

- [ ] 예외 처리 및 HTTP Status 세분화
- [ ] Swagger / OpenAPI 문서화
- [ ] API 통합 테스트
- [ ] Repository / Service 테스트 보강
- [ ] React 프론트엔드 구현
- [ ] 프론트엔드와 백엔드 연동
- [ ] 배포

---

## 13. 향후 개선 예정

- HTTP 상태 코드 및 Custom Exception 구조 개선
- Swagger를 통한 API 문서화
- 영화 / 게시글 페이지네이션
- 영화 정렬 및 필터링
- 영화 평균 평점 제공
- Refresh Token 도입
- 관리자 기능
- 상영 일정 등록 관리 기능
- 결제 기능
- 테스트 코드 보강
- 배포 환경 구성

---

## 14. 현재 상태 요약

현재 백엔드는 영화 탐색부터 예매, 리뷰, 커뮤니티까지 서비스의 핵심 흐름을 수행할 수 있는 **1차 MVP 기능 구현이 완료된 상태**입니다.

```text
영화 탐색
   ↓
상영 정보 확인
   ↓
좌석 확인
   ↓
예매
   ↓
내 예매 조회 / 취소
   ↓
리뷰 작성
   ↓
커뮤니티 게시글 / 댓글
```

다음 단계에서는 기능을 추가하기보다 기존 백엔드의 예외 처리, API 문서화, 테스트를 정리한 뒤 React 프론트엔드 구현을 진행합니다.
