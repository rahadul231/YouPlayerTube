# Feasibility and Online-Integration Recommendation (Phase 01)

Date: 2026-11-07 (revision 3, **APPROVED 2026-11-07**). Dependency versions, Data API per-method details, Media Integrity/OAuth steps and all device behaviour remain UNVERIFIED/UNTESTED. Evidence: see `provider-boundaries.md`. No code or device has been tested in this phase.

## 1. Feature classification

| Feature | Class | Notes |
|---|---|---|
| Splash, four tabs, dark theme, Settings | Supported | Pure native Compose |
| Local import, Media3 playback, ±10 s seek | Supported | Needs real-device codec tests |
| Native PiP for local/authorized media | Supported | API 26+; OEM variation; device test required |
| Screen-off audio, notification, lock-screen controls | Supported | MediaSessionService; foreground-service config; Samsung battery policies to document |
| Offline library, playlists, favorites, history, sleep timer | Supported | Room, DataStore, Media3 |
| Authorized direct-URL downloads | Conditional | Requires rights gate, HTTP status/Range/MIME checks |
| Format sheet | Conditional | Only real options from source metadata |
| MP3 conversion | Deferred | Needs separate transcoding and licensing decision, not a rename |
| YouTube in Home: embedded video playback | Conditional | IFrame embed; identity, size and overlay rules apply |
| YouTube in Home: keyword search and a trending shelf (own UI via Data API) | Conditional | See section 3 |
| YouTube in Home: personalized recommendations, subscriptions feed, YouTube notifications | Provider-restricted | No supported API reproduces them; see section 3 |
| YouTube account sign-in, Subscribe, like, comments inside the app | Deferred | Needs OAuth Data API work; see section 3 |
| YouTube download, audio extraction, quality chooser | Provider-restricted | Prohibited / API removed |
| YouTube ad removal, background play, native PiP of embed | Provider-restricted | Prohibited / provider-controlled |
| Android Cast replacing AirPlay | Deferred | Only if a real receiver integration is later approved |
| Facebook/Instagram/X, AI Pro, iOS | Deferred / out of scope | Not in v1 |

"No in-app ads" means the app's own UI has no ads and no ad SDKs. It does not mean YouTube's ads disappear.

## 2. Integration options compared

| Option | What it is | Fits "no URL bar, YouTube directly"? | Policy risk | Reliability | Verdict |
|---|---|---|---|---|---|
| A. Official YouTube app / Custom Tab handoff | Intent to YouTube app or browser | Partly: leaves the app | Lowest | High | **Mandatory fallback** |
| B. IFrame Player embed in OS WebView | Our UI picks a video ID; YouTube's player renders it | For playback only, not feed/search | Moderate: identity (error 153), Media Integrity, overlay and size rules, API-client duties if Data API used | Medium; needs spike | **Spike candidate** |
| C. General WebView browsing of m.youtube.com | Full YouTube site inside our shell | Looks closest to mockup | Unresolved: ToS status unverified, sign-in likely blocked in WebViews (unverified), pressure toward injection/blocking, background play being blocked for non-Premium | Low–medium, fragile | **Not chosen for spike**; revisit only if you explicitly accept the risk after reading this |

## 3. Q1 resolution: how close can Home get to the mockup?

Your preference: open straight into a YouTube-focused Home, no address bar, no site chooser. The approach evaluated: **YouTube Data API search + IFrame embedded player + external YouTube fallback**.

### 3.1 What the Data API supports (checked 2026-11-07)

