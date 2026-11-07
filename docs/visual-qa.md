# Visual QA Matrix (Phase 01)

Updated: 2026-11-07 (Asia/Dhaka) · **Status: INSPECTED and APPROVED (original ten-screen design, 2026-11-07)** — `YouPlayerTube_Visual_References_for_Claude.pdf` (2 pages) was supplied and viewed.
- PDF page 1: one composite sheet (1536×1024 raster) with the ten phone screens, a project-structure panel, a tech-stack panel and a key-features panel.
- PDF page 2: logo on white (1600×900 JPEG).

Caveats: the sheet is a flattened raster, not layered design source. Dimensions and colours below are read from the picture and are approximate. Playbook note: the iOS-flavoured status bar, lock screen and home-screen icons in screens 8–9 are not an Android spec.

## 1. Ten-screen matrix (what the mockup actually shows)

| # | Screen | Elements observed | Implementing prompt | Native/system-controlled | Baseline screenshot |
|---|---|---|---|---|---|
| 1 | App Launch (Splash) | Black bg with red wave glow at bottom; centred orbital-play logo; wordmark (You/Tube white, Player red); tagline "YouTube Player & Downloader"; red ring spinner; text "Loading YouTube…" | 05 | Android 12+ system splash | Pending |
| 2 | Home (YouTube Directly) | Top bar: small logo + wordmark, bell icon, search icon; chip row (AI, **All** selected, Music, Mixes, Live, Tutorials); large video cards with thumbnail, duration badge, channel avatar, title, "⋮"; bottom nav Home/Downloads/Library/Settings (Home active, red) | 06 | Anything inside a YouTube embed | Pending |
| 3 | Video Play with Download Button | Back arrow, cast/PiP-style icon, ⋮; player with 1:16 / 3:56 + fullscreen icon, thin red progress; title, "4.8M views · 3 years ago"; channel avatar + name + red **Subscribe**; action row: 👍 40K, 👎, Share, red **Download** (clipped at the right edge); Comments 342 card | 06 (provider part), 07 (native part) | Embedded player UI, Subscribe/likes/comments | Pending |
| 4 | Select Quality & Download | Header with thumbnail/title/3:56; **Audio (Music)** radios: 64k M4A 2.4 MB, 128k M4A 4.6 MB (selected), 192k MP3 6.8 MB, 256k MP3 9.1 MB; **Video** radios: 144p 6.4 MB, 240p 8.3, 360p 11.9, 480p 19.5, 720p HD 30.9, 1080p HD 67.2 MB; full-width red **Download** button | 10 | — | Pending |
| 5 | Download Manager | Title + search icon; chips All (selected) / Video / Music; active row "45.2 MB / 67.2 MB · 3.1 MB/s" with red progress bar and pause icon; two completed rows with green checks (36.6 MB, 528.9 MB) | 10 | — | Pending |
| 6 | Offline Library | Title + ⋮; rows: Downloaded Videos "24 videos · 3.2 GB", Downloaded Music "186 songs · 882 MB", Playlists "5 playlists", Favorites "32 items", Watch Later "12 items", History "Recently watched" | 11–12 | — | Pending |
| 7 | More Options (⋮) | Bottom sheet over player: quick row Favorite / Repeat / Sleep / PiP; list: Repeat (toggle on), Sleep Timer "After 30 min", Add to Watch Later, Add to Playlist, Picture-in-Picture, Minimize Player (Lite) | 13 | — | Pending |
| 8 | Picture-in-Picture | Red-outlined floating window: ✕ top-left, expand and ✕ top-right, centre pause with ⟲10 left and ⟳10 right, red progress bar; shown over an iOS-style home screen (WhatsApp, Chrome, Notes, Gmail, dock) | 08 | PiP window chrome, controls, placement (system/OEM) | Pending |
| 9 | Background Audio (Screen Lock) | iOS-style lock screen: padlock, 6:52, "Fri, Nov 6"; media card with art, title, artist, 1:16 / 3:56 scrubber, ⏮ ⏸ ⏭ style controls | 09 | Notification and lock-screen look (system/OEM) | Pending |
| 10 | Settings | Title; rows with subtitles: Playback (Quality, PiP, Background), Downloads (Path, Concurrent downloads), Appearance (Dark theme, UI), Data & Storage (Cache, Clear data), Notifications (no subtitle), About (YouPlayerTube v1.0) | 14 | System notification settings | Pending |

Navigation: four bottom tabs **Home, Downloads, Library, Settings**; active tab icon and label red, inactive grey, on screens 2, 5, 6, 10. Screens 3, 4, 7 have no bottom bar.

## 2. Discrepancies between mockup, playbook and requirements

