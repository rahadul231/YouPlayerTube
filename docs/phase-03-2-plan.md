# Phase 03.2 — Persistent state and library data foundation

Status: APPROVED for implementation by the user on 2026-11-13 (Asia/Dhaka). This file retains the approved design record. Actual implementation, executed tests and remaining signing/physical-device blockers are authoritative in progress.md and test-evidence/phase-03-2/report.md.

## 1. Baseline and scope

Build from main `65dd9a3f54dcd1d751703c51af046bea10d33825`, merged PR #1. Actions [#9](https://github.com/rahadul231/YouPlayerTube/actions/runs/43302349235) passed with 17 executed JVM tests. User confirms the Phase 03.1 APK on SM-J415F/API 28: four tabs, Back, scroll and rotation passed. These manual results do not establish automated device execution or process-death persistence.

Preserve package `com.youplayertube.app`, minSdk 26 (including Android 9/API 28), provisional compile/target SDK 37, four tabs, initial Home destination, approved black/red design, all 58 YPT requirements and all written ten-screen decisions. Original visual PDF/logo remain missing; exact visual fidelity stays BLOCKED, but data-layer work can proceed.

Deliver a real persistence foundation: Room database version 1, reactive media repository, persisted small preferences, explicit loading/error states, and restoration validation. This advances YPT-007/018/019/022/026/031/032/033/047/050 foundations; it does not mark their entire feature sets complete.

Out of scope: SAF import UI, playback, PiP/background audio, download execution, YouTube search/embed/network calls, API credentials, OAuth, full playlist/favorite/history editing UI, transcoding, new branding, or release signing. No demo media, fake counts or sample records enter the production database. Tests may use clearly isolated fixtures.

## 2. State ownership and startup rules

| State | Owner/storage | Restoration promise |
|---|---|---|
| Media metadata and app-local collection relationships | Room | Survives process death, normal relaunch and device reboot until explicitly removed/clear-data/uninstall |
| Default library sort (name/date/type) and small future preferences | Preferences DataStore | Persisted atomically; observe through repository Flow |
| Selected tab, temporary search/filter and future route IDs | SavedStateHandle | Restores a supported existing task after Android saves/recreates it; not a permanent task history |
| Per-tab scroll position | Compose SaveableStateHolder | Restores where supported; clamp gracefully when the underlying list changes |
| Loading, errors, active jobs | ViewModel/repository | Recomputed after restart; never serialize a job, Context, database or large list into a Bundle |

Fresh launch without a saved task opens Home (YPT-002). Restoring an existing task may restore its selected tab (YPT-007). Do not persist last-tab as an unconditional startup preference. Force-stop/fresh launch and OS-restored task are different test cases. Room/DataStore contents survive force-stop, but navigation need not.

On launch, show explicit loading until durable data is available; do not flash invented zero counts on a load failure. Missing/corrupt preferences and database errors receive distinct handling. Preference corruption may reset preferences with an honest notice; an unexpected I/O failure must not silently overwrite stored preferences. Database errors never trigger automatic destructive recreation. The app remains navigable with a retry/error state.

## 3. Architecture and contracts

Retain one :app module, manual AppContainer injection, existing Compose UI and MVVM. Move shared database/preferences instances to an application-scoped container; Activities receive dependencies rather than opening databases themselves. Use application Context only, one database and one DataStore per file/process. No Hilt or Koin.

Flow of data: DAO/DataStore -> repository -> ViewModel StateFlow -> lifecycle-aware Compose. Writes are suspend operations returning typed success/failure; database I/O never runs on the main thread. Inject dispatchers/clock/ID generation where meaningful for deterministic tests. Room entities remain internal to data; domain models contain no Room annotations or Android Context.

