package io.github.moonggae.kmedia.session

import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.github.moonggae.kmedia.custom.MediaCustomLayoutHandler
import kotlinx.coroutines.CoroutineScope

@OptIn(UnstableApi::class)
internal class LibrarySessionCallback(
    private val scope: CoroutineScope,
    private val customLayoutHandler: MediaCustomLayoutHandler,
): MediaLibrarySession.Callback {
    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        return super.onPlaybackResumption(mediaSession, controller)
    }

    override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): MediaSession.ConnectionResult {
        val sessionCommands = MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()
            .remove(SessionCommand.COMMAND_CODE_SESSION_SET_RATING)
            .remove(SessionCommand.COMMAND_CODE_LIBRARY_SUBSCRIBE)
            .remove(SessionCommand.COMMAND_CODE_LIBRARY_UNSUBSCRIBE)
            .remove(SessionCommand.COMMAND_CODE_LIBRARY_SEARCH)
            .remove(SessionCommand.COMMAND_CODE_LIBRARY_GET_SEARCH_RESULT)
            .build()

        val playerCommands = MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon().build()

        return when {
            session.isMediaNotificationController(controller) ->
                AcceptedResultBuilder(session)
                    .setAvailablePlayerCommands(playerCommands)
                    .setAvailableSessionCommands(sessionCommands)
                    .setCustomLayout(customLayoutHandler.createCustomLayout(session))
                    .build()

            else -> AcceptedResultBuilder(session).build()
        }
    }

    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle
    ): ListenableFuture<SessionResult> {
        customLayoutHandler.handleCustomCommand(session, customCommand)
        return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
    }
}