| Fact | Source | Status |
|---|---|---|
| Default quota: 10,000 units/day for general endpoints and about 100 `search.list` calls/day; since 1 June 2026 `search.list` has its own quota bucket; more than the default needs a compliance audit (from 1 July 2021) | https://developers.google.com/youtube/v3/getting-started ; https://developers.google.com/youtube/v3/revision_history | Verified (getting-started page and revision history). Per-method cost table NOT fetched (page not reachable) |
| `search.list` `relatedToVideoId` ("related videos") stopped working 7 Aug 2023 | revision history | Verified |
| `activities.list` `home` parameter: deprecated 12 Sep 2016; later returned roughly a logged-out homepage; unauthorized requests got a 403 `homeParameterDeprecated` error | revision history | Verified; not a supported personalised feed |
| `videos.list` `chart=mostPopular` includes Trending plus Music, Movies and Gaming (changed 21 Jul 2025) | revision history | Verified; per-region/category parameters expected, confirm in spike |
| Insert/update/delete (including rating a video) require OAuth 2.0; some list methods return public data with an API key | getting-started | Verified |
| OAuth sign-in in a Google-disallowed embedded user agent fails with `disallowed_useragent`; Android authorization is described in a separate Android guide | https://developers.google.com/youtube/data/guides/auth/installed-apps | Verified for the error; Android-specific flow NOT verified (guide not fetched) |
| `commentThreads`: channel comments no longer retrievable/insertable via API (30 Apr 2024) | revision history | Verified; video comment read still expected, confirm in spike |

### 3.2 Mockup element → achievable?

| Mockup element (screen) | Achievable | How / limit |
|---|---|---|
| Home opens directly, no URL bar, no chooser (2) | **Yes** | App-owned Compose screen; no address field |
| Search icon and results (2) | **Yes, own UI** | `search.list` results rendered by us; about 100 searches/day default, enough for personal use |
| Video cards: thumbnail, title, channel, duration, views, age (2) | **Yes (expected)** | `search.list` + `videos.list` metadata; confirm fields in spike |
| Category chips Music / Live / Tutorials (2) | **Partly** | Map to search queries or categories; "AI" and "Mixes" are YouTube-internal and not available |
| Trending-style first page (2) | **Partly** | `mostPopular` shelf by region. This is **not** personalised recommendations |
| Personalised recommendations / "Up next" (2, 3) | **No** | Related-videos search removed; `home` feed not a supported personalised feed |
| Subscriptions feed, YouTube watch history (2) | **Deferred** | Would need Google sign-in with sensitive scopes; not in v1 |
| Bell / notifications (2) | **No** | No API; button omitted |
| Tap a card → plays (3) | **Yes** | IFrame embedded player in WebView; ads, controls and appearance controlled by YouTube; some videos disallow embedding → show Open-in-YouTube |
| Subscribe, like/dislike (3) | **Deferred** | OAuth write calls; v1 shows "Open in YouTube" on the channel/video instead |
| Comments card (3) | **Deferred** | Not in v1; Open-in-YouTube |
| Share (3) | **Yes** | System share sheet with the public youtube.com URL |
| Download button, 144p–1080p / MP3 sheet on YouTube video (3, 4) | **No** | Prohibited; see provider-boundaries.md |
| Google sign-in | **Deferred** | Never a custom form or WebView login; if added later, the official Android authorization flow only |

Overall: the **layout and the search-and-play loop** can look close to screen 2 and 3. The **content ranking, personalisation, account features and notifications cannot be reproduced**. Expect "YouTube search plus a trending shelf plus embedded playback", not YouTube's home feed.

### 3.3 Obligations and risks of using the Data API

| Item | Note |
|---|---|
| API Services Terms and policies | Treat as applying even to a personal sideloaded app. Disclosure of API use, a link to YouTube's Terms of Service and a privacy policy are required (provider-boundaries.md) |
| "Not mistakable for YouTube" and "independent value" | Policy guide warns against apps that replicate YouTube by aggregating embeds. Mitigation: clear YouPlayerTube branding, a "Powered by YouTube" style disclosure, native local/authorized features as the real value, minimal YouTube surface. A residual risk remains; not legal advice |
| Embed rules | Player never covered by our UI; at least 200×200 px (480×270 recommended); no ad or control interference; open links in the YouTube app or system browser |
| API key | Keep out of Git (local.properties/CI secret, restricted by package name and signing certificate). A key inside an APK can still be extracted, so restrict it and expect limits. Key restriction steps are not verified in this phase |
| Quota exhaustion | Show "Search limit reached for today" and Open-in-YouTube; cache nothing from YouTube beyond what policy allows (verify in spike) |
| Embedding disabled by owner | Filter results to embeddable videos if supported; otherwise show the honest fallback card |
| WebView identity | Test error 153 and Media Integrity behaviour in the Prompt 06 spike |
| GitHub sharing | No key, no keystore, no personal data in the repo. Public source with the "YouPlayerTube" name and logo that resembles YouTube's mark raises trademark risk even for personal use; add a non-affiliation notice (not legal advice) |

