package com.youplayertube.app.data.local

import com.youplayertube.app.domain.*

fun MediaRecord.toEntity(): MediaEntity {
    val type = when (source) { is LocalUri -> "LOCAL"; is AuthorizedRemote -> "REMOTE"; is YouTubeReference -> "YOUTUBE" }
    val locator = when (source) { is LocalUri -> source.uri; is AuthorizedRemote -> source.url; is YouTubeReference -> source.videoId.value }
    return MediaEntity(id, type, "$type:$locator", (source as? LocalUri)?.uri,
        (source as? AuthorizedRemote)?.url, (source as? YouTubeReference)?.videoId?.value,
        (source as? AuthorizedRemote)?.rights?.name, title, kind.name, createdAt, updatedAt,
        mime, durationMs, sizeBytes, remotePersistenceConfirmed, rightsNote)
}
fun MediaEntity.toDomain(): MediaRecord {
    val source = when (sourceType) {
        "LOCAL" -> { require(remoteUrl == null && youtubeId == null && rights == null); LocalUri(requireNotNull(localUri)) }
        "REMOTE" -> { require(localUri == null && youtubeId == null); AuthorizedRemote(requireNotNull(remoteUrl), DownloadRights.valueOf(requireNotNull(rights))) }
        "YOUTUBE" -> { require(localUri == null && remoteUrl == null && rights == null); YouTubeReference(YouTubeVideoId(requireNotNull(youtubeId))) }
        else -> error("Unknown source type")
    }
    val record = MediaRecord(id, source, title, MediaKind.valueOf(kind), createdAt, updatedAt, mime,
        durationMs, sizeBytes, remotePersistenceConfirmed, rightsNote)
    require(record.toEntity().sourceKey == sourceKey) { "Source identity mismatch" }
    return record
}
