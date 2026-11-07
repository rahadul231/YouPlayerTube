# YouPlayerTube — Requirements (Phase 01)

Status: **APPROVED** by the user on 2026-11-07 (Phase 01 final; 58 requirements YPT-001–YPT-058, IDs unchanged) · Scope: documentation only, no code. Items marked UNVERIFIED/UNTESTED elsewhere remain so: no code, dependency version, API detail or device behaviour has been verified or tested.
Visual inputs: the 10-screen mockup and orbital-play logo were supplied as `YouPlayerTube_Visual_References_for_Claude.pdf` and inspected; findings and discrepancies are in `visual-qa.md`.

## 1. Source eligibility classes

| Class | Meaning | Native Media3 playback | Native download | Native PiP / background audio | Offline library |
|---|---|---|---|---|---|
| `YouTubeReference` | A YouTube video/playlist ID or URL. Metadata only, never a stream. | No | **No** | No (provider-controlled) | Reference only (app-local favorite/watch-later entry, no media) |
| `LocalUri` | User-imported file via SAF / content URI (owned or licensed) | Yes | N/A (already local) | Yes | Yes |
| `AuthorizedRemote` | Direct URL / manifest the user is entitled to fetch (own server, licensed, public-domain, CC) | Yes | Yes, if rights gate passes | Yes | Yes |

Rule: a `YouTubeReference` can never be converted into `LocalUri` or `AuthorizedRemote`.

## 2. User stories

Legend: S = Supported, C = Conditional, P = Provider-restricted, D = Deferred. Screen = mockup screen number (1–10).

