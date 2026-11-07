# YouTube Provider Boundaries (Phase 01)

Status: part of Phase 01, **APPROVED 2026-11-07**. Items marked "No"/"not verified" remain UNVERIFIED. Access date: **2026-11-07**. Paraphrased from official pages; re-verify before Phase 06 and before any public release. This is engineering guidance, not legal advice.

Re-fetched and confirmed in revision 2: required-minimum-functionality (page dated 2026-11-06), IFrame Player API reference, Policies Guide (page dated 2026-09-14). Carried over from the first pass and NOT re-fetched in revision 2: developer-policies main page, iframe revision history, Android PiP page, the secondary news source.

## 1. Compliance table

| Topic | What the official material says (paraphrased) | Source | Rule for this project |
|---|---|---|---|
| Download / cache / offline copies | API clients must not download, import, back up, cache or store copies of YouTube audiovisual content, or enable offline playback, without YouTube's prior written approval | https://developers.google.com/youtube/terms/developer-policies | No download, caching or offline storage of any `YouTubeReference` |
| Downloads outside Premium | The policies guide lists allowing downloads outside the YouTube Premium experience as a prohibited example | https://developers.google.com/youtube/terms/developer-policies-guide | No Download action on YouTube pages |
| Separating audio/video | Must not separate, isolate or modify audio or video components, or promote them separately; the guide cites MP3 offerings as an example | developer-policies; developer-policies-guide | No audio-only mode, MP3 option or alternate tracks for YouTube content |
| Ads | Must not modify, interfere with, replace or block ads served by YouTube or its API services, including in players | developer-policies; guide | No ad blocking, skipping or overlay tricks. The app's own UI has no ads; YouTube's ads may still appear in the embed |
| Player modification | Must not modify, build upon or block any portion or function of the YouTube player | developer-policies; guide | No JS injection, no CSS hiding of player UI, no custom controls driving YouTube's player beyond documented API calls |
| Background play | Must not create or promote features that play content from a background player (one not displayed on the screen the user is viewing) | developer-policies | No service-based or screen-off playback of YouTube content |
| Playback context signals | Must not restrict or fail to provide accurate signals that let YouTube verify origin and context; example given: interfering with cookies | developer-policies-guide | Do not strip cookies, referer or identity; do not spoof origin |
| Credentials | Must not request, collect or store users' YouTube login credentials | developer-policies (principle 4, privacy) | No custom login form; no cookie or token access |
| Embedded player size | Viewport at least 200×200 px; controls must fit without shrinking it below that; 16:9 recommended at least 480×270 | https://developers.google.com/youtube/terms/required-minimum-functionality | Layout test at 360dp width portrait and landscape |
| Overlays | No overlays, frames or visual elements in front of or obscuring any part of the embedded player, including controls | required-minimum-functionality | App-owned UI must sit outside the player bounds. Our PiP/More menus cannot float over the embed |
| Player identity | Embeds must send a referer or equivalent client identification; the IFrame API now returns error 153 when missing. YouTube extended the Android WebView Media Integrity API so embedding apps send an attested app ID | https://developers.google.com/youtube/iframe_api_revision_history | Spike must test error 153 behavior and the Media Integrity setup. Exact integration steps NOT yet verified |
| Search playlists | `cuePlaylist`/`loadPlaylist` with `listType=search` was deprecated and returns 4xx; use the Data API `search.list` instead | iframe_api_revision_history | No "search inside the embed". Search needs Data API, which carries API-client obligations |
| Playback quality | `setPlaybackQuality`, `getAvailableQualityLevels` and `suggestedQuality` are no longer supported (no-ops) | iframe_api_revision_history | A 144p–1080p chooser for YouTube cannot be implemented |
| API client duties | Privacy policy, link to YouTube ToS, disclosure of use of YouTube API Services, user data deletion | developer-policies | Required if Data API / API Services are used. Decide in Prompt 06 |
| Background play outside the API | Google confirmed it blocks background play for non-Premium users in third-party mobile browsers (news reports, early 2026) | https://news.slashdot.org/story/26/02/03/1655249/youtube-kills-background-playback-on-third-party-mobile-browsers (secondary) | Do not rely on WebView background audio. Secondary source only |
| Android PiP | PiP is available from Android 8.0 (API 26); activity must declare `supportsPictureInPicture`; handle configChanges | https://developer.android.com/guide/topics/ui/picture-in-picture | Min SDK 26 holds. Android 12+ auto-enter and custom-action limits to be verified in Prompt 08 |

Revision 2 additions: the IFrame reference confirms the Media Integrity extension (embedding apps send an attested app ID; configuration and an opt-out exist) and error 153 wording; it documents no `setPlaybackQuality`, and says `listType=search` is deprecated (valid values playlist, user_uploads). The Policies Guide says videos cannot keep playing when the service window is closed or minimized, that links should open in the YouTube app or system browser, and that apps must not be mistakable for YouTube.

