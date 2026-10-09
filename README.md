# Peusic

**Peusic** — offline-first personal music player for Android.

Sonic Aurora visual identity · No ads · No tracking · No accounts · No cloud sync.

## Features (v1.1.0)

| Area | Status |
|------|--------|
| Sonic Aurora theme (System / Light / Dark blue) | ✅ |
| Navigation (Discover, Downloads, Music, Settings) | ✅ |
| Splash + Now Playing | ✅ |
| Mini-player bar (play/pause/next + progress) | ✅ |
| Authorised direct-URL downloader (OkHttp + WorkManager + Room) | ✅ |
| Strict URL validation (blocks YouTube/Spotify/etc.) | ✅ |
| Local library via MediaStore | ✅ |
| Background playback (Media3 MediaSessionService) | ✅ |
| Queue play from library, seek, shuffle, repeat | ✅ |
| DataStore preferences | ✅ |
| Unit tests (URL validator) | ✅ |
| Debug APK via GitHub Actions | ✅ |

## Requirements

- minSdk 26 · target/compile SDK 35 · JDK 17+

## Build

```bash
./gradlew assembleDebug
./gradlew test
```

Debug APK: `app/build/outputs/apk/debug/`

GitHub Actions uploads the APK artifact on every successful push to `main`.

## Privacy

No advertising or analytics SDKs. Network is used only for user-initiated authorised downloads. Library and playback stay on device.

## Legal downloads

Only direct HTTPS/HTTP audio file URLs you are authorised to download. No YouTube, Spotify, SoundCloud, or DRM circumvention.

## Licence

Currently private. Dependency licences (Media3, OkHttp, WorkManager, Room, Compose) apply as usual.
