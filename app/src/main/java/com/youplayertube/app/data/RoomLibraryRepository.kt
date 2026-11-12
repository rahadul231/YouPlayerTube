package com.youplayertube.app.data

import com.youplayertube.app.data.local.*
import androidx.room.withTransaction
import com.youplayertube.app.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*

class RoomLibraryRepository(private val database: () -> YouPlayerTubeDatabase) : LibraryRepository, CollectionRepository {
    override fun observe(query: LibraryQuery): Flow<LoadState<LibraryData>> = flow {
        val dao = database().library()
        emitAll(combine(dao.observeSummary(), dao.observeItems(query.text, query.sort.name, query.limit)) { counts, rows ->
            // Validate each row before presenting metadata. Never coerce corrupt sources.
            val items = rows.map { it.toDomain() }
            LoadState.Ready(LibraryData(LibrarySnapshot(counts.videos, counts.music, counts.playlists,
                counts.favorites, counts.watchLater, counts.history), items)) as LoadState<LibraryData>
        })
    }.onStart { emit(LoadState.Loading) }.catch { cause ->
        if (cause is CancellationException) throw cause
        emit(LoadState.Failed("Library could not be loaded. Your stored data has not been reset."))
    }
    override suspend fun save(record: MediaRecord) = write {
        val db = database()
        db.withTransaction {
            val entity = record.toEntity()
            val existing = db.library().find(record.id)
            require(existing == null || existing.sourceKey == entity.sourceKey) { "A source identity cannot be reclassified" }
            db.library().save(entity)
        }
    }
    override suspend fun removeMetadata(id: String) = write { database().library().remove(id) }
    override suspend fun savePlaylist(playlist: Playlist) = write {
        database().library().savePlaylist(PlaylistEntity(playlist.id, playlist.name, playlist.createdAt))
    }
    override suspend fun addToPlaylist(playlistId: String, mediaId: String) = write {
        val dao = database().library()
        database().withTransaction {
            val current = dao.entries(playlistId)
            if (current.none { it.mediaId == mediaId }) dao.addEntries(listOf(PlaylistEntryEntity(playlistId, mediaId, current.size)))
        }
    }
    override suspend fun reorderPlaylist(playlistId: String, mediaIds: List<String>) = write { database().library().reorder(playlistId, mediaIds) }
    override suspend fun removePlaylist(playlistId: String) = write { database().library().removePlaylist(playlistId) }
    override suspend fun favorite(mediaId: String, addedAt: Long) = write {
        require(addedAt >= 0); database().library().favorite(FavoriteEntity(mediaId, addedAt))
    }
    override suspend fun watchLater(mediaId: String, addedAt: Long) = write {
        require(addedAt >= 0); database().library().watchLater(WatchLaterEntity(mediaId, addedAt))
    }
    override suspend fun recordHistory(event: HistoryEvent) = write {
        val source = requireNotNull(database().library().find(event.mediaId)).toDomain().source
        require(source !is YouTubeReference || event.positionMs == null)
        database().library().history(HistoryEntity(event.id, event.mediaId, event.watchedAt, event.positionMs))
    }
    override suspend fun clearHistory() = write { database().library().clearHistory() }
    private suspend fun write(block: suspend () -> Unit): WriteResult = try {
        block(); WriteResult.Saved
    } catch (cause: Exception) {
        if (cause is CancellationException) throw cause
        WriteResult.Failed("The change could not be saved. Stored media files were not deleted.")
    }
}
