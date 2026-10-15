# Peusic

**Peusic** — production offline-first personal music player for Android.

Sonic Aurora visual identity · No ads · No tracking · No accounts · No cloud sync.

Inspired by **Musicolet** (efficient navigation, folders, queues), **Poweramp** (solid playback controls), and **OtoMusic** (clean library + Now Playing).

## Status v1.4.0 — UI/UX Overhaul (Phase A)

### Playing music ✅
| Feature | Status |
|---------|--------|
| Library tabs: Songs / Albums / Artists / Folders / Playlists | ✅ |
| Search (title, artist, album) | ✅ |
| Play from list / album / artist / folder | ✅ |
| Play Next from overflow menu | ✅ |
| Queue size + Track X of Y | ✅ |
| Shuffle / Repeat off-one-all | ✅ |
| Seek, prev/next, play/pause | ✅ |
| Playback speed 0.75x–1.5x | ✅ |
| Sleep timer 15 / 30 min | ✅ |
| Clear queue | ✅ |
| Mini-player + MediaSession background | ✅ |
| Gapless (Media3) | ✅ |

### UI / Visual (v1.4.0) ✅
| Feature | Status |
|---------|--------|
| Large album art on Now Playing | ✅ |
| Album art thumbnails on library rows | ✅ |
| Proper Material icons (transport, shuffle, repeat, queue) | ✅ |
| FilterChips for speed + sleep | ✅ |
| Rich track rows + overflow menu | ✅ |
| Polished MiniPlayer with art + icon controls | ✅ |
| Graceful fallback when art/metadata missing | ✅ |
| Coil image loading | ✅ |

### Next (Phase B/C)
- Multiple independent queues
- Embedded + LRC lyrics UI
- 10-band EQ + ReplayGain
- Tag editor + M3U import/export
- Hierarchical folders, widgets, Android Auto
- Micro-interactions & empty states polish

See **FEATURES.md** for the complete roadmap.

## Architecture

- Kotlin + Jetpack Compose + Material 3
- Media3 ExoPlayer + MediaSessionService
- MediaStore library (READ_MEDIA_AUDIO)
- Coil for album art
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
