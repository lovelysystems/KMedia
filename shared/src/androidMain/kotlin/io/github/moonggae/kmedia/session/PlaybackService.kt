package io.github.moonggae.kmedia.session

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.WAKE_MODE_NETWORK
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import io.github.moonggae.kmedia.cache.CacheManager
import io.github.moonggae.kmedia.di.IsolatedKoinContext
import io.github.moonggae.kmedia.listener.PlaybackAnalyticsEventListener
import io.github.moonggae.kmedia.listener.PlaybackIOHandler
import io.github.moonggae.kmedia.listener.PlaybackStateHandler

object MediaConstants {
    const val ACTION_OPEN_MAIN_APP_UI = "io.github.moonggae.kmedia.session.ACTION_OPEN_MAIN_APP_UI"
}

@OptIn(UnstableApi::class)
class PlaybackService : MediaSessionService() {
    private var player: ExoPlayer? = null
        get() {
            if (field == null || field?.isReleased == true) {
                field = createPlayer()
            }
            return field
        }

    lateinit var session: MediaSession

    private val cacheManager: CacheManager by IsolatedKoinContext.koin.inject()
    private val playbackStateHandler: PlaybackStateHandler by IsolatedKoinContext.koin.inject()
    private val playbackIOHandler: PlaybackIOHandler by IsolatedKoinContext.koin.inject()
    private val playbackAnalyticsEventListener: PlaybackAnalyticsEventListener by IsolatedKoinContext.koin.inject()
    private fun createPlayer(): ExoPlayer {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val renderersFactory = DefaultRenderersFactory(this)
            .forceEnableMediaCodecAsynchronousQueueing()

        val builder = ExoPlayer.Builder(this, renderersFactory)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(WAKE_MODE_NETWORK)

        if (cacheManager.enableCache) {
            cacheManager.getProgressiveMediaSourceFactory(applicationContext)?.let { mediaSourceFactory ->
                builder.setMediaSourceFactory(mediaSourceFactory)
            }
        }

        return builder.build().apply {
            playbackStateHandler.attachTo(this)
            playbackIOHandler.attachTo(this)
            playbackAnalyticsEventListener.attach(this)
        }
    }

    override fun onCreate() {
        super.onCreate()
        this.setShowNotificationForIdlePlayer(SHOW_NOTIFICATION_FOR_IDLE_PLAYER_NEVER)
        val activityIntent = Intent(MediaConstants.ACTION_OPEN_MAIN_APP_UI).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            setPackage(applicationContext.packageName)
        }

        val pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            pendingIntentFlags
        )
        session = MediaSession
            .Builder(this, player!!)
            // Setting the activity for the session allows the notification to open the app's main UI
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onDestroy() {
        session.player.release()
        session.release()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        session.player.pause()
        session.player.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        session.release()
        super.onTaskRemoved(rootIntent)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = session
}
