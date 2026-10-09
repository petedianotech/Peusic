# Peusic

**Peusic** — production-ready offline-first personal music player for Android.

Sonic Aurora visual identity · No ads · No tracking · No accounts · No cloud sync.

## Features (v1.1.1)

| Area | Status |
|------|--------|
| Sonic Aurora theme (System / Light / Dark blue) | ✅ |
| Navigation (Discover, Downloads, Music, Settings) + Splash | ✅ |
| Mini-player + full Now Playing (seek, prev/next, shuffle, repeat) | ✅ |
| Authorised direct-URL audio downloader (OkHttp + WorkManager + Room) | ✅ |
| Strict URL validation (blocks YouTube / Spotify / DRM hosts) | ✅ |
| Local library via MediaStore (scoped storage) | ✅ |
| Background playback (Media3 ExoPlayer + MediaSessionService) | ✅ |
| System media controls, notification, lock-screen, Bluetooth | ✅ |
| Queue play from library | ✅ |
| DataStore preferences (theme, gapless, resume, skip-silence) | ✅ |
| Unit tests (URL validator) | ✅ |
| Debug APK artifact via GitHub Actions | ✅ |

## Architecture

- **UI**: Jetpack Compose + Material 3 + Navigation
- **Playback**: Media3 1.5.1 (ExoPlayer + MediaSessionService)
- **Library**: MediaStore queries (READ_MEDIA_AUDIO)
- **Downloads**: OkHttp + WorkManager + Room
- **Settings**: DataStore Preferences
- **Packages**: `ui/`, `domain/`, `data/`, `service/`

## Requirements

- minSdk 26 · target / compile SDK 35 · JDK 17+

## Build

```bash
./gradlew assembleDebug
./gradlew test
```

Debug APK lands in `app/build/outputs/apk/debug/`.

GitHub Actions builds on every push to `main` and uploads the **peusic-debug-apk** artifact (retention 30 days).

## Privacy

Peusic ships with zero advertising or analytics SDKs.  
Network access is used exclusively for user-initiated authorised downloads.  
Library scanning and playback are 100 % on-device.

## Legal downloads

Only paste direct HTTPS/HTTP links to audio files you are authorised to download.  
YouTube, Spotify, SoundCloud, Apple Music and any DRM-protected sources are rejected by the URL validator.

## Known limitations (real-device testing recommended)

- Artwork loading / bounded disk cache (placeholder used today)
- Full queue reordering UI
- Playback speed / sleep-timer polish
- Large-library performance tuning on low-end devices

## Licence

Currently private. Dependency licences (Media3, OkHttp, WorkManager, Room, Compose) apply as usual.