Replace synchronous LibraryRepository.snapshot() with observable summaries/query results, e.g. observeSummary() and observeItems(query), plus narrowly defined suspend metadata mutations. Distinguish loading/ready/failed state in UI. Domain queries define sort, media category and optional text filter with stable ordering/tie-breakers. Avoid an unbounded all-media list in AppUiState. Paging is deferred until demonstrated scale requires it; v1 queries should be bounded and indexed.

Provide tested repository operations for metadata insert/update/remove and collection relationships. These operations do not import/read media bytes or delete media files. Playlist operations use transactions for membership/order; history clear affects history only. Repository-level operations for collection data are foundations, not a promise of full user-facing collection features in this phase.

## 4. Proposed library models and Room v1 schema

| Domain/table | Proposed fields and invariants |
|---|---|
| MediaRecord / media_items | Stable app ID, source discriminator, title, media kind (video/audio/unknown), optional MIME/duration/size, created/updated timestamps; absent metadata remains null, never fabricated |
| Source payload | LocalUri: content URI only; AuthorizedRemote: validated HTTPS URL plus rights state/provenance; YouTubeReference: video ID only, never stream/content URI. Discriminator and nullable fields validated on write AND read |
| Playlist / playlists | Stable ID, validated name, timestamps; no production demo playlists |
| PlaylistEntry / playlist_entries | Playlist ID + media ID + position; composite uniqueness, FK relationships and deterministic ordering; atomic reorder |
| Favorite / favorites | Unique media ID plus timestamp |
| WatchLater / watch_later | Unique media ID plus timestamp; app-local, not YouTube account synchronization |
| HistoryEntry / history_entries | Event ID, media ID, timestamp, optional native playback position; history clear does not remove media rows/files |

Use indices for source lookup, media type/title/date and relationship keys. Decide duplicate-source normalization before final schema export; do not strip URL query parameters that can carry authorization. Do not persist credentials, cookies, bearer tokens or signed remote URLs by default: require a safe persistent locator or keep expiring URLs in memory and later resolve via an authorized adapter. Rights metadata is not proof of permanent permission and must be revalidated before future playback/download use.

Prefer one typed media table with a source converter/mapper and explicit validation, rather than persisting Kotlin class names. Reject inconsistent rows as a typed data error; never coerce a YouTube row into a native source. Strengthen URI/host validation as necessary for serialization round trips. Future redirect/destination checks remain required when network functionality is introduced.

Schema export is mandatory and checked into app/schemas/. No destructive-migration fallback. Database v1 is the first on-device database, so Phase 03.1 -> 03.2 is database creation, not an invented v0 -> v1 migration. Test schema identity/creation now; add MigrationTestHelper coverage for the first real v1 -> v2 change. Use transactions, FKs and schema review to avoid premature empty tables for future downloads/player sessions.

YouTube references are app-local links only. No YouTube audiovisual bytes or extracted stream URLs. Provider-fetched descriptive metadata is not needed in this phase; any future metadata caching must implement the applicable refresh/deletion policy before it is enabled. Native video/music counts include local/eligible offline media records, not YouTube links or remote links masquerading as downloaded files. Stored size is metadata, not a claim that storage usage was measured from files.

## 5. Preferences and visible integration

Add AppPreferences/PreferencesRepository with a Flow of typed defaults and suspend updates. Begin with default library sort; use a small accessible control on Library, preserving the tab and visual structure. The selection genuinely persists and drives repository ordering; an empty library remains empty. No theme switch, quality control or download setting claims to work before its underlying feature exists.

Replace EmptyLibraryRepository in production with RoomLibraryRepository. Library summaries update from database Flow. Existing Home/Downloads remain honest planned/empty surfaces. Keep all seven Settings sections; do not add a debugging/fixture editor to production UI. Full collection editors and SAF import remain later work. Test-only fixture insertion verifies persisted data without shipping sample media or exposing internal controls.

## 6. Proposed file changes (only after approval)