| # | Item | Mockup | Playbook / requirements | Decision needed |
|---|---|---|---|---|
| D1 | Settings sections | 6 rows (no Privacy & Security) | 7 sections incl. Privacy & Security | **DECIDED 2026-11-07 (Q9): keep Privacy & Security** as a deliberate deviation |
| D2 | Mandatory button order `Previous \| −10 \| Play/Pause \| +10 \| Next` | **Not drawn anywhere.** PiP shows ⟲10 / pause / ⟳10; lock screen shows rewind / pause / forward | Mandatory in native player | Playbook order stays the spec; mockup gives no pixel positions for it (see section 4) |
| D3 | Package path in structure panel | `java/com/youplayertube/` | `com.youplayertube.app` | **DECIDED (Q10): `com.youplayertube.app`** |
| D4 | DI | "Hilt / Koin" | Hilt only if justified, no Koin | **Rule (Q10): Hilt only if justified by the architecture and verified compatible; decided in Prompt 03** |
| D5 | Structure panel `utils/` "YouTube parsing" | Implies scraping/parsing YouTube | Prohibited extraction | Do not create; rename to `util/` generic helpers only |
| D6 | Splash/header copy "YouTube Player & Downloader", "Loading YouTube…", header strip "Background Play • Download" | Marketing wording | Provider boundaries | **Approved in principle (Q8):** replace with honest copy; final strings reviewed in Prompts 05/06 |
| D7 | Key-features panel ticks "Open YouTube directly", "Background & screen-off play", "Download (MP3/M4A/MP4)", "Quality: 144p–1080p" for YouTube | Claims about YouTube content | Prohibited / provider-restricted | Features apply to native media only; README must say so |
| D8 | Library/Download counts (24 videos 3.2 GB, 186 songs, etc.) | Demo data | No fake data | Real empty states and computed counts |
| D9 | Screen 4 sizes vs real formula | 128k M4A × 3:56 shown 4.6 MB; formula gives ≈ 3.8 MB (128 kbps × 236 s / 8). 64k shown 2.4 MB (≈ 1.9); 256k shown 9.1 MB (≈ 7.6) | Size = bitrate × duration / 8 | Mockup sizes are illustrative only; use real metadata |
| D10 | Screen 4 mixes M4A and MP3 | MP3 192k/256k | MP3 needs transcoding | Deferred; list only real container |
| D11 | Home bell icon and chip row | Mimics YouTube notifications and filters | No provider data without approved API | Bell omitted; chips map to real queries/categories (see `feasibility.md` section 3) |
| D12 | iOS visuals in screens 8–9 | iOS lock screen, home screen, status bar | Android-only | Accepted deviation |
| D13 | Screen 3 Download button clipped at right edge | Layout flaw in mockup | — | Fix during layout; not a spec |

## 3. Design tokens

Measured by sampling pixels from the PDF raster (approximate; JPEG-like artefacts; samples taken at estimated positions, several samples were off-target and are discarded). Re-measure from a larger original in Prompt 04.

| Token | Value | Basis |
|---|---|---|
| Brand / primary red (Download button) | ≈ `#FA0F24` | Sampled, close to playbook `#EF202B` |
| Background (screens) | near-black ≈ `#050507`–`#060606` | Sampled; playbook `#0B0B0E` |
| Sheet/surface | ≈ `#111312` | Sampled (quality sheet panel); playbook `#17171C` |
| Primary text | white | Observed |
| Secondary text | mid/light grey (not measured) | Observed |
| Chip selected | light-grey pill with dark text (Downloads chips); red used for Home active tab | Observed |
| Success | green check, ≈ standard green (not measured) | Observed |
| Page backdrop (presentation sheet only) | deep maroon/black gradient | Not an app token |
| Typography | Clean sans (Roboto-class) for UI; heavy bold sans for wordmark; exact face unidentified | Observed |
| Radii, spacing, icon sizes | Rounded cards/pills (~12–16dp look), 24dp-style line icons; not measurable from this raster | Estimate only |

Final decision: use playbook tokens as defaults, adjust to measured values in Prompt 04 after you confirm.

## 4. Player button positions

| Screen | Positions as drawn |
|---|---|
| 3 video page | Top row: back (left), cast/PiP-style icon and ⋮ (right). Player footer: time left, fullscreen right. No transport button row visible |
| 7 More | Quick row left→right: Favorite, Repeat, Sleep, PiP |
| 8 PiP | ✕ top-left; expand + ✕ top-right; ⟲10 (left), ⏸ (centre), ⟳10 (right) |
| 9 Lock screen | ⏮ / rewind (left), ⏸ (centre), ⏭ / forward (right) |
| Spec for native player | `Previous \| −10 s \| Play/Pause \| +10 s \| Next` (playbook); to be laid out in Prompt 07 and signed off by you |

## 5. Logo inspection (PDF page 2)

| Item | Finding |
|---|---|
| Mark | Red rounded rectangle with white play triangle, orbited by a red swoosh ring |
| Wordmark | "You" black, "**Player**" red, "Tube" black (white/red/white on dark splash), red underline swoosh |
| Format | 1600×900 raster (JPEG) on white; no transparency, no vector |
| Gap | Splash and adaptive icon need a transparent PNG/SVG of the mark (and a light-text wordmark variant). **Per your decision (Q7):** a transparent/vector version is prepared in Prompt 05 preserving the appearance |
| Risk | Mark closely follows the YouTube play-button silhouette and the name contains "YouTube"; see Q3 and Q8. Not a legal opinion |

## 6. Accepted deviations (proposed)

| Item | Reason |
|---|---|
| iOS-style notification, lock screen, PiP and home-screen visuals | Android system UI differs and is OEM-controlled |
| YouTube embed appearance, ads | Provider-controlled |
| No Subscribe / like / dislike / comments for YouTube references | No permitted surface (see gap register) |
| Demo counts replaced by real values | No fake data |

## 7. Review procedure (Prompt 04 onward)

1. Capture portrait screenshots at 360dp, 393dp and a tablet width, plus font scale 1.3× and 2.0×.
2. Place beside the mockup screen; log each difference with severity and whether it is system-controlled.
3. Store baselines under `docs/screenshots/` (no copyrighted third-party content).