Not verified in this phase: exact Media Integrity API implementation steps, Google's current policy on OAuth in embedded WebViews, YouTube Terms of Service wording on browsing youtube.com in a WebView, trademark/branding rules for an app named "YouPlayerTube". Each is an open question below.

## 2. Hard prohibitions (build blockers)

1. No `yt-dlp`, NewPipe-style extractors, scraping, hidden endpoints, or stream interception.
2. No ad suppression of any kind.
3. No DRM bypass, request forgery, altered origin or forged player identity.
4. No cookie, token or credential capture; no custom Google sign-in.
5. No `addJavascriptInterface` bridge on a WebView that loads YouTube.
6. No code path from `YouTubeReference` to `DownloadCoordinator` or to a Media3 `MediaItem` URI.

## 3. Gap register (mockup elements without a permitted implementation)

| # | Mockup element | Gap | Resolution |
|---|---|---|---|
| G1 | Floating Download button on a YouTube video page (screen 3) | Prohibited for YouTube content | Omit on YouTube; show only for native eligible media. Show honest copy |
| G2 | Format sheet with 144p–1080p and MP3/M4A for YouTube (screen 4) | Prohibited and quality API removed | Sheet only for eligible sources; lists real options only |
| G3 | Download Manager entries from YouTube (screen 5) | Same as G1 | Manager contains only eligible downloads |
| G4 | Home "directly shows YouTube" with provider search/subscriptions (screen 2) | No approved embed reproduces YouTube's feed, search or subscriptions; embed search playlists are removed | Resolved in `feasibility.md` section 3: own search UI + trending shelf via Data API; no personalised feed or subscriptions |
| G5 | PiP of YouTube video (screen 8) | Provider-controlled | Native PiP only for native media; YouTube uses whatever YouTube provides |
| G6 | Screen-off audio of YouTube (screen 9) | Background player prohibited | Native media only |
| G7 | More menu actions on YouTube videos (screen 7) | Favorite/Watch Later are app-local only; Repeat/Shuffle/Sleep Timer/Minimize cannot drive the embed | Disable with reason, or app-local reference list |
| G8 | Account-backed Home (subscriptions, history) | No custom login, no credentials | Handoff to official YouTube only |
| G9 | Logo | Supplied as 1600×900 JPEG on white; no transparency or vector; resembles YouTube's play mark | Request transparent master (Q7); trademark review (Q3/Q8) |
| G10 | Mockup is iOS-flavoured in places (AirPlay, notification look) | System UI is OEM-controlled on Android | Document as accepted deviation |
| G11 | Subscribe / like / dislike / comments (screen 3) | No permitted surface in the embed; account writes need OAuth Data API | Omit for `YouTubeReference`; Deferred |
| G12 | Home as a YouTube-style feed with chips, cards, bell (screen 2) | Guide warns against apps mistakable for YouTube and mass-aggregated embeds | Data API search + trending shelf + Open-in-YouTube (`feasibility.md` section 3) |
| G13 | Structure panel `utils/` "YouTube parsing" | Implies scraping | Not created; generic helpers only |
| G14 | Key-features panel and header strip claims (background play, MP3/M4A/MP4, 144p–1080p for YouTube) | Prohibited for YouTube | README/listing wording limits these to native media |
| G15 | Splash tagline "YouTube Player & Downloader", "Loading YouTube…" | Implies a downloader/affiliation | Replace copy (Q8) |
| G16 | Quality 144p–1080p chooser | `setPlaybackQuality` not documented | Native sources only |
| G17 | Mockup Download button clipped (screen 3) and Settings lacks Privacy & Security | Layout / scope discrepancies | Logged in `visual-qa.md` D1, D13 |

## 4. YouTube Data API facts used for Home (checked 2026-11-07)

| Fact | Source | Verified |
|---|---|---|
| Default quota about 10,000 units/day plus about 100 `search.list` calls/day; separate `search.list` bucket from 1 Jun 2026; higher quota needs audit | https://developers.google.com/youtube/v3/getting-started ; https://developers.google.com/youtube/v3/revision_history | Yes |
| `relatedToVideoId` unsupported since 7 Aug 2023 | revision history | Yes |
| `activities.list home` deprecated 2016; not a supported personalised feed | revision history | Yes |
| `mostPopular` chart definition changed 21 Jul 2025 | revision history | Yes |
| Writes (rate, subscribe, comment) need OAuth 2.0; public reads can use an API key | getting-started | Yes |
| `disallowed_useragent` for sign-in in disallowed embedded user agents | https://developers.google.com/youtube/data/guides/auth/installed-apps | Yes; Android-specific flow not fetched |
| Per-method quota costs, `videoEmbeddable`, API-key restriction steps | Not reachable this session | **No** (Q11) |

