package com.petediano.peusic.data.library

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.petediano.peusic.domain.model.Album
import com.petediano.peusic.domain.model.Artist
import com.petediano.peusic.domain.model.Folder
import com.petediano.peusic.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreLibraryRepository(private val context: Context) {

    private val collection: Uri =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

    private val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ALBUM_ID,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK,
        MediaStore.Audio.Media.YEAR,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.DATE_ADDED,
        MediaStore.Audio.Media.DATA
    )

    suspend fun getAllTracks(sortBy: String = MediaStore.Audio.Media.TITLE): List<Track> =
        withContext(Dispatchers.IO) {
            val tracks = mutableListOf<Track>()
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = when (sortBy) {
                "artist" -> "${MediaStore.Audio.Media.ARTIST} COLLATE NOCASE ASC"
                "album" -> "${MediaStore.Audio.Media.ALBUM} COLLATE NOCASE ASC"
                "duration" -> "${MediaStore.Audio.Media.DURATION} DESC"
                "date" -> "${MediaStore.Audio.Media.DATE_ADDED} DESC"
                else -> "${MediaStore.Audio.Media.TITLE} COLLATE NOCASE ASC"
            }
            context.contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val contentUri = ContentUris.withAppendedId(collection, id)
                    val data = cursor.getString(dataCol)
                    val folder = data?.substringBeforeLast('/', missingDelimiterValue = "")?.takeIf { it.isNotBlank() }
                    tracks.add(
                        Track(
                            id = id,
                            contentUri = contentUri,
                            title = cursor.getString(titleCol) ?: "Unknown",
                            artist = cursor.getString(artistCol),
                            album = cursor.getString(albumCol),
                            albumId = cursor.getLong(albumIdCol).takeIf { it > 0 },
                            durationMs = cursor.getLong(durationCol),
                            trackNumber = cursor.getInt(trackCol).takeIf { it > 0 },
                            year = cursor.getInt(yearCol).takeIf { it > 0 },
                            mimeType = cursor.getString(mimeCol),
                            sizeBytes = cursor.getLong(sizeCol),
                            dateAdded = cursor.getLong(dateCol),
                            folderPath = folder
                        )
                    )
                }
            }
            tracks
        }

    suspend fun getAlbums(): List<Album> = withContext(Dispatchers.IO) {
        getAllTracks().groupBy { it.albumId to it.displayAlbum }
            .map { (key, list) ->
                Album(id = key.first ?: 0L, title = key.second, artist = list.firstOrNull()?.displayArtist,
                    trackCount = list.size, year = list.mapNotNull { it.year }.maxOrNull())
            }.sortedBy { it.title.lowercase() }
    }

    suspend fun getArtists(): List<Artist> = withContext(Dispatchers.IO) {
        getAllTracks().groupBy { it.displayArtist }
            .map { (name, list) ->
                Artist(name = name, trackCount = list.size, albumCount = list.map { it.displayAlbum }.distinct().size)
            }.sortedBy { it.name.lowercase() }
    }

    suspend fun getFolders(): List<Folder> = withContext(Dispatchers.IO) {
        getAllTracks().filter { !it.folderPath.isNullOrBlank() }
            .groupBy { it.folderPath!! }
            .map { (path, list) ->
                Folder(path = path, name = path.substringAfterLast('/').ifBlank { path }, trackCount = list.size)
            }.sortedBy { it.name.lowercase() }
    }

    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val q = query.lowercase()
        getAllTracks().filter {
            it.title.lowercase().contains(q) ||
                it.displayArtist.lowercase().contains(q) ||
                it.displayAlbum.lowercase().contains(q)
        }
    }
}
