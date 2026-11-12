package com.youplayertube.app
import com.youplayertube.app.domain.*
import com.youplayertube.app.data.local.*
import org.junit.Assert.*
import org.junit.Test

class MediaMappingTest {
    private fun record(source: MediaSource) = MediaRecord("id", source, "Title", MediaKind.VIDEO, 1,
        remotePersistenceConfirmed = source is AuthorizedRemote, rightsNote = if (source is AuthorizedRemote) "Own durable server locator" else null)
    @Test fun sourceRoundTrips() {
        listOf(LocalUri("content://media/video/1"), YouTubeReference(YouTubeVideoId("abcdefghijk")),
            AuthorizedRemote("https://media.example.org/owned.mp4", DownloadRights.DOWNLOAD_ALLOWED)).forEach { source ->
            val item=record(source); assertEquals(item,item.toEntity().toDomain())
        }
    }
    @Test fun youtubeCannotAcquireNativePayload() {
        val row=record(YouTubeReference(YouTubeVideoId("abcdefghijk"))).toEntity()
        assertThrows(IllegalArgumentException::class.java) { row.copy(remoteUrl="https://media.example.org/a").toDomain() }
    }
    @Test fun sourceIdentityMismatchFails() {
        val row=record(LocalUri("content://media/video/1")).toEntity()
        assertThrows(IllegalArgumentException::class.java) { row.copy(sourceKey="LOCAL:other").toDomain() }
    }
    @Test fun signedRemoteLocatorsAreNotPersisted() {
        assertThrows(IllegalArgumentException::class.java) { record(AuthorizedRemote("https://media.example.org/a?token=secret", DownloadRights.PLAYBACK_ONLY)) }
        assertThrows(IllegalArgumentException::class.java) { MediaRecord("id", AuthorizedRemote("https://media.example.org/a", DownloadRights.PLAYBACK_ONLY), "A", MediaKind.VIDEO, 1) }
    }
    @Test fun providerMetadataCachingIsNotEnabled() {
        assertThrows(IllegalArgumentException::class.java) { record(YouTubeReference(YouTubeVideoId("abcdefghijk"))).copy(sizeBytes=3) }
    }
    @Test fun invalidMetadataAndQueryBoundsRejected() {
        assertThrows(IllegalArgumentException::class.java) { record(LocalUri("content://media/video/1")).copy(durationMs=-1) }
        assertThrows(IllegalArgumentException::class.java) { LibraryQuery(limit=0) }
        assertThrows(IllegalArgumentException::class.java) { LocalUri("content:missing-authority") }
    }
}
