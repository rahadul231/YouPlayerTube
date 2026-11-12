package com.youplayertube.app.domain

import kotlinx.coroutines.flow.Flow

interface YouTubeGateway { suspend fun search(query: String): List<YouTubeReference> }
interface NativePlayback { fun open(source: NativeSource) }
interface AuthorizedDownloader { suspend fun enqueue(source: AuthorizedRemote): DownloadEligibility }
interface LibraryRepository {
    fun observe(query: LibraryQuery): Flow<LoadState<LibraryData>>
    suspend fun save(record: MediaRecord): WriteResult
    suspend fun removeMetadata(id: String): WriteResult
}
interface PreferencesRepository {
    val preferences: Flow<AppPreferences>
    suspend fun setLibrarySort(sort: LibrarySort)
}

interface CollectionRepository {
    suspend fun savePlaylist(playlist: Playlist): WriteResult
    suspend fun addToPlaylist(playlistId: String, mediaId: String): WriteResult
    suspend fun reorderPlaylist(playlistId: String, mediaIds: List<String>): WriteResult
    suspend fun removePlaylist(playlistId: String): WriteResult
    suspend fun favorite(mediaId: String, addedAt: Long): WriteResult
    suspend fun watchLater(mediaId: String, addedAt: Long): WriteResult
    suspend fun recordHistory(event: HistoryEvent): WriteResult
    suspend fun clearHistory(): WriteResult
}
