package com.youplayertube.app.domain

/** Contracts only: no network, playback or download implementation in Phase 03.1. */
interface YouTubeGateway { suspend fun search(query: String): List<YouTubeReference> }
interface NativePlayback { fun open(source: NativeSource) }
interface AuthorizedDownloader { suspend fun enqueue(source: AuthorizedRemote): DownloadEligibility }
interface LibraryRepository { fun snapshot(): LibrarySnapshot }
data class LibrarySnapshot(val videos: Int = 0, val music: Int = 0, val playlists: Int = 0,
    val favorites: Int = 0, val watchLater: Int = 0, val history: Int = 0)
