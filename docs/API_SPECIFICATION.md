# ARGON — REST API Contract (/api/v1)

## Overview & Standard Conventions
- Base URL: `https://api.argonclub.id/api/v1`
- Auth Header: `Authorization: Bearer <jwt_or_session_token>`
- Response Structure:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operasi berhasil",
  "meta": { "page": 1, "total": 42 }
}
```
- Error Structure (RFC 7807):
```json
{
  "success": false,
  "error": {
    "code": "ENTITLEMENT_REQUIRED",
    "message": "Konten ini membutuhkan keanggotaan Premium atau pembelian tersendiri.",
    "statusCode": 403
  }
}
```

---

## 1. Authentication
- `POST /auth/register`
  - Body: `{ email: string, password: string, displayName: string, acceptedTerms: boolean }`
  - Returns: `{ user: User, token: string }`
- `POST /auth/login`
  - Body: `{ email: string, password: string }`
  - Returns: `{ user: User, token: string, roles: string[] }`
- `POST /auth/logout`
  - Revokes active refresh token / session.
- `POST /auth/password-reset`
  - Body: `{ email: string }`
- `POST /auth/verify-email`
  - Body: `{ token: string }`

---

## 2. Member Profile & Library
- `GET /me` -> Current user profile, active subscription status, assigned roles.
- `PATCH /me` -> Update `displayName`, `biography`, `interests`, `learningGoals`, `notificationPreferences`.
- `DELETE /me` -> Soft-deletes account, revokes sessions, triggers GDPR/Indonesian PDP data export or deletion.
- `GET /me/membership` -> Active plan, billing interval, expiration date, cancel-at-period-end status.
- `GET /me/library` -> Returns items split into:
  - `ownedProducts`: Standalone purchased ebooks and courses (lifetime access).
  - `subscriptionEntitlements`: Premium library items accessible via active plan.
  - `bookmarks`: Saved articles, stories, podcasts.
  - `learningProgress`: Array of `{ contentId, progressPercent, lastPositionSeconds, completedAt }`.
- `GET /me/orders` -> List user invoices and receipt summaries.
- `GET /me/bookings` -> Upcoming and completed 1-on-1 consultations with Google Meet URLs.
- `GET /me/notifications` -> Member alerts, booking reminders, editorial announcements.

---

## 3. Content
- `GET /content`
  - Query: `category`, `type` (`ebook`, `podcast`, `audio_lesson`, `article`, `narrative_template`, `founder_story`), `accessType` (`free`, `premium`), `search`, `page`, `limit`
  - Returns: Paginated content cards with preview metadata and access labels.
- `GET /content/:slug`
  - Returns full content if free or user is entitled; if locked, returns preview teaser, outline, and paywall trigger.
- `GET /categories` -> Full taxonomy of editorial categories.
- `POST /content/:id/bookmark` / `DELETE /content/:id/bookmark` -> Toggle bookmark.
- `GET /content/:id/access` -> Returns `{ entitled: boolean, reason: 'FREE' | 'SUBSCRIPTION' | 'PURCHASE' | 'LOCKED' }`.
- `POST /content/:id/progress`
  - Body: `{ progressPercent: number, lastPositionSeconds: number, completed: boolean }`

---

## 4. Commerce & Billing
- `GET /plans` -> Returns available plans (e.g. Bulanan Rp 149.000, Tahunan Rp 1.490.000) with localized IDR benefits.
- `GET /products` -> Standalone digital items (e.g. Masterclass Ebook Bundle).
- `POST /orders` -> Create checkout order.
  - Body: `{ items: [{ productId, quantity }], couponCode?: string, provider: 'store_kit' | 'google_play' | 'midtrans' }`
- `POST /billing/checkout` -> Generate payment session or store order snapshot.
- `POST /billing/webhooks/:provider` -> Idempotent server webhook with signature validation for StoreKit / Google Play RTDN / Midtrans Snap.
- `POST /billing/restore-purchases` -> Validate device receipt against server database.

---

## 5. Consultation Booking System
- `GET /consultation-services` -> Active mentor services with duration, pricing, and member discount.
- `GET /availability` -> Query parameter `serviceId`, `month` (Asia/Jakarta timezone). Returns slots `{ id, startAt, endAt, status: 'available' }`.
- `POST /bookings` -> Reserve slot with server transaction lock.
  - Body: `{ serviceId, slotId, intakeAnswers: { goal: string, context: string, questions: string[] } }`
- `GET /bookings/:id` -> Booking details, status, Google Meet link.
- `POST /bookings/:id/cancel` -> Cancellation with policy validation.
- `POST /bookings/:id/reschedule` -> Move to newly selected availability slot.

---

## 6. Admin & Editor Endpoints (Protected by RBAC)
- `GET /admin/overview` -> Total members, active subscribers, gross IDR revenue, booking count.
- `GET /admin/users` -> Member search, role management, account suspension.
- `CRUD /admin/content` -> Create, draft, publish, schedule, or archive content and upload assets.
- `CRUD /admin/consultation-services` & `GET /admin/bookings` -> Calendar availability & private mentor notes.
- `GET /admin/audit-logs` -> Tamper-resistant log of admin actions.
