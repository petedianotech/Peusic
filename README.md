# Peusic

**Peusic** — premium offline-first personal music player for Android.

Sonic Aurora visual identity • No ads • No tracking • No accounts • No cloud sync.

## Architecture

- Kotlin + Jetpack Compose + Material 3
- Media3 ExoPlayer + MediaSessionService
- MediaStore local library
- OkHttp + WorkManager + Room for authorised downloads
- DataStore for user preferences
- Packages: `ui/`, `domain/`, `data/`, `service/`

## Requirements

- **minSdk 26**
- Target / compile SDK 35
- JDK 17+

## Features (v1.0.0)

| Area | Status |
|------|--------|
| Sonic Aurora design system | ✅ |
| Navigation | ✅ |
| Authorised direct-URL downloader | ✅ |
| Local library (MediaStore) | ✅ |
| Background playback + system controls | ✅ |
| Theme preference (System / Light / Dark blue) | ✅ |
| Playback preferences | ✅ |
| Privacy statement & troubleshooting | ✅ |
| Unit tests (URL validation) | ✅ |
| Debug APK via GitHub Actions | ✅ |

## Explicit non-goals

- No ads or analytics SDKs
- No accounts or cloud sync
- No YouTube / Spotify / DRM circumvention
- LibVLC not shipped (Media3 only)

## Build

```bash
./gradlew assembleDebug
./gradlew test
```

GitHub Actions builds on every push/PR and uploads the debug APK artifact.

## Privacy

No advertising or analytics SDKs. Network used only for user-initiated authorised downloads. Library and playback stay on device.

## Known limitations

- Full player polish (shuffle/repeat/speed/sleep UI, artwork cache)
- Queue reordering UI
- Exact codec matrix on physical devices
- Some UI source files may still need full remote sync

## Licence

Currently private. Dependency licences apply as usual.
