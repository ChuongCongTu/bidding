# CLAUDE.md — Session Context (Procurement System)

> Đọc file này trước khi làm bất cứ việc gì. Đây là toàn bộ context của session làm việc giữa Claude và user.

---

## 1. Collaboration Protocol

### Vai trò của Claude
Senior Fullstack Developer (Angular + Java Spring Boot) 8+ năm kinh nghiệm — đóng vai Tech Lead, mentor trực tiếp, và đồng nghiệp cùng team. **Không phá vỡ role này.**

### Cách làm việc
- **[BẮT BUỘC] Cuối mỗi sprint: tự động cập nhật Section 9 của file này** — không cần user nhắc:
  1. Tick `[x]` sprint vừa hoàn thành, ghi tóm tắt những gì đã build
  2. Cập nhật `🔲 Đang làm` sang sprint tiếp theo
  3. Cập nhật `Quyết định kỹ thuật` nếu có decision mới
  4. Cập nhật `Next` list
  5. Commit: `git add CLAUDE.md && git commit -m "Update CLAUDE.md: Sprint X complete"`
- Giao task theo sprint thực tế, không dạy lý thuyết chay
- Giải thích **tại sao** làm vậy, không chỉ làm gì
- **Đọc file thực tế** để review — không hỏi user paste code
- Format review: **PASS / KHÔNG ĐẠT** — liệt kê issue theo mức độ: Bug (fix ngay) / Cần cải thiện / Minor
- Giao từng task nhỏ, review xong mới giao tiếp — không dump hết một lúc
- Hỏi câu hỏi domain/design trước khi code — kiểm tra hiểu biết của user
- Khen đúng chỗ khi user tự nghĩ ra quyết định tốt
- Phản biện thẳng thắn nếu user đi sai hướng; acknowledge nếu user đúng

### Profile người dùng
- **4 năm kinh nghiệm** đi làm thực tế
- **FE mạnh**: Angular đã làm hết (component, routing, service, RxJS, state management) — đang dùng Angular 9, muốn upgrade lên v19
- **BE còn yếu**: CRUD + JPA cơ bản, hay fix bug dự án cũ, chưa làm Security/JWT đúng cách
- Học để **fill gaps BE**, không phải học từ đầu
- Có IntelliJ, VS Code, Java 17, Node.js, Docker
- Dùng **PostgreSQL qua Docker** (không cài local)
- Hay tự thêm tool hay vào setup (ví dụ: tự thêm Adminer vào docker-compose)
- Sẽ push back nếu không đồng ý — hãy defend hoặc acknowledge

### Language
- Trao đổi: **tiếng Việt**
- Code, tên biến, comment: **tiếng Anh**

---

## 2. Project Overview

**Mini online procurement system** — đấu thầu qua mạng, inspired by muasamcong.mpi.gov.vn

### Actors & Account Rules
| Role | Mô tả |
|---|---|
| `ADMIN` | Quản trị, tạo tài khoản Investor và Contractor |
| `INVESTOR` | Chủ đầu tư — tạo Plan/Tender, lập HSMT, đánh giá, công bố kết quả |
| `CONTRACTOR` | Nhà thầu — tìm Tender, nộp HSDT, xem kết quả |

- **Admin tạo tất cả tài khoản** — không có self-registration, không có approval flow
- Single `users` table với nullable fields theo role
- Default admin tạo tự động lúc startup qua `DataInitializer` (`admin@procurement.com` / `Admin@123`)

---

## 3. Domain Model

### Entity Hierarchy
```
User (ADMIN / INVESTOR / CONTRACTOR)
│
├── Plan [KHLCNT] — Investor tạo
│     └── Tender [Gói thầu] — nhiều tender trong 1 plan
│           ├── BiddingDoc [HSMT] — versioned document
│           │     └── BiddingDocFile
│           ├── BidSubmission [HSDT] — Contractor nộp
│           │     └── BidSubmissionFile
│           ├── BidEvaluation — Investor chấm từng HSDT
│           └── TenderResult — kết quả cuối (1-1 với Tender)
```

### State Machines

**Plan:** `DRAFT → IN_PROGRESS → COMPLETED | CANCELLED`

**Tender:** `DRAFT → PUBLISHED → HSMT_ISSUED → BIDDING → BID_CLOSED → EVALUATING → AWARDED | CANCELLED`

**BiddingDoc:** `DRAFT → ACTIVE → SUPERSEDED`
> Publish version mới → tất cả HSDT đang SUBMITTED tự động WITHDRAWN

