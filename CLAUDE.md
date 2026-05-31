# Procurement System — Project Context

## Role Setup
- **AI role:** Senior Fullstack Developer / Tech Lead / Mentor
- **User role:** Mid-level developer (4 years experience, strong FE Angular, leveling up BE Spring Boot)
- **Goal:** Learn Spring Boot best practices through real project. Target: mid-level fullstack confidence for interviews.
- **Language:** Vietnamese preferred for discussion, English for code.

---

## Project Overview

Mini online public procurement system (đấu thầu qua mạng), inspired by muasamcong.mpi.gov.vn.

### Actors
| Role | Mô tả |
|---|---|
| `ADMIN` | Quản trị hệ thống, tạo tài khoản Investor và Contractor |
| `INVESTOR` | Chủ đầu tư — tạo Plan, Tender, lập HSMT, đánh giá, công bố kết quả |
| `CONTRACTOR` | Nhà thầu — tìm kiếm Tender, nộp HSDT, xem kết quả |

### Account Creation Rules
- Admin tạo tất cả tài khoản trực tiếp (không có self-registration, không có approval flow)
- Single `users` table với nullable fields theo role

---

## Tech Stack

**Backend (main focus)**
- Java 17, Spring Boot 4.x
- Spring Security 6 + JWT (Sprint 4)
- Spring Data JPA + PostgreSQL
- Flyway migration
- Lombok, Maven

**Frontend** (later)
- Angular 19 — standalone components, signals

**Infrastructure**
- PostgreSQL via Docker (`docker-compose.yml`)
- Adminer tại `localhost:8081` để inspect DB

---

## Domain Model & State Machines

### Entity Hierarchy
```
User (ADMIN / INVESTOR / CONTRACTOR)
│
├── Plan [KHLCNT] — Investor tạo
│     └── Tender [Gói thầu] — nhiều tender trong 1 plan
│           ├── BiddingDoc [HSMT] — có version, có thể nâng version
│           │     └── BiddingDocFile
│           ├── BidSubmission [HSDT] — Contractor nộp
│           │     └── BidSubmissionFile
│           ├── BidEvaluation — Investor chấm điểm từng HSDT
│           └── TenderResult — kết quả cuối (1-1 với Tender)
```

### State Machines

**Plan:**
```
DRAFT → IN_PROGRESS → COMPLETED | CANCELLED
```

**Tender:**
```
DRAFT → PUBLISHED → HSMT_ISSUED → BIDDING → BID_CLOSED → EVALUATING → AWARDED | CANCELLED
```

**BiddingDoc (HSMT):**
```
DRAFT → ACTIVE → SUPERSEDED
```
> Khi Investor publish version mới → tất cả BidSubmission đang SUBMITTED tự động WITHDRAWN

**BidSubmission (HSDT):**
```
DRAFT → SUBMITTED ↔ WITHDRAWN
```
> Chỉ được rút/nộp lại khi Tender còn ở BIDDING. Chỉ 1 SUBMITTED per contractor per tender tại một thời điểm. WITHDRAWN records giữ lại cho audit trail.

### Enums
```java
UserRole:        ADMIN, INVESTOR, CONTRACTOR
UserStatus:      ACTIVE, INACTIVE
PlanStatus:      DRAFT, IN_PROGRESS, COMPLETED, CANCELLED
TenderStatus:    DRAFT, PUBLISHED, HSMT_ISSUED, BIDDING, BID_CLOSED, EVALUATING, AWARDED, CANCELLED
TenderType:      GOODS, CONSTRUCTION, NON_CONSULTING, CONSULTING
TenderMethod:    OPEN_BIDDING, COMPETITIVE_QUOTE, DIRECT
BiddingDocStatus: DRAFT, ACTIVE, SUPERSEDED
BidStatus:       DRAFT, SUBMITTED, WITHDRAWN
EvaluationResult: PASS, FAIL
```

