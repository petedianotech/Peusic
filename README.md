# Peusic

**Peusic** is a premium, offline-first personal music player for Android.

It combines broad local-audio compatibility, polished playback controls and efficient offline music management under a distinctive visual identity called **Sonic Aurora**.

- No advertisements  
- No analytics or tracking  
- No user accounts or login  
- No cloud synchronisation  
- No social features  

Music is managed locally wherever possible.

## Visual identity — Sonic Aurora

Atmospheric blue → icy-blue → periwinkle → muted teal → subtle mint gradients.  
Deep navy surfaces in dark mode (never pure black). Clean icy-white / pale-blue surfaces in light mode.  
Gradients are used selectively on hero surfaces, primary actions and progress indicators.

## Architecture (Phase 1)

- Single Android application module  
- Kotlin + Jetpack Compose + Material 3  
- Clean package boundaries:  
  `ui/` (theme, components, screens, navigation)  
  `domain/` (future models)  
  `data/` (future repositories)  
  `service/` (future playback / download)  

Presentation is fully separated from future playback, download and storage logic.

## Minimum SDK decision

**minSdk = 26 (Android 8.0)**

Rationale:
- Media3 / ExoPlayer audio playback is supported from API 23; API 26 gives reliable MediaSession, notification and background behaviour on the large majority of active devices.
- Scoped storage and modern permission model are well established.
- Avoids older platform media quirks while still covering the vast majority of the intended user base.
- Target / compile SDK 35 (Android 15) for current Play requirements and modern APIs.

## Current feature status (Phase 1)

| Area | Status |
|------|--------|
| Project scaffold & Gradle | ✅ Complete |
| Sonic Aurora design system | ✅ Complete |
| Light / dark-blue themes | ✅ Complete |
| Bottom navigation (Discover, Downloads, Music, Settings) | ✅ Complete |
| Splash screen | ✅ Complete |
| Discover, Downloads, Music, Settings screens (UI) | ✅ Complete |
| Now Playing full screen + Mini-player shell | ✅ Complete (placeholders) |
| Real local library scan | ❌ Phase 3 |
| Real playback engine (Media3) | ❌ Phase 3 |
| Authorised downloader | ❌ Phase 2 |
| Settings persistence | ❌ Phase 4 |

## Build instructions

### Prerequisites
- JDK 17+
- Android SDK with platform 35 and build-tools
- Android Studio recommended

```bash
./gradlew assembleDebug
```

Debug APK will be produced at  
`app/build/outputs/apk/debug/app-debug.apk`

### GitHub Actions
A workflow builds the debug APK on every push and pull request and uploads it as an artifact.

## Licence
This project is currently private. Open-source licence will be added when the repository is made public.

## Privacy
Peusic does not contain advertising SDKs, analytics SDKs or any tracking. Network access is used only for authorised downloads (Phase 2) and optional official metadata queries.