| ID | Story | Screen | Class | Acceptance criteria |
|---|---|---|---|---|
| YPT-001 | As a user I see a branded splash on launch | 1 | S | Supplied logo on black, red loading accent; uses Android SplashScreen API; no added delay beyond real init |
| YPT-002 | Launch goes straight to Home | 1,2 | S | No chooser page; Home is start destination |
| YPT-003 | Home shows a YouTube experience | 2 | C | Per `feasibility.md` section 3: app-owned search + trending shelf from the Data API, playback in the IFrame embed, Open-in-YouTube fallback; never claims parity with the YouTube app |
| YPT-004 | No custom URL/address bar | 2 | S | No user-editable URL field anywhere in app chrome |
| YPT-005 | Official YouTube app/browser fallback | 2,3 | S | Visible "Open in YouTube" action validated against `youtube.com` / `youtu.be` hosts via Intent/Custom Tab |
| YPT-006 | Embed failure states are honest | 2,3 | S | Distinct UI for offline, embed not allowed, video not found, referer/identity error, with Retry and Open-in-YouTube |
| YPT-007 | Four app-owned tabs | 2,5,6,10 | S | Home, Downloads, Library, Settings; selected tab survives rotation and process death |
| YPT-008 | Predictable back behavior | all | S | Back pops within tab stack; exits only from Home root |
| YPT-009 | Video page for native media | 3 | S | Title, details, progress, controls shown for `LocalUri`/`AuthorizedRemote` |
| YPT-010 | Player controls in mandatory order | 3 | S | `Previous \| −10 s \| Play/Pause \| +10 s \| Next` (left to right) in native player only |
| YPT-011 | ±10 s seek, cumulative | 3 | S | Two +10 taps = +20 s from start position; clamped to [0, duration]; unit-tested |
| YPT-012 | Optional double-tap seek (20 s) | 3 | D | Only if it does not conflict with system PiP gestures; decide in Prompt 13 |
| YPT-013 | Download affordance gated by eligibility | 3 | C | Shown enabled only when `DownloadEligibility = Allowed`; on `YouTubeReference` it is omitted or disabled with explanation |
| YPT-014 | Download format sheet lists real options only | 4 | C | Options derived from actual source metadata (MIME, bitrate, resolution, size); no invented 144p–1080p or MP3 |
| YPT-015 | Download Manager queue and progress | 5 | C | Real byte progress; states Queued/Connecting/Running/Completed/Failed/Canceled |
| YPT-016 | Pause/resume only where source supports it | 5 | C | Control hidden/disabled unless HTTP Range or Media3 download supports it |
| YPT-017 | Retry and cancel downloads | 5 | C | Retry after failure; cancel deletes partial file |
| YPT-018 | Offline Library with categories | 6 | S | Videos, Music, Playlists, Favorites, Watch Later, History; empty states instead of demo counts |
| YPT-019 | Library sort and search | 6 | S | Sort by name/date/type; text search over indexed metadata |
| YPT-020 | Import local media | 6 | S | SAF picker; persisted URI grant; no broad storage permission |
| YPT-021 | More menu | 7 | S | Favorite, Repeat, Shuffle, Sleep Timer, Add to Playlist/Watch Later, PiP, Minimize; unsupported items disabled with reason |
| YPT-022 | Favorites (app-local) | 7 | S | Persisted in Room; labelled app-local, no YouTube sync claim |
| YPT-023 | Repeat (off/one/all) | 7 | S | Bound to Media3 repeat mode |
| YPT-024 | Shuffle | 7 | S | Bound to Media3 shuffle mode |
| YPT-025 | Sleep Timer | 7 | S | 15/30/60 min presets; service-owned; actually stops playback; survives rotation |
| YPT-026 | Add to Playlist / Watch Later | 7 | S | Room-backed; YouTube references labelled as external links |
| YPT-027 | Minimize / Lite player | 7 | S | In-app compact player; no overlay permission |
| YPT-028 | Native Android PiP | 8 | S (native media) / P (YouTube) | System PiP only; playback continues; position preserved; feature-detected |
| YPT-029 | Screen-off audio | 9 | S (native media) / P (YouTube) | Audio continues with screen locked via MediaSessionService |
| YPT-030 | Media notification and lock-screen controls | 9 | S (native media) | Title, artwork, play/pause/prev/next; appearance is OEM-controlled |
| YPT-031 | Settings with seven sections | 10 | S | Playback, Downloads, Appearance, Data & Storage, Notifications, Privacy & Security, About; persisted in DataStore |
| YPT-032 | Playlists | 6 | S | Create/rename/delete/reorder; persisted queue |
| YPT-033 | History with clear option | 6,10 | S | Clearing history never deletes media files |
| YPT-034 | Disabled-action copy | all | S | Concise honest text, see section 4 |
| YPT-035 | Zero ad SDKs in app | all | S | Dependency audit shows no ad/tracking SDKs. This does not mean YouTube ads are removed |
| YPT-036 | Minimal permissions | all | S | No `MANAGE_EXTERNAL_STORAGE`; notification permission requested only on first need |
| YPT-037 | Branding uses supplied logo only | 1 | C | Logo supplied (raster on white). Transparent/vector version prepared in Prompt 05 preserving the appearance (your decision Q7); trademark check before any public release; non-affiliation notice in README |
| YPT-038 | Dark theme default | all | S | Tokens in `visual-qa.md` section 3 (approximate); final values confirmed in Prompt 04 |
| YPT-039 | Account sign-in only via provider route | 2 | C | No custom Google login form; no credential or cookie handling by the app |
| YPT-040 | Storage management | 10 | S | Usage computed from actual files; clear cache vs delete media are separate confirmed actions |
| YPT-041 | Accessibility baseline | all | S | TalkBack labels, 48dp targets, font scaling to 200% without clipping |
| YPT-042 | Sideloadable signed release APK | — | S | Verified on a physical Samsung device |
| YPT-043 | YouTube download / audio extraction / ad removal / forced background play | — | **P (not built)** | Must not exist in any build |
| YPT-044 | Facebook/Instagram/X, AI Pro, iOS | — | D (out of scope) | Not built in v1 |
| YPT-045 | Subscribe, like/dislike, Share, Comments row on video page | 3 | P / D | Not shown for `YouTubeReference` (no permitted surface in the embed; account actions need approved OAuth API work). Share may open the system share sheet with the public YouTube URL (C) |
| YPT-046 | Honest splash/tagline/key-feature copy | 1 | S | Approved in principle (Q8): no wording implying YouTube downloads/background play; final strings drafted in Prompts 05/06 for your review; app name stays YouPlayerTube |
| YPT-047 | Real counts and empty states in Library/Downloads | 5,6 | S | Counts computed from Room/files; zero state shown when empty; no demo numbers |
| YPT-048 | Downloads screen filters and search | 5 | C | All / Video / Music chips and search over eligible downloads only |
| YPT-049 | Home header bell and search | 2 | C | Bell omitted (no YouTube notification data). Search icon opens the app-owned Data API search |
| YPT-050 | Settings sections: mockup's six plus Privacy & Security | 10 | S | **Approved (Q9).** Includes history clear and provider/privacy disclosure |
| YPT-051 | Quality setting limited to native sources | 10 | S | "Quality" under Playback applies only to eligible native media; copy says YouTube quality is chosen by YouTube |
| YPT-052 | PiP overlay actions | 8 | C | ⟲10 / pause / ⟳10 as PiP RemoteActions if the device's maximum action count permits; verified in Prompt 08 |
| YPT-053 | Search results list | 2 | C | Query → `search.list` results (thumbnail, title, channel, duration, views, age) in app UI; handles empty, error, quota-reached |
| YPT-054 | Trending shelf | 2 | C | `videos.list chart=mostPopular` by region; labelled "Trending", never "Recommended for you" |
| YPT-055 | Category chips | 2 | C | Only chips that map to real queries/categories; no "AI"/"Mixes" |
| YPT-056 | API key and disclosures | all | C | Key not in Git; restricted by package + signing cert; ToS link, privacy statement and API-use disclosure shown in Settings > About |
| YPT-057 | Embed playback for selected result | 3 | C | IFrame embed in WebView, no overlay on player, ≥200×200 px, Open-in-YouTube on error or disabled embedding |
| YPT-058 | Subscribe / like / comments / Google sign-in | 2,3 | D | Not in v1; shown as "Open in YouTube" only |

