# Peusic

**Peusic** — production offline-first personal music player for Android.

Sonic Aurora visual identity · No ads · No tracking · No accounts · No cloud sync.

Inspired by **Musicolet** (efficient navigation, folders, queues), **Poweramp** (solid playback controls), and **OtoMusic** (clean library + Now Playing).

## Phase 1 status (v1.2.0) — Playing music first

| Feature | Status |
|---------|--------|
| Library tabs: Songs / Albums / Artists / Folders / Playlists | ✅ |
| Search (title, artist, album) | ✅ |
| Play from list / album / artist / folder | ✅ |
| Play Next from track row | ✅ |
| Queue size + Track X of Y | ✅ |
| Shuffle / Repeat off-one-all | ✅ |
| Seek, prev/next, play/pause | ✅ |
| Playback speed 0.75x–1.5x | ✅ |
| Sleep timer 15 / 30 min | ✅ |
| Clear queue | ✅ |
| Mini-player + MediaSession background | ✅ |
| Authorised direct-URL downloader | ✅ |
| Theme + DataStore prefs | ✅ |
| Gapless (Media3) | ✅ |

See **FEATURES.md** for the full Poweramp + Musicolet + OtoMusic feature matrix and Phase 2 plan.

## Architecture

- Kotlin + Jetpack Compose + Material 3
- Media3 ExoPlayer + MediaSessionService
- MediaStore library (READ_MEDIA_AUDIO)
- OkHttp + WorkManager + Room (downloads)
- DataStore preferences

## Build

```bash
./gradlew assembleDebug
```

GitHub Actions uploads **peusic-debug-apk** on every successful push to `main`.

## Privacy

No advertising or analytics SDKs. Network only for user-initiated authorised downloads.

## Licence

Currently private. Dependency licences apply as usual.
