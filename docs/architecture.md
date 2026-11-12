# Phase 03.1 architecture

Status: IMPLEMENTED; validation evidence belongs in progress.md.

Existing :app module and package com.youplayertube.app are retained. Manual AppContainer injection avoids an unnecessary DI framework. MainActivity is the platform entry point; AppViewModel owns immutable AppUiState exposed as StateFlow. Compose collects with lifecycle awareness. SavedStateHandle stores the selected tab; a SaveableStateHolder preserves each tab's scroll state across supported Android saved-state recreation. This is not permanent storage and does not promise restoration after force-stop/clear-data.

## Navigation
Home is the initial destination. Home, Downloads, Library, Settings are the only current routes. Selecting a tab replaces the root, without growing history. Back from another tab returns Home; Back from Home delegates to Android. There are no invented child routes. Add independent child stacks and pop children before returning Home when later milestones implement them. NavigationState is pure Kotlin and tested on the JVM; a Compose instrumentation test covers actual clicks, recreation and back dispatch when a device is available.

## Boundaries
- domain/: MediaSource, NativeSource, YouTubeReference, eligibility and repository/platform contracts. No Android UI dependencies.
- data/: EmptyLibraryRepository, which returns genuine zero counts because import and persistence do not yet exist.
- ui/: lifecycle-aware MVVM state, navigation and Compose screens.
- MainActivity/AppContainer: Android entry and manual construction.

YouTubeReference has only a validated video ID and cannot be passed to NativePlayback.open(NativeSource) or AuthorizedDownloader.enqueue(AuthorizedRemote). LocalUri requires a SAF content URI. AuthorizedRemote requires HTTPS without credentials and rejects YouTube/provider media hosts. Explicit download rights are required by downloadEligibility; future download implementations MUST recheck rights, actual source capabilities, redirects and destination hosts at execution. These contracts do not constitute a complete network security or rights-verification implementation. There is no downloader, player, extractor or network client in this milestone.

## Dependencies
Existing AGP/Kotlin/Compose/SDK pins remain. Added lifecycle ViewModel/runtime Compose 2.11.0 for MVVM and lifecycle collection, JUnit 4.13.2, Compose UI testing via the existing BOM, and Android test runner 1.6.2. No navigation/DI/icon/network/database SDK is needed for four root screens. Versions are integration-verified by the recorded build, not assumed from availability.

## Visual and accessibility scope
Black surfaces/red accents follow the written approved defaults; final fidelity awaits the original PDF/logo. Original line icons are navigation symbols, not substitute branding. Material navigation items provide selection semantics and touch targets, paired with visible labels. Content scrolls at larger font scales and uses centered maximum-width layouts. No fake content, functioning-setting claims, demo progress, player or download controls are shown.

## Planned next steps
Persistent state/repository design and expanded restoration tests, then approved visual tokens/branding when originals are available. Room, DataStore, Media3, services, PiP, online API integration and downloads remain PLANNED. All 58 requirements and provider prohibitions are retained.
