package com.youplayertube.app.domain

/** Provider references never inherit NativeSource and never contain stream URLs. */
sealed interface MediaSource
@JvmInline value class YouTubeVideoId(val value: String) {
    init { require(Regex("[A-Za-z0-9_-]{11}").matches(value)) }
}
data class YouTubeReference(val videoId: YouTubeVideoId) : MediaSource
sealed interface NativeSource : MediaSource
data class LocalUri(val uri: String) : NativeSource {
    init { require(java.net.URI(uri).scheme == "content") { "SAF content URI required" } }
}
enum class DownloadRights { NOT_CONFIRMED, PLAYBACK_ONLY, DOWNLOAD_ALLOWED }
/** Constructed only by an authorized-provider adapter, never by converting a YouTube reference. */
data class AuthorizedRemote(val url: String, val rights: DownloadRights) : NativeSource {
    init {
        val parsed = java.net.URI(url)
        require(parsed.scheme == "https" && !parsed.host.isNullOrBlank() && parsed.userInfo == null)
        val host = parsed.host.lowercase().trimEnd('.')
        require(listOf("youtube.com", "youtu.be", "youtube-nocookie.com", "googlevideo.com").none {
            host == it || host.endsWith(".$it")
        }) { "YouTube resources are provider references, not native media" }
    }
}
sealed interface DownloadEligibility {
    data object Allowed : DownloadEligibility
    data class Denied(val reason: String) : DownloadEligibility
}
fun downloadEligibility(source: MediaSource): DownloadEligibility = when (source) {
    is YouTubeReference -> DownloadEligibility.Denied("YouTube downloads are unavailable in this app.")
    is LocalUri -> DownloadEligibility.Denied("This media is already local.")
    is AuthorizedRemote -> if (source.rights == DownloadRights.DOWNLOAD_ALLOWED) DownloadEligibility.Allowed
        else DownloadEligibility.Denied("Download authorization has not been granted.")
}
