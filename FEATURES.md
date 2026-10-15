# Peusic Feature Roadmap
## Goal: Match Poweramp + Musicolet + OtoMusic core strengths

**Focus: Playing music first.** Offline-first, no ads, no accounts.  
UI/UX style inspirations (layout & interaction, not colors):
- **Musicolet**: one-row efficient navigation, multiple queues, folder browser, minimal friction
- **Poweramp**: powerful player controls, queue, EQ depth, gapless/crossfade, library power
- **OtoMusic**: clean Material library, Now Playing themes, lyrics, playlists, sleep timer

Keep Peusic **Sonic Aurora** visual identity (colors/theme stay ours).

---

## Master Feature List

### A. Playback Engine (priority)
| Feature | Poweramp | Musicolet | OtoMusic | Peusic target |
|---------|----------|-----------|----------|---------------|
| Gapless | ✅ | ✅ | ✅ | Phase 1 |
| Crossfade | ✅ | – | ✅ | Phase 1 |
| Shuffle / Repeat off-one-all | ✅ | ✅ | ✅ | Phase 1 (done) |
| Dynamic queue + reorder | ✅ | ✅ multi | ✅ | Phase 1 |
| Multiple independent queues | – | ✅ (up to 20) | – | Phase 2 |
| Playback speed | ✅ | ✅ | ✅ | Phase 1 |
| Sleep timer (time / N songs) | ✅ | ✅ | ✅ | Phase 1 |
| A-B repeat | ✅ | ✅ | – | Phase 2 |
| ReplayGain | ✅ | – | ✅ | Phase 2 |
| Equalizer (graphical + presets) | ✅ advanced | ✅ per-output | ✅ | Phase 1 basic / Phase 2 advanced |
| Fade in/out on pause | – | – | ✅ | Phase 1 |
| Hi-Res / USB DAC / DVC | ✅ | – | – | Future (Phase 3+) |

### B. Library & Browsing
| Feature | Sources | Phase |
|---------|---------|-------|
| Songs list | All | 1 |
| Albums / Artists | All | 1 |
| Folders (linear) | Musicolet, Oto | 1 |
| Hierarchical folders | Musicolet | 2 |
| Genres + multi-genre split | Oto, Poweramp | 2 |
| Search (title/artist/album) | All | 1 |
| Sort options | All | 1 |
| Playlists create/edit/import/export M3U | All | 1 basic / 2 full |
| Smart playlists (most played etc.) | Oto, Poweramp | 2 |

### C. Now Playing & UI
| Feature | Phase |
|---------|-------|
| Large artwork + controls | 1 |
| Mini-player (persistent) | 1 (done) |
| Queue sheet from Now Playing | 1 |
| Lyrics (embedded + LRC synced) | 2 |
| Swipe gestures | 2 |
| Multiple Now Playing layouts | 2 |
| Lock-screen rich controls | 1 (MediaSession) |

### D. Management & Extras
| Feature | Phase |
|---------|-------|
| Tag editor (single + batch) | 2 |
| Album art edit/download | 2 |
| Bookmarks / notes | 2 |
| Widgets | 2 |
| Android Auto | 2 |
| Chromecast | 2 |
| Home-screen shortcuts | 2 |

---

## Phase 1 – Core Playback Experience (this build)
**Goal: Excellent everyday local music playback, Musicolet efficiency + solid Poweramp-style controls.**

1. Library tabs: **Songs | Albums | Artists | Folders | Playlists**
2. Search bar on Music screen
3. Full queue management: view, play-next, remove, clear
4. Playback speed control (0.5x–2.0x)
5. Sleep timer (minutes or end of current track)
6. Gapless confirmed + simple crossfade preference
7. Improved Now Playing (art area, speed, sleep, queue access, Track X of Y)
8. Basic playlist create + add tracks
9. Folder browser (linear)
10. Sort options for tracks

**Version target: 1.2.0**

## Phase 2 – Depth & Polish (next response)
- Multiple queues (Musicolet-style)
- Lyrics (embedded + LRC + editor)
- Advanced EQ (10-band + presets, per-device later)
- ReplayGain
- Tag editor
- M3U import/export full
- Hierarchical folders
- Widgets + Android Auto foundation
- A-B repeat, bookmarks

---

After Phase 1 the app must:
- Scan local library reliably
- Play any track with solid queue + controls
- Feel fast and organised like Musicolet
- Offer the playback tools people open Poweramp/Oto for daily