**BidSubmission:** `DRAFT → SUBMITTED ↔ WITHDRAWN`
> Phải rút (WITHDRAWN) rồi mới nộp lại. Chỉ 1 SUBMITTED per contractor per tender. WITHDRAWN records giữ lại cho audit.

### Bid Visibility Rules
| Tender State | Public | Investor | Contractor (của mình) |
|---|---|---|---|
| BIDDING | Thấy tender, tải HSMT | Thấy số lượng đã nộp | Thấy HSDT của mình |
| BID_CLOSED | — | Thấy toàn bộ HSDT + giá | Thấy HSDT của mình |
| EVALUATING | — | Chấm điểm, ghi chú | Không thấy thêm |
| AWARDED | Thấy kết quả, nhà thầu trúng | Full access | Thắng/thua |

### Enums
```java
UserRole:         ADMIN, INVESTOR, CONTRACTOR
UserStatus:       ACTIVE, INACTIVE
PlanStatus:       DRAFT, IN_PROGRESS, COMPLETED, CANCELLED
TenderStatus:     DRAFT, PUBLISHED, HSMT_ISSUED, BIDDING, BID_CLOSED, EVALUATING, AWARDED, CANCELLED
TenderType:       GOODS, CONSTRUCTION, NON_CONSULTING, CONSULTING
TenderMethod:     OPEN_BIDDING, COMPETITIVE_QUOTE, DIRECT
BiddingDocStatus: DRAFT, ACTIVE, SUPERSEDED
BidStatus:        DRAFT, SUBMITTED, WITHDRAWN
EvaluationResult: PASS, FAIL
```

---

## 4. Tech Stack

- Java 17, Spring Boot 4.x, Maven
- Spring Security 6 + JWT (jjwt 0.12.6)
- Spring Data JPA + PostgreSQL (Docker)
- Flyway migration, Lombok
- Angular 19 (chưa làm)
- Adminer tại `localhost:8081`

---

## 5. Package Structure

```
com.procurement.system/
├── auth/                        ← POST /api/auth/login
├── common/
│   ├── ApiResponse.java
│   ├── config/ (SecurityConfig, PasswordConfig, DataInitializer)
│   ├── exception/ (GlobalExceptionHandler, ResourceNotFoundException, BusinessException)
│   └── security/ (JwtUtil, JwtAuthenticationFilter, UserDetailsServiceImpl)
├── user/                        ← /api/admin/users  ✅ DONE
├── plan/                        ← /api/investor/plans  ✅ DONE
├── tender/                      ← /api/investor/tenders, /api/tenders/public
├── biddingdoc/                  ← HSMT
├── bidsubmission/               ← HSDT
├── evaluation/                  ← chấm điểm
└── result/                      ← kết quả cuối
```

---

## 6. API Conventions

```
/api/auth/**          → public
/api/admin/**         → ADMIN only
/api/investor/**      → INVESTOR only
/api/contractor/**    → CONTRACTOR only
/api/tenders/public   → public
```

Response format — `ApiResponse<T>`:
```json
{ "status": 200, "message": "Success", "data": { ... } }
{ "status": 404, "message": "User not found", "data": null }
```

POST → 201 | GET/PUT/PATCH → 200 | No-body → 204

---

## 7. Quyết định kỹ thuật quan trọng

| Quyết định | Lý do |
|---|---|
| Single `users` table | Đơn giản hơn tách profile tables; validate ở service layer |
| Feature-based packages | Code liên quan gần nhau; gần microservices structure |
| `Tender` (không phải `BiddingDetail`) | Standard term; không conflict với BidSubmission, BidEvaluation |
| Tách `bidsubmission/`, `evaluation/`, `result/` | User đề xuất đúng — 3 concerns khác nhau |
| `FetchType.LAZY` mọi relationship | Tránh N+1 query |
| `BigDecimal` cho tiền | Tránh floating-point precision loss |
| State machine validation | Check cả source lẫn target; dùng `switch` expression |
| `@FutureOrPresent` ở DTO + cross-field ở Service | Annotation cho format; business rule cho logic |
| BCrypt hash password, JWT là token | Hai thứ khác nhau — user đã từng nhầm |
| JWT claims: userId + role (không có email) | Tránh PII trong token |
| Error message generic: "Invalid credentials" | Tránh user enumeration attack |
| `DataInitializer` với `existsByRole` guard | Idempotent — chạy nhiều lần không duplicate |
| Check ownership → `ResourceNotFoundException` | Không tiết lộ resource có tồn tại không |
| Plan `code` auto-gen + retry-on-unique | DB UNIQUE làm trọng tài; retry lo cho request thua đua |
| `createPlan` KHÔNG `@Transactional` | Để mỗi `save()` tự là 1 transaction; tránh rollback-only khi retry |
| `updatePlan` chỉ cho sửa khi DRAFT | Khi plan đã IN_PROGRESS có tender đang chạy — lock để tránh inconsistency |
| Read methods dùng `@Transactional(readOnly=true)` | Giữ session mở để lazy-load `investor` trong `toResponse()`; tắt dirty checking |

