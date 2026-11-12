package com.youplayertube.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LibraryDao {
    @Query("SELECT * FROM media_items WHERE instr(lower(title), lower(:text)) > 0 ORDER BY CASE WHEN :sort = 'NAME' THEN lower(title) END ASC, CASE WHEN :sort = 'TYPE' THEN kind END ASC, CASE WHEN :sort = 'DATE' THEN createdAt END DESC, id ASC LIMIT :limit")
    abstract fun observeItems(text: String, sort: String, limit: Int): Flow<List<MediaEntity>>
    @Query("SELECT * FROM media_items WHERE id = :id") abstract suspend fun find(id: String): MediaEntity?
    @Query("SELECT * FROM media_items WHERE sourceKey = :key") abstract suspend fun findBySource(key: String): MediaEntity?
    @Query("SELECT * FROM media_items") abstract suspend fun all(): List<MediaEntity>
    @Upsert abstract suspend fun save(entity: MediaEntity)
    @Query("DELETE FROM media_items WHERE id = :id") abstract suspend fun remove(id: String)
    @Query("SELECT (SELECT count(*) FROM media_items WHERE sourceType='LOCAL' AND kind='VIDEO') AS videos, (SELECT count(*) FROM media_items WHERE sourceType='LOCAL' AND kind='AUDIO') AS music, (SELECT count(*) FROM playlists) AS playlists, (SELECT count(*) FROM favorites) AS favorites, (SELECT count(*) FROM watch_later) AS watchLater, (SELECT count(*) FROM history_entries) AS history")
    abstract fun observeSummary(): Flow<SummaryRow>
    @Upsert abstract suspend fun savePlaylist(entity: PlaylistEntity)
    @Query("DELETE FROM playlists WHERE id=:id") abstract suspend fun removePlaylist(id: String)
    @Insert abstract suspend fun addEntries(entries: List<PlaylistEntryEntity>)
    @Query("SELECT * FROM playlist_entries WHERE playlistId=:id ORDER BY position") abstract suspend fun entries(id: String): List<PlaylistEntryEntity>
    @Query("DELETE FROM playlist_entries WHERE playlistId=:id") abstract suspend fun clearEntries(id: String)
    @Upsert abstract suspend fun favorite(entity: FavoriteEntity)
    @Upsert abstract suspend fun watchLater(entity: WatchLaterEntity)
    @Insert abstract suspend fun history(entity: HistoryEntity)
    @Query("DELETE FROM history_entries") abstract suspend fun clearHistory()
    @Query("SELECT count(*) FROM history_entries") abstract suspend fun historyCount(): Int
    @Transaction open suspend fun reorder(id: String, orderedIds: List<String>) {
        require(orderedIds.distinct().size == orderedIds.size)
        require(entries(id).map { it.mediaId }.toSet() == orderedIds.toSet())
        clearEntries(id)
        addEntries(orderedIds.mapIndexed { index, mediaId -> PlaylistEntryEntity(id, mediaId, index) })
    }
}
data class SummaryRow(val videos: Int, val music: Int, val playlists: Int, val favorites: Int, val watchLater: Int, val history: Int)