### Bid Opening Rules (who sees what)
| Thời điểm | Public | Investor | Contractor (của mình) |
|---|---|---|---|
| BIDDING | Thấy Tender, tải HSMT | Thấy số lượng đã nộp | Thấy HSDT của mình |
| BID_CLOSED | — | Thấy toàn bộ HSDT + giá | Thấy HSDT của mình |
| EVALUATING | — | Chấm điểm, ghi chú | Không thấy thêm |
| AWARDED | Thấy kết quả + nhà thầu trúng | Full | Thấy thắng/thua |

---

## Package Structure (Feature-based)

```
com.procurement.system/
├── common/
│   ├── ApiResponse.java          ← unified response wrapper
│   ├── config/
│   │   ├── SecurityConfig.java   ← tạm disable, Sprint 4 sẽ implement JWT
│   │   └── PasswordConfig.java   ← BCryptPasswordEncoder bean
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── ResourceNotFoundException.java  ← 404
│       └── BusinessException.java          ← 400
├── user/
│   ├── dto/ (CreateUserRequest, UpdateUserRequest, UserResponse)
│   └── enums/ (UserRole, UserStatus)
├── plan/
│   └── enums/ (PlanStatus)
├── tender/
│   └── enums/ (TenderStatus, TenderType, TenderMethod)
├── biddingdoc/
│   └── enums/ (BiddingDocStatus)
├── bidsubmission/
│   └── enums/ (BidStatus)
├── evaluation/
│   └── enums/ (EvaluationResult)
└── result/
```

---

## API Conventions

### Response Format (luôn dùng ApiResponse<T>)
```json
// Success
{ "status": 200, "message": "Success", "data": { ... } }

// Created
{ "status": 201, "message": "Created", "data": { ... } }

// Error
{ "status": 404, "message": "User not found", "data": null }
```

### HTTP Status Codes
- `POST` tạo resource → `201 Created`
- `GET`, `PUT`, `PATCH` → `200 OK`
- `DELETE` hoặc no-body operations → `204 No Content`

### Exception Handling
- `ResourceNotFoundException` → HTTP 404
- `BusinessException` → HTTP 400
- `MethodArgumentNotValidException` → HTTP 400 (validation)

### Password
- Hash bằng **BCrypt** trước khi lưu DB
- **JWT** dùng cho authentication token (khác BCrypt — BCrypt là hash, JWT là token)

---

## Database

### Flyway Migrations (V1–V7)
```
V1: users
V2: plans
V3: tenders
V4: bidding_docs, bidding_doc_files
V5: bid_submissions, bid_submission_files
V6: bid_evaluations
V7: tender_results
```

### Key Design Decisions
- UUID primary keys (`gen_random_uuid()`)
- `ddl-auto: validate` — Hibernate chỉ validate, Flyway quản lý schema
- Single `users` table (nullable fields theo role, validated ở service layer)
- `proposed_price NOT NULL` trong `bid_submissions`
- `tender_id UNIQUE` trong `tender_results` (1-1 relationship)

---

## Sprint Status

### ✅ Sprint 1 — Project Setup & Database
- Spring Boot project, Docker PostgreSQL, Adminer
- Flyway migrations V1–V7
- JPA Entities + Enums (tất cả đầy đủ)
- Feature-based package structure

### ✅ Sprint 2 — User Management API
- 5 endpoints: POST, GET list, GET by id, PUT, PATCH status
- BCrypt password hashing
- `ApiResponse<T>` unified wrapper
- `GlobalExceptionHandler` với 3 exception types
- DTOs: CreateUserRequest, UpdateUserRequest, UserResponse

### 🔲 Sprint 3 — Plan & Tender API (NEXT)
**Pending decision:** JWT authentication trước hay Plan/Tender API trước?
- JWT trước: `POST /api/plans` tự lấy investor từ token — realistic hơn
- Plan/Tender trước: thấy kết quả nhanh hơn, JWT làm sau

---

## Coding Conventions
- Không viết comment (trừ khi logic thực sự non-obvious)
- Dùng `FetchType.LAZY` cho tất cả `@ManyToOne` / `@OneToOne`
- Dùng `BigDecimal` cho tất cả giá trị tiền tệ (không dùng Double/Float)
- `toResponse()` method trong Service để map Entity → DTO
- Không expose Entity trực tiếp từ Controller
- Custom exception thay vì `RuntimeException` thuần
