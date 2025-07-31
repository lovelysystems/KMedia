package io.github.moonggae.kmedia.di

import io.github.moonggae.kmedia.cache.CacheManager
import io.github.moonggae.kmedia.cache.MusicCacheRepository
import io.github.moonggae.kmedia.controller.MediaPlaybackController
import io.github.moonggae.kmedia.controller.PlatformMediaPlaybackController
import io.github.moonggae.kmedia.listener.PlaybackAnalyticsEventListener
import io.github.moonggae.kmedia.listener.PlaybackIOHandler
import io.github.moonggae.kmedia.listener.PlaybackStateHandler
import io.github.moonggae.kmedia.repository.PlatformMusicCacheRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun getPlatformPlaybackModule(): Module = module {
    singleOf(::CacheManager)
    singleOf(::PlaybackStateHandler)
    singleOf(::PlaybackIOHandler)
    singleOf(::PlatformMediaPlaybackController) bind MediaPlaybackController::class
    singleOf(::PlatformMusicCacheRepository) bind MusicCacheRepository::class
    singleOf(::PlaybackAnalyticsEventListener)
}