| Files/packages | Planned purpose |
|---|---|
| domain/LibraryModels.kt, LibraryQuery.kt, AppPreferences.kt | Typed IDs/metadata/query/default preference models |
| domain/Repositories.kt | Reactive LibraryRepository and PreferencesRepository; preserve YouTube/native platform boundaries |
| data/local/YouPlayerTubeDatabase.kt, entity/*, dao/*, mapper/* | Room v1 schema, DAOs, transactions, validated source mapping |
| data/RoomLibraryRepository.kt | Async data access, count/query mapping, typed errors |
| data/preferences/DataStorePreferencesRepository.kt | Single DataStore, typed defaults and durable preference updates |
| AppContainer.kt, new YouPlayerTubeApplication.kt, AndroidManifest.xml, MainActivity.kt | Application-scoped dependency lifecycle, Activity injection |
| ui/AppViewModel.kt, ui/YouPlayerTubeApp.kt (extract Library UI if useful) | Loading/error/data state and accessible persistent sort control |
| gradle/libs.versions.toml, app/build.gradle.kts, root build.gradle.kts as needed | Room/KSP/DataStore/test configuration only |
| app/schemas/* | Exported database v1 schema |
| src/test/*, src/androidTest/* | Domain/mapper/repository/preferences/ViewModel/Room/restoration tests |
| .github/workflows/android.yml or separate device-test workflow | Retain wrapper/nonzero-test/report gates; add device job where supported |
| docs/progress.md, versions.md, architecture.md, test-evidence/* | Actual status, compatibility pins, results, limitations and evidence |

No new Gradle module, backend, analytics, advertising, service or broad-storage permission is proposed.

## 7. Dependencies and compatibility gate

Keep JDK 17, wrapper 9.6.0, AGP 9.5.0, Kotlin/Compose compiler 2.4.10 and current Compose/lifecycle pins unless a concrete incompatibility is demonstrated. Proposed additions: stable Room runtime/compiler/testing artifacts, compatible KSP plugin, Preferences DataStore and kotlinx-coroutines-test (plus explicit coroutine runtime only if needed).

Do not copy dependency coordinates from an old playbook: current official Room documentation describes Room 3 and the androidx.room3 namespace. At implementation start, compare supported stable Room lines, minimum SDK/JDK, Android-only API requirements and KSP compatibility with this exact project. Select one line/namespace, pin versions in the catalog, compile generated DAOs and run the compatibility build before expanding the schema. Do not introduce Kotlin Multiplatform/another module solely for testing, or silently upgrade Kotlin/AGP to make a dependency fit. If no suitable stable combination works, mark BLOCKED and report the concrete conflict.

All versions remain PLANNED until a real build/test proves integration. Existing Android 9 behaviour must not regress; keep minSdk 26 and review any newly used API above API 28. Modern Android validation includes current targetSdk-specific behaviour; compiling for SDK 37 does not establish runtime correctness.

## 8. Implementation order and review gates

1. Compatibility check: pin minimum new dependencies, establish KSP generation, compile and run existing 17 tests. Stop on unexplained incompatibility.
2. Domain contracts: immutable metadata/source/query models, mapping/eligibility invariants and JVM tests.
3. Room v1: entities, DAOs, indices/FKs, transactional collections, exported schema; real database round-trip/constraint/reopen tests.
4. Preferences: singleton DataStore, defaults, durable sort, error handling and on-disk reopen tests.
5. Integration: application container, reactive repository/ViewModel, library loading/ready/error states and sort control; retain all navigation tests.
6. Restoration and performance: activity recreation, saved task/process restoration, force-stop/relaunch durability and regression tests on API 28 plus a modern runtime.
7. Evidence and delivery: update authoritative progress by requirement/area, retain reports, build review APK, open PR to main. No automatic merge; user Samsung acceptance is recorded separately.

Use small commits on a new development branch after plan approval. Documentation-only planning PR does not authorize application implementation. New tests must execute, not merely compile or report NO-SOURCE.

## 9. Tests and acceptance criteria

| Area | Required evidence / acceptance |
|---|---|
| Regression | Existing navigation/source tests still execute and pass; tabs/Back/scroll/rotation preserved |
| Source mapping | All three source classes round-trip; malformed/inconsistent/provider-to-native payloads rejected; no YouTube download eligibility |
| Real Room | Insert/read/update/delete metadata; FK/unique constraints, transactional membership/reorder, duplicate handling, summary correctness, close/reopen durability; fixtures only in isolated test databases |
| Schema | Version 1 exported and reviewed; no destructive fallback; migration coverage explicitly deferred until a real migration exists |
| DataStore | Defaults, update/reopen durability, concurrent updates, invalid enum/default recovery and I/O/corruption handling; no silent data overwrite |
| ViewModel | Reactive changes, loading/error/retry, cancellation/lifecycle collection, preference sorting and stable ordering |
| Restoration | Recreation and saved-state restoration separate from actual background process kill; saved task restores tab and reloads DB; fresh/force-stop launch opens Home while durable preferences/data survive |
| Device compatibility | API 28 device/emulator plus modern Android runtime; no main-thread DB I/O, ANR, crash or broad-storage prompt |
| Privacy/deletion | Clear history removes history only; metadata deletion never deletes media bytes; no credentials/YouTube streams written to DB |
| Build/CI | Wrapper debug/test APK assembly, lint, nonzero JVM test execution and retained reports; real Room/device results explicitly identified |
| User acceptance | New APK tested by user on SM-J415F; report install identity and exact checks; do not carry Phase 03.1 results forward as new evidence |

Run domain/mapper/ViewModel/DataStore tests on Codex Cloud and Actions. For Android Room, prefer real Android instrumentation (in-memory tests plus on-disk reopen tests); a fake repository does not prove SQLite behaviour. Plan a separate Actions emulator job for API 28 and a modern API, checking runner KVM and image availability before claiming support. API 37 runtime coverage is required to establish target-runtime behaviour; if its image is unavailable, use an available modern runtime and record the API 37 gap. Keep local laptop free of builds/emulators. Android test APKs that only compile remain IMPLEMENTED/UNTESTED.

If device CI cannot run, retain its concrete blocker and use a separately recorded physical-device test run; do not mark Room/restoration validation complete merely because JVM tests pass. A JVM Room backend may supplement testing if supported by the selected Room line without restructuring the application, but does not replace Android validation. Do not add Robolectric solely as a shortcut around unavailable device infrastructure.

For actual process death, background the task, terminate its process (not force-stop), restore from Recents, and inspect state. Treat ActivityScenario.recreate(), Don't keep activities, force-stop and process death as different evidence. Fresh install/clear-data starts empty; ordinary reboot/relaunch retains durable records/preferences. Track startup regression against Phase 03.1 measured baseline once available rather than claiming an unmeasured performance target.

## 10. Risks and completion definition

KSP/Room compatibility is the first gate. Other risks: schema churn, source/rights confusion, revoked future URI grants, stale metadata, signed-URL persistence, startup races, disk-full/corrupt data, and memory pressure on the 2 GB Samsung. Mitigate with bounded queries, background I/O, transactions, explicit errors, typed adapters, minimal metadata and real device tests. Do not hide persistence errors by silently switching back to EmptyLibraryRepository.

Complete only when persistence is IMPLEMENTED, meaningful tests including real database validation are TESTED with reports, required database/restoration gaps are resolved, and a review APK/PR is available. If required validation remains BLOCKED, deliver a clearly incomplete review milestone and do not claim Phase 03.2 complete. Feature-level YPT status advances only for delivered acceptance criteria. Public branding, YouTube policy integration and signed release acceptance remain separate gates.

## Official references checked for planning
- [Room storage/setup](https://developer.android.com/training/data-storage/room)
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- [Database testing](https://developer.android.com/training/data-storage/room/testing-db)
- [Saving UI state](https://developer.android.com/topic/libraries/architecture/saving-states)
