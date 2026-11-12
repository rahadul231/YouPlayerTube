package com.youplayertube.app
import com.youplayertube.app.domain.*
import org.junit.Assert.*
import org.junit.Test

class SourceBoundaryTest {
    @Test fun youtubeIsNotNativeAndCannotDownload() {
        val source: MediaSource = YouTubeReference(YouTubeVideoId("abcdefghijk"))
        assertFalse(source is NativeSource)
        assertTrue(downloadEligibility(source) is DownloadEligibility.Denied)
    }
    @Test fun localFilesAreNativeButAlreadyLocal() {
        val source = LocalUri("content://media/external/video/1")
        assertTrue(downloadEligibility(source) is DownloadEligibility.Denied)
    }
    @Test fun remoteNeedsExplicitDownloadRights() {
        DownloadRights.entries.forEach { rights ->
            assertEquals(rights == DownloadRights.DOWNLOAD_ALLOWED,
                downloadEligibility(AuthorizedRemote("https://media.example.org/video.mp4", rights)) == DownloadEligibility.Allowed)
        }
    }
    @Test fun rejectsProviderHostsIncludingSubdomainsAndTrailingDot() {
        listOf("youtube.com", "www.youtube.com", "youtu.be", "youtube-nocookie.com", "r1.googlevideo.com", "YOUTUBE.COM.").forEach {
            assertThrows(IllegalArgumentException::class.java) { AuthorizedRemote("https://$it/video", DownloadRights.DOWNLOAD_ALLOWED) }
        }
    }
    @Test fun rejectsUnsafeRemoteAddresses() {
        listOf("http://example.org/a", "file:///tmp/a", "https://user:pass@example.org/a", "https:///a").forEach {
            assertThrows(IllegalArgumentException::class.java) { AuthorizedRemote(it, DownloadRights.DOWNLOAD_ALLOWED) }
        }
    }
    @Test fun requiresSafUriAndValidVideoId() {
        assertThrows(IllegalArgumentException::class.java) { LocalUri("file:///sdcard/a.mp4") }
        assertThrows(IllegalArgumentException::class.java) { YouTubeVideoId("https://youtu.be/abcdefghijk") }
    }
    @Test fun similarHostIsNotYouTube() {
        assertEquals(DownloadEligibility.Allowed, downloadEligibility(AuthorizedRemote("https://youtube.com.example.org/a", DownloadRights.DOWNLOAD_ALLOWED)))
    }
}
