package io.github.moonggae.kmedia.di

import io.github.moonggae.kmedia.cache.CacheManager
import io.github.moonggae.kmedia.cache.MusicCacheRepository
import io.github.moonggae.kmedia.controller.MediaPlaybackController
import io.github.moonggae.kmedia.controller.PlatformMediaPlaybackController
import io.github.moonggae.kmedia.custom.CustomLayoutUpdateListener
import io.github.moonggae.kmedia.custom.MediaCustomLayoutHandler
import io.github.moonggae.kmedia.listener.PlaybackAnalyticsEventListener
import io.github.moonggae.kmedia.listener.PlaybackIOHandler
import io.github.moonggae.kmedia.listener.PlaybackStateHandler
import io.github.moonggae.kmedia.repository.PlatformMusicCacheRepository
import io.github.moonggae.kmedia.session.LibrarySessionCallback
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual fun getPlatformPlaybackModule(): Module = module {
    singleOf(::CacheManager)
    singleOf(::PlaybackStateHandler)
    singleOf(::PlaybackIOHandler)
    singleOf(::PlatformMediaPlaybackController) bind MediaPlaybackController::class
    singleOf(::PlatformMusicCacheRepository) bind MusicCacheRepository::class
    singleOf(::LibrarySessionCallback)
    singleOf(::MediaCustomLayoutHandler)
    singleOf(::CustomLayoutUpdateListener)
    singleOf(::PlaybackAnalyticsEventListener)

//    single<PendingIntent> {
//        PendingIntent.getActivity(
//            androidContext(),
//            1,
//            Intent(androidContext(), get()).apply {
//                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                putExtra(PlaybackConstraint.EXTRA_NAME_EVENT, PlaybackConstraint.EVENT_NOTIFICATION_CLICK)
//            },
//            FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//    }
}