# ARGON — System Architecture & Implementation Specification

## A. System Architecture Diagram

```
+----------------------------------------------------------------------------------+
|                                CLIENT APPLICATIONS                               |
|                                                                                  |
|   +------------------------------------+   +---------------------------------+   |
|   |   Android App (Native Compose)     |   |   React Native / Expo Client    |   |
|   |   Kotlin • Material 3 • MVVM       |   |   TypeScript • Expo Router      |   |
|   |   Clean Architecture • StateFlow   |   |   Cross-Platform (iOS & Android)|   |
|   +-----------------+------------------+   +----------------+----------------+   |
|                     |                                       |                    |
|                     +-------------------+-------------------+                    |
|                                         |                                        |
|   +-------------------------------------+------------------------------------+   |
|   |        Web Admin & Editor Dashboard (React + TypeScript + Tailwind)      |   |
+---|-------------------------------------+------------------------------------|---+
                                          | HTTPS / TLS 1.3
                                          v
+----------------------------------------------------------------------------------+
|                               API GATEWAY & BACKEND                              |
|   NestJS / Node.js TypeScript API (Versioned /api/v1)                             |
|                                                                                  |
|   [Security & Middleware]                                                        |
|   • Cloudflare WAF / Rate Limiter (IP & User-based)                              |
|   • Auth Guard: JWT / Managed Session (Supabase / Firebase / Auth0)              |
|   • RBAC Guard: Guest, Free Member, Premium Member, Content Editor, Admin        |
|   • Idempotency Interceptor & Audit Logger                                       |
|                                                                                  |
|   [Core Modules]                                                                 |
|   • AuthModule         • ContentModule          • CommerceModule                 |
|   • EntitlementModule  • ConsultationModule     • NotificationModule             |
|   • StorageModule      • WebhookModule          • AdminModule                    |
+-----------------------------------------+----------------------------------------+
                                          |
                +-------------------------+------------------------+
                |                                                  |
                v                                                  v
+-------------------------------+              +-----------------------------------+
|     DATABASE & CACHE          |              |       EXTERNAL INTEGRATIONS       |
|                               |              |                                   |
|   • PostgreSQL 16 (Primary)   |              |   • Storage: AWS S3 / Cloudflare  |
|     - Normalized Tables       |              |     R2 (Private with Signed URLs) |
|     - UUID Primary Keys       |              |   • Store Billing: Google Play    |
|     - Strict Foreign Keys     |              |     Billing & Apple StoreKit 2    |
|     - Row Level Security      |              |   • Subscriptions SDK: RevenueCat |
|     - UTC Timestamps          |              |   • Local ID Payments: Midtrans / |
|                               |              |     Xendit (Snap & QRIS/VA)       |
|   • Redis 7 (Cache & Queues)  |              |   • Video/Audio: Mux / Cloudflare |
|     - Rate limit buckets      |              |   • Meetings: Google Calendar/Meet|
|     - Booking hold locks      |              |   • Push Notifications: Expo Push |
|     - Entitlement cache       |              |     Service & Firebase Cloud Msg  |
+-------------------------------+              +-----------------------------------+
```

---

## B. Repository and Folder Structure

```
argon-platform/
├── backend/                         # NestJS API Backend
│   ├── prisma/
│   │   ├── schema.prisma            # Normalized Prisma Schema
│   │   ├── migrations/              # Database migration history
│   │   └── seed.ts                  # Comprehensive Indonesian seed data
│   ├── src/
│   │   ├── common/                  # Guards, decorators, filters, interceptors
│   │   │   ├── guards/              # JwtAuthGuard, RolesGuard, EntitlementGuard
│   │   │   └── interceptors/        # AuditLogInterceptor, IdempotencyInterceptor
│   │   ├── config/                  # Validated environment configuration
│   │   ├── modules/
│   │   │   ├── auth/                # Auth, registration, token verification
│   │   │   ├── users/               # Profiles, account deletion, preferences
│   │   │   ├── content/             # Ebooks, podcasts, audio, articles, templates
│   │   │   ├── commerce/            # Orders, plans, store billing, Midtrans/Xendit
│   │   │   ├── entitlements/        # Server-side access resolution engine
│   │   │   ├── consultations/       # Mentor catalog, slots, double-booking lock
│   │   │   ├── notifications/       # Push, email, in-app alerts
│   │   │   └── admin/               # KPIs, user management, audit logs, CMS CRUD
│   │   └── main.ts
│   ├── package.json
│   └── tsconfig.json
│
├── mobile/                          # React Native + Expo Cross-Platform App
│   ├── app/                         # Expo Router File-based Navigation
│   │   ├── (auth)/                  # Onboarding, login, register, forgot-password
│   │   ├── (tabs)/                  # Bottom navigation
│   │   │   ├── index.tsx            # Home Dashboard (Stories, Continue learning)
│   │   │   ├── explore.tsx          # Search, Category & Format Filters
│   │   │   ├── library.tsx          # Purchased Ebooks, Saved, Subscriptions
│   │   │   └── profile.tsx          # Account, Membership, Orders, Settings
│   │   ├── content/[slug].tsx       # Reader, Audio Player, Narrative Template
│   │   ├── booking/                 # Consultation service selection, slot & intake
│   │   ├── billing/paywall.tsx      # Membership Upgrade & Subscription screen
│   │   └── _layout.tsx
│   ├── components/                  # Reusable editorial UI design system
│   │   ├── AudioPlayer.tsx          # Seek, speed controls, transcript toggle
│   │   ├── EbookReader.tsx          # Chapter viewer, font size, progress
│   │   ├── StorytellingCard.tsx     # Hook, Context, Conflict, Turning Point
│   │   └── ConsultationSlot.tsx
│   ├── constants/theme.ts           # Warm Ivory, Charcoal, Muted Bronze tokens
│   ├── services/                    # API client with Axios, interceptors, offline cache
│   ├── app.json
│   └── package.json
│
├── android-native/                  # Native Android Production Build (Jetpack Compose)
│   ├── app/src/main/java/com/example/argon/
│   │   ├── model/                   # Domain entities (Content, User, Order, etc.)
│   │   ├── data/                    # Repository, Entitlement Engine, Seed Data
│   │   ├── ui/
│   │   │   ├── theme/               # Editorial Theme (Warm Ivory, Charcoal, Bronze)
│   │   │   ├── components/          # Cards, Player, Reader, Bottom Nav
│   │   │   ├── screens/             # Home, Explore, Detail, Library, Profile, Admin
│   │   │   └── ArgomApp.kt          # Main Navigation & State Orchestrator
│   │   └── MainActivity.kt
│   └── build.gradle.kts
│
└── admin-web/                       # React + TypeScript Admin Dashboard
    ├── src/
    │   ├── pages/                   # Overview, Members, ContentCMS, Orders, Bookings
    │   ├── components/              # MetricCards, AuditTable, EditorModal
    │   └── api/
    └── package.json
```