### 3.4 Recommendation

Run the Prompt 06 spike as **A + B + Data API read-only**:
1. Home: app-owned search field and results list plus a trending shelf, backed by an API key (public data only).
2. Playback: IFrame embed for the selected video; **Open in YouTube** beside every item and on every error.
3. No sign-in, Subscribe, like or comments in v1.
4. Native controls never attach to the embed.

README honesty line: "Home searches YouTube and plays videos in YouTube's own embedded player; downloads, PiP and background play are for your own or authorized media only."

Spike stop conditions: embedded playback fails on your Samsung (error 153 or similar and not fixable with documented steps), or quota/policy review shows you cannot proceed without an audit. Then fall back to handoff-only Home (Option A).

## 4. Phase 02 go / no-go criteria

| # | Criterion | Status 2026-11-07 |
|---|---|---|
| 1 | You approve this recommendation (A + B + Data API read-only) | **Met: APPROVED 2026-11-07** |
| 2 | Mockup and logo supplied | **Met.** Transparent/vector logo to be prepared in Prompt 05 (your decision) |
| 3 | Package `com.youplayertube.app` | **Met** |
| 4 | Windows PC and Samsung details | **Deferred by you.** Needed at the start of Prompt 02 (Windows version, RAM, installed tools) and before device tests (Samsung model, Android version, processor, RAM) |
| 5 | Q1 answered | **Met** (design above APPROVED 2026-11-07; Data API details marked UNVERIFIED in section 3.1 and Q11 remain to be verified in the Prompt 06 spike) |
| 6 | Visual matrix reviewed | **Met: approved with the original ten-screen design 2026-11-07** (D1, D3, D4, D6 decided; D2, D5, D7–D13 stand as written) |
| 7 | Branding/distribution answered | **Met** (Q3 personal sideload, possible GitHub; Q8 approved) |

No-go if: criterion 1 is not met, or you require YouTube download, ad removal or background play.

## 5. Decisions log

| ID | Decision | Date |
|---|---|---|
| Q1 | Prefer YouTube-focused Home without address bar; evaluated Data API + embed + fallback | 2026-11-07 |
| Q3 | Personal sideload on Samsung; Google Play not required; GitHub source sharing possible | 2026-11-07 |
| Q4 | Samsung and Windows; exact specs supplied later | 2026-11-07 |
| Q7 | Use supplied logo as reference; transparent/vector version prepared at splash phase | 2026-11-07 |
| Q8 | Wording adjusted to separate native features from YouTube restrictions; name stays YouPlayerTube | 2026-11-07 |
| Q9 | Keep Privacy & Security row | 2026-11-07 |
| Q10 | Package `com.youplayertube.app`; Hilt only if justified by architecture and verified dependency compatibility (decided in Prompt 03; manual injection is the default) | 2026-11-07 |

## 6. Open questions

| ID | Question | Owner |
|---|---|---|
| Q5 | Verify Media Integrity API steps and Android OAuth flow | Claude, Prompt 06 |
| Q6 | Verify PiP Android 12+ auto-enter and custom-action limits | Claude, Prompt 08 |
| Q11 | Confirm per-method quota costs, `videoEmbeddable` filter, API-key restriction steps and category chip mapping | Claude, Prompt 06 spike |
| Q12 | Windows version/RAM and Samsung model/Android version/processor/RAM | You, at Prompt 02 start |
