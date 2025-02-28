package io.github.moonggae.kmedia.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.github.moonggae.kmedia.cache.CacheManager
import io.github.moonggae.kmedia.cache.CacheMediaItemWorker
import io.github.moonggae.kmedia.cache.CacheStatusListener
import io.github.moonggae.kmedia.cache.MusicCacheRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

internal actual class PlatformMusicCacheRepository(
    private val cacheManager: CacheManager,
    private val cacheStatusListener: CacheStatusListener,
    private val applicationContext: Context
) : MusicCacheRepository {
    override val maxSizeMb: Int = cacheManager.maxSizeMb

    override val enableCache: Boolean = cacheManager.enableCache

    override val usedSizeBytes: Flow<Long?> = if (enableCache) {
        flow {
            while (true) {
                emit(cacheManager.usedCacheBytes)
                delay(1000)
            }
        }
    } else {
        flowOf(0L)
    }

    override suspend fun clearCache() {
        cacheManager.cleanCache()
        cacheManager.keys.forEach {
            cacheStatusListener.onCacheStatusChanged(it, CacheStatusListener.CacheStatus.NONE)
        }
    }

    override suspend fun checkMusicCached(key: String) = cacheManager.checkItemCached(key) ?: false

    override suspend fun preCacheMusic(url: String, key: String) {
        val workData = workDataOf(
            CacheMediaItemWorker.KEY_URL to url,
            CacheMediaItemWorker.KEY_CACHE_KEY to key
        )

        val workRequest = OneTimeWorkRequestBuilder<CacheMediaItemWorker>()
            .setInputData(workData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(CacheMediaItemWorker.WORK_TAG)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "cache_${key}",
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                workRequest
            )
    }

    override suspend fun removeCachedMusic(vararg keys: String) {
        keys.forEach { key ->
            cacheManager.removeFile(key)
            cacheStatusListener.onCacheStatusChanged(key, CacheStatusListener.CacheStatus.NONE)
        }
    }
}