---

## 8. Database

Flyway V1–V7: `users → plans → tenders → bidding_docs + files → bid_submissions + files → bid_evaluations → tender_results`

- UUID PKs (`gen_random_uuid()`)
- `ddl-auto: validate`
- `proposed_price NOT NULL` trong `bid_submissions`
- `tender_id UNIQUE` trong `tender_results`

---

## 9. Sprint Status

### ✅ Đã hoàn thành
- [x] **Sprint 1** — Setup, Docker, Flyway V1–V7, Entities + Enums, package structure
- [x] **Sprint 2** — User API (5 endpoints), BCrypt, ApiResponse, GlobalExceptionHandler
- [x] **Sprint 3** — JWT Auth, DataInitializer, `POST /api/auth/login`

### ✅ Sprint 4 — Plan & Tender API (DONE)

**Domain decisions (đã chốt):**
- `code` (KHLCNT) **auto-gen** ở Service — không cho client nhập. Format `KH-{fiscalYear}-{0001}`. Chiến lược **retry-on-unique-violation** (mượn UNIQUE constraint làm trọng tài). → `CreatePlanRequest` cần bỏ field `code` khi code Service.
- INVESTOR chỉ thấy plan **của mình** — chặn ngay ở query (`findByIdAndInvestor`), không lọc sau.
- Create: client KHÔNG gửi `status`; BE set cứng `DRAFT`.

**Tiến độ task:**
- [x] **4.1 `PlanRepository`** — `findAllByInvestor`, `findByIdAndInvestor`, `countByFiscalYear`
- [x] **4.2 `PlanService`** — `getCurrentInvestorId()` + `validateTransition()` + 5 method (create/getMy list/getMy by id/update/changeStatus). `createPlan` KHÔNG `@Transactional`; read methods dùng `@Transactional(readOnly=true)`
- [x] **4.3 `PlanController`** — 5 endpoints tại `/api/investor/plans`; `@RequestBody @Valid` đầy đủ
- [x] **4.4 Tender API** ✅ DONE
  - [x] DTOs: `CreateTenderRequest`, `UpdateTenderRequest`, `ChangeTenderStatusRequest`, `TenderResponse`
  - [x] `TenderRepository` — `findAllByPlan`, `findByIdAndInvestorId` (@Query JPQL), `findAllByStatusIn`, `countByFiscalYear`
  - [x] `TenderService` — đầy đủ 8 methods
  - [x] `TenderController` — 6 investor endpoints tại `/api/investor/tenders`
  - [x] `TenderPublicController` — 2 public endpoints tại `/api/tenders/public`

**Domain decisions Tender (đã chốt):**
- `TenderStatus`: `NEW → INIT_MT → PUB_MT → OPEN_BID → PUB_KQLCNT | CANCEL_BID`. NEW xóa thẳng (DELETE), không cancel. PUB_KQLCNT và CANCEL_BID là terminal.
- Tender code auto-gen: `GT-{fiscalYear}-{0001}`, fiscalYear lấy từ plan (fallback `LocalDate.now().getYear()`)
- Ownership check qua plan: `planRepository.findByIdAndInvestor(planId, investor)`
- `updateTender` và `deleteTender` chỉ cho phép khi status = `NEW`
- `TenderRepository.findByIdAndInvestorId` dùng `@Query` JPQL thay vì Spring Data derived naming

SecurityConfig route rules: `/api/tenders/public/**` (public), `/api/investor/**` (INVESTOR), `/api/contractor/**` (CONTRACTOR).

### Next
1. BiddingDoc API — upload HSMT, versioning, auto-withdraw BidSubmission khi publish version mới
2. BidSubmission API — nộp/rút/re-submit HSDT
3. Evaluation + Result API
4. Angular 19 frontend

---

## 10. Coding Conventions
- Không comment (trừ khi logic non-obvious)
- `FetchType.LAZY` cho mọi relationship
- `BigDecimal` cho tiền
- `toResponse()` private trong Service
- Không expose Entity từ Controller
- Custom exception thay vì `RuntimeException`
- `@Valid` trên `@RequestBody`
