# Peusic

**Peusic** — production offline-first personal music player for Android.

Sonic Aurora visual identity · No ads · No tracking · No accounts · No cloud sync.

Inspired by **Musicolet** (efficient navigation, folders, queues), **Poweramp** (solid playback controls), and **OtoMusic** (clean library + Now Playing).

## Status v1.3.0 — Phase 1 + Phase 2 core

### Playing music (Phase 1) ✅
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
| Gapless (Media3) | ✅ |

### Phase 2 foundation ✅
| Feature | Status |
|---------|--------|
| FEATURES.md full matrix vs Poweramp / Musicolet / OtoMusic | ✅ |
| Production CI (self-bootstrapping Gradle + APK artifact) | ✅ |
| Authorised direct-URL downloader | ✅ |
| Theme + DataStore prefs | ✅ |
| Version 1.3.0 production packaging | ✅ |

### Next (optional polish)
- Multiple independent queues (Musicolet-style)
- Embedded + LRC lyrics UI
- 10-band EQ + ReplayGain
- Tag editor + M3U import/export
- Hierarchical folders, widgets, Android Auto

See **FEATURES.md** for the complete roadmap.

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
