package com.petediano.peusic.data.download

import java.net.URI
import java.util.Locale

object UrlValidator {

    private val BLOCKED_HOSTS = setOf(
        "youtube.com", "www.youtube.com", "m.youtube.com", "youtu.be",
        "youtube-nocookie.com", "music.youtube.com",
        "vimeo.com", "dailymotion.com", "twitch.tv",
        "spotify.com", "open.spotify.com", "deezer.com",
        "soundcloud.com", "tiktok.com", "instagram.com"
    )

    private val AUDIO_EXTENSIONS = setOf(
        "mp3", "m4a", "aac", "ogg", "oga", "opus", "flac", "wav", "wma", "aiff", "ape", "alac"
    )

    private val AUDIO_MIME_HINTS = listOf("audio/", "application/ogg", "application/x-flac")

    data class ValidationResult(
        val isValid: Boolean,
        val reason: String? = null,
        val normalizedUrl: String? = null,
        val suggestedFileName: String? = null
    )

    fun validate(rawUrl: String): ValidationResult {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) return ValidationResult(false, "URL is empty")

        val uri = try { URI(trimmed) } catch (_: Exception) {
            return ValidationResult(false, "Invalid URL syntax")
        }

        val scheme = uri.scheme?.lowercase(Locale.US)
        if (scheme != "https" && scheme != "http") {
            return ValidationResult(false, "Only HTTP and HTTPS URLs are supported. Prefer HTTPS.")
        }

        val host = uri.host?.lowercase(Locale.US)
            ?: return ValidationResult(false, "Missing host")

        if (host in BLOCKED_HOSTS || BLOCKED_HOSTS.any { host.endsWith(".$it") }) {
            return ValidationResult(
                false,
                "This source does not permit direct audio downloads. Open the official page instead."
            )
        }

        val path = uri.path ?: ""
        if (path.isBlank() || path == "/") {
            return ValidationResult(false, "URL does not point to a specific file")
        }

        val fileNameFromPath = path.substringAfterLast('/').substringBefore('?')
        val suggestedName = sanitizeFileName(
            if (fileNameFromPath.isNotBlank()) fileNameFromPath else "download_${System.currentTimeMillis()}"
        )

        return ValidationResult(
            isValid = true,
            normalizedUrl = uri.toASCIIString(),
            suggestedFileName = suggestedName
        )
    }

    fun sanitizeFileName(name: String): String {
        var clean = name
            .replace(Regex("""[\\/:*?\"<>|]"""), "_")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(180)
        if (clean.isBlank() || clean == "." || clean == "..") {
            clean = "audio_${System.currentTimeMillis()}"
        }
        if (!clean.contains('.')) clean += ".mp3"
        return clean
    }

    fun isLikelyAudioContentType(contentType: String?): Boolean {
        if (contentType.isNullOrBlank()) return false
        val lower = contentType.lowercase(Locale.US)
        return AUDIO_MIME_HINTS.any { lower.startsWith(it) } ||
            lower.contains("mpeg") || lower.contains("mp4") || lower.contains("ogg")
    }
}
