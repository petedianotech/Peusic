package com.petediano.peusic.data.download

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.petediano.peusic.domain.model.DownloadItem
import com.petediano.peusic.domain.model.DownloadStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class DownloadRepository(private val context: Context) {

    private val dao = PeusicDatabase.getInstance(context).downloadDao()
    private val workManager = WorkManager.getInstance(context)

    fun observeDownloads(): Flow<List<DownloadItem>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun enqueue(url: String, title: String? = null): Result<String> {
        val validation = UrlValidator.validate(url)
        if (!validation.isValid) {
            return Result.failure(IllegalArgumentException(validation.reason ?: "Invalid URL"))
        }

        val id = UUID.randomUUID().toString()
        val fileName = validation.suggestedFileName ?: "audio_${System.currentTimeMillis()}.mp3"
        val host = try { java.net.URI(validation.normalizedUrl!!).host } catch (_: Exception) { null }

        val item = DownloadItem(
            id = id,
            url = validation.normalizedUrl!!,
            title = title?.takeIf { it.isNotBlank() } ?: fileName.substringBeforeLast('.'),
            fileName = fileName,
            sourceHost = host,
            status = DownloadStatus.QUEUED
        )

        dao.insert(DownloadEntity.fromDomain(item))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .setInputData(
                workDataOf(
                    DownloadWorker.KEY_DOWNLOAD_ID to id,
                    DownloadWorker.KEY_URL to item.url,
                    DownloadWorker.KEY_FILE_NAME to item.fileName
                )
            )
            .addTag("download_$id")
            .build()

        workManager.enqueueUniqueWork("download_$id", ExistingWorkPolicy.KEEP, request)
        return Result.success(id)
    }

    suspend fun cancel(id: String) {
        workManager.cancelUniqueWork("download_$id")
        dao.updateStatus(id, DownloadStatus.CANCELLED.name)
    }
}
