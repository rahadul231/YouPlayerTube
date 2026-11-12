# YouPlayerTube architecture — Phase 03.2

Status: IMPLEMENTED; executed verification and blockers are recorded in progress.md and test-evidence/phase-03-2/report.md.

## Application and state

The existing :app module/package and manual injection remain. YouPlayerTubeApplication owns one lazy AppContainer using application Context. Its Room database and Preferences DataStore are shared for the process. MainActivity creates the saved-state-aware AppViewModel; Compose observes lifecycle-aware StateFlow.

Domain models contain no Android/Room annotations. data/local contains six Room v1 entities, DAOs, schema and source mapping. RoomLibraryRepository implements reactive library reads and suspend metadata/collection writes. DataStorePreferencesRepository stores the genuine default Library sort preference. UI distinguishes Loading, Ready and Failed; errors never become fake zero counts or a fallback empty database. Explicit Retry restarts observation.

Fresh launch opens Home. Existing-task restoration uses SavedStateHandle for tab selection and Compose SaveableStateHolder for per-tab scroll. Persistent sort comes from DataStore, never determines which tab opens. Database contents/preferences survive normal process/relaunch/package replacement when Android retains app data. Saved task restoration, recreation and force-stop are separately tested; none promises survival of clear-data/uninstall.

## Database v1

media_items stores source-tagged metadata and a unique source identity. playlists and ordered playlist_entries use FKs and transactions; favorites/watch_later have unique media keys; history_entries holds app-local events. Indices support lookups, sort and relationships. @Upsert preserves relationships; replacing source identity on an existing media ID is rejected. Reorder validates complete membership before atomic replacement. History clear/removing metadata never deletes media bytes. No import, stream retrieval, playback or download execution exists.

Native offline video/music counts include local records only; remote/YouTube links are not counted as downloaded files. Query results are bounded (100 UI default, maximum 500), literal substring filtering and deterministic ID tie-breakers. Metadata files/sizes are not a disk-usage measurement. Collections have repository/data foundations, not full editor screens.

YouTubeReference stores only its validated video ID and app-local title/link: no cached provider media metadata, stream URI or bytes. LocalUri requires a content URI with authority/path. Remote persistence requires an explicit durable-locator confirmation and rights provenance; query/fragment URLs are rejected to avoid storing signed URLs/query credentials. This is not a universal secret detector: authorized adapters must never mark token-bearing paths durable. Future native network operations must revalidate rights, redirects and destinations.

Write and read mapping reject inconsistent source payloads. NativePlayback accepts NativeSource; AuthorizedDownloader accepts AuthorizedRemote; no YouTube-to-native conversion exists. A media ID cannot be reassigned to a different source identity.

## Data safety and backup

Database v1 is the first database; no fictional v0 migration. app/schemas exports schema v1 for future migration tests. No destructive-migration fallback. PreservingOpenHelperFactory overrides the framework's default corruption-deletion callback and disables data-loss recovery. Unexpected schema/corruption errors surface without deleting stored data. DataStore has no replacement corruption handler: corrupt bytes/errors remain visible and preserved. Invalid sort enum values use a typed default; I/O/corruption does not silently reset the file.

Manifest allowBackup=false is unchanged. No broad storage permissions, service, analytics or ad SDK added. Signing configuration is unchanged; installed APK compatibility is a separate gate documented in signing.md.

## Dependencies and validation

Gate selected Room 2.8.5 Android-only stable line, KSP 2.3.13, Preferences DataStore 1.3.0 and coroutine testing 1.10.2 with the existing JDK 17/Gradle 9.6.0/AGP 9.5.0/Kotlin 2.4.10 toolchain. A temporary KSP-generated database compiled and all 17 original JVM tests passed before expansion. The probe is removed from the final app.

JVM tests exercise domain mapping, navigation, ViewModel failures/preferences and real on-disk DataStore reopen/corruption. Actions adds API 28 and API 36 emulator tests for real Room, UI, controlled same-key upgrades and process termination/restoration. A same-key rebuilt Phase 03.1 baseline is distinct from the owner's installed build #9 signer. API 37 runtime testing, OEM behaviour, exact branding/assets and owner-device acceptance remain separate checks. See the report for actual execution, rather than treating test compilation as passing.
