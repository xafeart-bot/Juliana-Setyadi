# ARGON — Cross-Platform Expo Implementation & Deployment Guide

## E. Phased Implementation Plan

1. **Phase 1: Architecture & Design System Setup**
   - Editorial Warm Ivory (`#FDFBF7`), Charcoal (`#1A1715`), and Muted Bronze (`#C5A059`) tokens.
   - Core layout primitives, typography scale, navigation structure, and safe-area insets.
2. **Phase 2: Authentication & Profile Management**
   - Register, login, forgot-password, session token persistence, and profile editing.
   - Multi-role authorization: Guest, Free Member, Premium Member, Content Editor, Admin.
3. **Phase 3: Content Library & Media Engines**
   - Ebook PDF / chapter reader with font sizing and progress sync.
   - Audio / Podcast streaming player with playback speed (0.75x–2.0x), seeking, and transcripts.
   - Storytelling template generator (Hook, Context, Conflict, Turning Point, Takeaway).
4. **Phase 4: Entitlement Engine & Paywall**
   - Server-side access resolution rules (Free vs Premium subscription vs Standalone purchase).
   - Subscription checkout (Bulanan & Tahunan) and one-time ebook purchase flows.
5. **Phase 5: Consultation Booking System**
   - Service catalog, date & time slot selection, intake questionnaire, double-booking lock.
   - Google Meet link generation and booking management.
6. **Phase 6: Admin CMS & Operations Dashboard**
   - Revenue analytics (IDR gross and active MRR), content CRUD, audit logs.

---

## F. Required External Accounts & Environment Variables

### External Service Providers
1. **Google Cloud / Firebase & Play Console:**
   - Google Play Developer Console (for Android in-app billing & Google Play distribution).
   - Firebase Project (for Auth / Cloud Messaging push notifications).
2. **Apple Developer Program:**
   - Apple App Store Connect (for iOS StoreKit 2 subscriptions & distribution).
3. **Billing & Payment Gateways:**
   - **RevenueCat**: Cross-platform mobile in-app purchase SDK & receipt validation.
   - **Midtrans / Xendit**: Indonesian local payment gateway for consultation services (QRIS, BCA/Mandiri/BRI Virtual Accounts, GoPay, OVO).
4. **Cloud Infrastructure & Storage:**
   - **PostgreSQL Database**: Supabase, Neon, or AWS RDS.
   - **Private Object Storage**: AWS S3 or Cloudflare R2 with Pre-signed URLs for DRM/ebook protection.
   - **Video/Audio CDN**: Cloudflare Stream or AWS CloudFront.

### Backend Environment Variables (`.env.production`)
```bash
# Server & Port
PORT=4000
NODE_ENV=production
API_PREFIX=/api/v1
APP_TIMEZONE=Asia/Jakarta

# Database & Cache
DATABASE_URL="postgresql://argon_admin:SECRET_PASSWORD@db.argonclub.id:5432/argon_db?sslmode=require"
REDIS_URL="redis://default:REDIS_SECRET@cache.argonclub.id:6379"

# Authentication & JWT
JWT_SECRET="argon_jwt_secure_key_min_64_characters_entropy_required"
JWT_EXPIRATION="7d"

# Payment Gateways (Indonesian & Store Billing)
MIDTRANS_SERVER_KEY="Mid-server-xxxxxxxxxxxxxxxxx"
MIDTRANS_CLIENT_KEY="Mid-client-xxxxxxxxxxxxxxxxx"
MIDTRANS_IS_PRODUCTION=true
REVENUECAT_SECRET_API_KEY="appl_xxxxxxxxxxxxxxxxx"
GOOGLE_PLAY_SERVICE_ACCOUNT_JSON="{\"type\": \"service_account\", ...}"
APPLE_STOREKIT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n..."

# Private Asset Storage (Signed URLs)
STORAGE_BUCKET="argon-private-assets"
STORAGE_REGION="ap-southeast-1"
STORAGE_ACCESS_KEY="xxxxxxxxxxxx"
STORAGE_SECRET_KEY="xxxxxxxxxxxxxxxxxxxxxxxx"
```

---

## G. React Native + Expo Project Structure & Export Instructions

To run or export the mobile app into a React Native + Expo environment:
```bash
# 1. Initialize Expo project with Expo Router
npx create-expo-app@latest argon-mobile --template blank-typescript
cd argon-mobile

# 2. Install dependencies
npx expo install expo-router react-native-safe-area-context react-native-screens expo-linking expo-constants expo-status-bar
npx expo install expo-av @react-native-async-storage/async-storage lucide-react-native
npm install zustand @tanstack/react-query axios

# 3. Copy shared types, design tokens, and screens from docs/export_crossplatform
# 4. Start local development build:
npx expo start --dev-client
```
