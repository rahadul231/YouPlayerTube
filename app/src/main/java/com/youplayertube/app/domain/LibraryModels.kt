package com.youplayertube.app.domain

import java.net.URI

enum class MediaKind { VIDEO, AUDIO, UNKNOWN }
enum class LibrarySort { NAME, DATE, TYPE }
data class LibraryQuery(val sort: LibrarySort = LibrarySort.DATE, val text: String = "", val limit: Int = 100) {
    init { require(limit in 1..500); require(text.length <= 200) }
}
data class MediaRecord(
    val id: String, val source: MediaSource, val title: String, val kind: MediaKind,
    val createdAt: Long, val updatedAt: Long = createdAt,
    val mime: String? = null, val durationMs: Long? = null, val sizeBytes: Long? = null,
    val remotePersistenceConfirmed: Boolean = false, val rightsNote: String? = null,
) {
    init {
        require(id.isNotBlank() && id.length <= 128)
        require(title.isNotBlank() && title.length <= 500)
        require(createdAt >= 0 && updatedAt >= createdAt)
        require(durationMs == null || durationMs >= 0); require(sizeBytes == null || sizeBytes >= 0)
        when (source) {
            is AuthorizedRemote -> {
                val uri = URI(source.url)
                require(remotePersistenceConfirmed && !rightsNote.isNullOrBlank()) { "A durable, authorized locator must be confirmed" }
                require(uri.rawQuery == null && uri.rawFragment == null) { "Do not persist signed URLs or query credentials" }
            }
            is YouTubeReference -> {
                require(mime == null && durationMs == null && sizeBytes == null) { "Provider metadata caching is not enabled" }
                require(!remotePersistenceConfirmed && rightsNote == null)
            }
            is LocalUri -> require(!remotePersistenceConfirmed && rightsNote == null)
        }
    }
}
data class LibrarySnapshot(val videos: Int = 0, val music: Int = 0, val playlists: Int = 0,
    val favorites: Int = 0, val watchLater: Int = 0, val history: Int = 0)
data class LibraryData(val summary: LibrarySnapshot, val items: List<MediaRecord>)
sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Ready<T>(val value: T) : LoadState<T>
    data class Failed(val message: String) : LoadState<Nothing>
}
sealed interface WriteResult {
    data object Saved : WriteResult
    data class Failed(val message: String) : WriteResult
}
data class AppPreferences(val librarySort: LibrarySort = LibrarySort.DATE)

data class Playlist(val id: String, val name: String, val createdAt: Long) {
    init { require(id.isNotBlank() && id.length <= 128); require(name.isNotBlank() && name.length <= 200); require(createdAt >= 0) }
}
data class HistoryEvent(val id: String, val mediaId: String, val watchedAt: Long, val positionMs: Long? = null) {
    init { require(id.isNotBlank() && mediaId.isNotBlank()); require(watchedAt >= 0); require(positionMs == null || positionMs >= 0) }
}
