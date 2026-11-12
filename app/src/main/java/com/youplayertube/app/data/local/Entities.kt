package com.youplayertube.app.data.local

import androidx.room.*

@Entity(tableName = "media_items", indices = [Index(value = ["sourceKey"], unique = true), Index("kind"), Index("title"), Index("createdAt")])
data class MediaEntity(
    @PrimaryKey val id: String, val sourceType: String, val sourceKey: String,
    val localUri: String?, val remoteUrl: String?, val youtubeId: String?, val rights: String?,
    val title: String, val kind: String, val createdAt: Long, val updatedAt: Long,
    val mime: String?, val durationMs: Long?, val sizeBytes: Long?,
    val remotePersistenceConfirmed: Boolean, val rightsNote: String?,
)
@Entity(tableName = "playlists")
data class PlaylistEntity(@PrimaryKey val id: String, val name: String, val createdAt: Long)
@Entity(tableName = "playlist_entries", primaryKeys = ["playlistId", "mediaId"],
    foreignKeys = [ForeignKey(entity = PlaylistEntity::class, parentColumns = ["id"], childColumns = ["playlistId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MediaEntity::class, parentColumns = ["id"], childColumns = ["mediaId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("mediaId"), Index(value = ["playlistId", "position"], unique = true)])
data class PlaylistEntryEntity(val playlistId: String, val mediaId: String, val position: Int)
@Entity(tableName = "favorites", foreignKeys = [ForeignKey(entity = MediaEntity::class, parentColumns = ["id"], childColumns = ["mediaId"], onDelete = ForeignKey.CASCADE)])
data class FavoriteEntity(@PrimaryKey val mediaId: String, val addedAt: Long)
@Entity(tableName = "watch_later", foreignKeys = [ForeignKey(entity = MediaEntity::class, parentColumns = ["id"], childColumns = ["mediaId"], onDelete = ForeignKey.CASCADE)])
data class WatchLaterEntity(@PrimaryKey val mediaId: String, val addedAt: Long)
@Entity(tableName = "history_entries", foreignKeys = [ForeignKey(entity = MediaEntity::class, parentColumns = ["id"], childColumns = ["mediaId"], onDelete = ForeignKey.CASCADE)], indices = [Index("mediaId"), Index("watchedAt")])
data class HistoryEntity(@PrimaryKey val id: String, val mediaId: String, val watchedAt: Long, val positionMs: Long?)