## 3. Measurable launch and navigation criteria (to verify on device)

| Metric | Target | Measured how |
|---|---|---|
| Cold start to first Home frame | ≤ 2.0 s on the Samsung test device (provisional) | `adb shell am start -W` TotalTime, 5 runs, median |
| Tab switch | ≤ 300 ms to first frame | Compose benchmark or manual screen recording |
| Back-stack correctness | 100% of defined routes pass | Compose navigation tests |
| Touch targets | ≥ 48dp | Accessibility Scanner |
| Font scale | 1.0×–2.0× without clipped primary actions | Manual |

Targets are provisional until baseline numbers exist.

## 4. Disabled-action copy (draft)

| Situation | Copy |
|---|---|
| Download on a YouTube video | "Downloads aren't available for YouTube videos in this app. You can use YouTube's own offline options if you have access." |
| PiP on a YouTube embed | "Picture-in-picture for YouTube videos is controlled by YouTube." |
| Background play on a YouTube embed | "YouTube playback stops when you leave the screen." |
| Pause/resume unsupported by server | "This server doesn't support resuming. Retry will restart the download." |
| Format sheet with one option | "Only one format is available for this file." |
| PiP unsupported / disabled | "Picture-in-picture isn't available on this device or is turned off in system settings." |
| Embed not allowed | "The owner doesn't allow this video to play here. Open it in YouTube instead." |

## 5. Contract mapping (future code)

| Area | Contract |
|---|---|
| Home / Online | `YouTubeGateway` returns `YouTubeReference` or `OpenExternal`; never a stream URL |
| Player | Consumes `MediaItem` whose `source` is `LocalUri` or `AuthorizedRemote` only |
| Downloads | `DownloadEligibility` evaluated before the format sheet opens |
| Library | Room stores metadata and URIs; `YouTubeReference` rows carry no media URI |
