package com.petediano.peusic.domain.model

import android.net.Uri

data class Track(
    val id: Long,
    val contentUri: Uri,
    val title: String,
    val artist: String?,
    val album: String?,
    val albumId: Long?,
    val durationMs: Long,
    val trackNumber: Int? = null,
    val year: Int? = null,
    val mimeType: String? = null,
    val sizeBytes: Long = 0L,
    val dateAdded: Long = 0L,
    val folderPath: String? = null
) {
    val displayArtist: String get() = artist?.takeIf { it.isNotBlank() } ?: "Unknown Artist"
    val displayAlbum: String get() = album?.takeIf { it.isNotBlank() } ?: "Unknown Album"
    val durationFormatted: String
        get() {
            if (durationMs <= 0) return "--:--"
            val totalSec = durationMs / 1000
            val m = totalSec / 60
            val s = totalSec % 60
            return "%d:%02d".format(m, s)
        }
}

data class Album(
    val id: Long,
    val title: String,
    val artist: String?,
    val trackCount: Int,
    val year: Int? = null
)

data class Artist(
    val name: String,
    val trackCount: Int,
    val albumCount: Int
)

data class Folder(
    val path: String,
    val name: String,
    val trackCount: Int
)
