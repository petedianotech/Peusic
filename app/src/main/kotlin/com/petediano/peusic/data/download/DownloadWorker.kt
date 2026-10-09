package com.petediano.peusic.data.download

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.petediano.peusic.domain.model.DownloadStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

class DownloadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val dao = PeusicDatabase.getInstance(appContext).downloadDao()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val id = inputData.getString(KEY_DOWNLOAD_ID) ?: return@withContext Result.failure()
        val url = inputData.getString(KEY_URL) ?: return@withContext Result.failure()
        val fileName = inputData.getString(KEY_FILE_NAME) ?: "audio.mp3"

        try {
            dao.updateStatus(id, DownloadStatus.DOWNLOADING.name)

            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    dao.updateStatus(id, DownloadStatus.FAILED.name)
                    return@withContext Result.failure(
                        workDataOf("error" to "HTTP ${response.code}")
                    )
                }

                val body = response.body ?: run {
                    dao.updateStatus(id, DownloadStatus.FAILED.name)
                    return@withContext Result.failure()
                }

                val contentType = body.contentType()?.toString()
                val total = body.contentLength()

                val resolver = applicationContext.contentResolver
                val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val values = ContentValues().apply {
                    put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Audio.Media.MIME_TYPE, contentType ?: "audio/mpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/Peusic")
                        put(MediaStore.Audio.Media.IS_PENDING, 1)
                    }
                }

                val uri = resolver.insert(collection, values)
                    ?: run {
                        dao.updateStatus(id, DownloadStatus.FAILED.name)
                        return@withContext Result.failure()
                    }

                var downloaded = 0L
                resolver.openOutputStream(uri)?.use { out ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(8 * 1024)
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            out.write(buffer, 0, read)
                            downloaded += read
                            if (total > 0) {
                                dao.updateProgress(id, downloaded, total)
                                setProgress(workDataOf("progress" to (downloaded * 100 / total).toInt()))
                            }
                        }
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                }

                val entity = dao.getById(id)
                if (entity != null) {
                    dao.update(
                        entity.copy(
                            status = DownloadStatus.COMPLETED.name,
                            downloadedBytes = downloaded,
                            totalBytes = if (total > 0) total else downloaded,
                            localUri = uri.toString(),
                            mimeType = contentType,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                Result.success()
            }
        } catch (e: Exception) {
            dao.updateStatus(id, DownloadStatus.FAILED.name)
            Result.failure(workDataOf("error" to (e.message ?: "Download failed")))
        }
    }

    companion object {
        const val KEY_DOWNLOAD_ID = "download_id"
        const val KEY_URL = "url"
        const val KEY_FILE_NAME = "file_name"
    }
}
