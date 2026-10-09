package com.petediano.peusic.data.download

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.petediano.peusic.domain.model.DownloadItem
import com.petediano.peusic.domain.model.DownloadStatus

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val url: String,
    val title: String,
    val fileName: String,
    val mimeType: String? = null,
    val totalBytes: Long = -1L,
    val downloadedBytes: Long = 0L,
    val status: String = DownloadStatus.QUEUED.name,
    val errorMessage: String? = null,
    val localUri: String? = null,
    val sourceHost: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val workId: String? = null
) {
    fun toDomain(): DownloadItem = DownloadItem(
        id = id, url = url, title = title, fileName = fileName, mimeType = mimeType,
        totalBytes = totalBytes, downloadedBytes = downloadedBytes,
        status = try { DownloadStatus.valueOf(status) } catch (_: Exception) { DownloadStatus.FAILED },
        errorMessage = errorMessage, localUri = localUri, sourceHost = sourceHost,
        createdAt = createdAt, updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(item: DownloadItem, workId: String? = null) = DownloadEntity(
            id = item.id, url = item.url, title = item.title, fileName = item.fileName,
            mimeType = item.mimeType, totalBytes = item.totalBytes,
            downloadedBytes = item.downloadedBytes, status = item.status.name,
            errorMessage = item.errorMessage, localUri = item.localUri,
            sourceHost = item.sourceHost, createdAt = item.createdAt,
            updatedAt = item.updatedAt, workId = workId
        )
    }
}
