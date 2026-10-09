# Peusic

**Peusic** is a premium, offline-first personal music player for Android.

Sonic Aurora visual identity • No ads • No tracking • No accounts • No cloud sync.

## Architecture

- Kotlin + Jetpack Compose + Material 3
- Media3 ExoPlayer + MediaSessionService for playback
- MediaStore for local library
- OkHttp + WorkManager + Room for authorised downloads
- Clean packages: `ui/`, `domain/`, `data/`, `service/`

## Minimum SDK

**minSdk = 26**. Target/compile SDK 35.

## Phase 3 — Local library & playback

### Implemented
- Real MediaStore scanning (Songs / Albums / Artists / Folders)
- Runtime permission for READ_MEDIA_AUDIO / READ_EXTERNAL_STORAGE
- PlaybackService (MediaSessionService + ExoPlayer)
- MediaController-based PlaybackController
- Play all / play from track
- System media controls & notification via MediaSession
- Audio focus + becoming-noisy handling

### Playback engine decision
**Media3 ExoPlayer only.** Common local formats are covered. LibVLC was evaluated and not added; no measured gaps justified the size/licensing cost. Architecture remains open for a future evidence-based fallback.

### Still needs real-device work
- Full shuffle/repeat/speed/sleep-timer UI
- Artwork caching
- Queue reordering UI
- Exact codec matrix

## Feature status

| Area | Status |
|------|--------|
| Design system & navigation | ✅ |
| Authorised direct URL downloader | ✅ |
| Local library (MediaStore) | ✅ |
| Background playback (Media3) | ✅ |
| System media controls | ✅ |
| Full player polish | Partial |
| Settings persistence | Phase 4 |

## Build

```bash
./gradlew assembleDebug
./gradlew test
```

## Privacy
No ads or analytics. Network only for user-initiated authorised downloads. Library and playback stay on device.
