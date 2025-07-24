package io.github.moonggae.kmedia.controller.controlcenter

import platform.MediaPlayer.MPRemoteCommandCenter
import platform.MediaPlayer.MPRemoteCommandHandlerStatusSuccess

interface MediaCommandHandler {
    fun onPlay()
    fun onPause()
    fun onNext()
    fun onPrevious()
    fun onSeek(positionMs: Long)
}

internal class MediaCommandCenter(
    private val commandHandler: MediaCommandHandler
) {
    private val commandCenter = MPRemoteCommandCenter.sharedCommandCenter()

    fun setupCommands() {
        // Play Command
        commandCenter.playCommand.enabled = true
        commandCenter.playCommand.addTargetWithHandler { _ ->
            commandHandler.onPlay()
            MPRemoteCommandHandlerStatusSuccess
        }

        // Pause Command
        commandCenter.pauseCommand.enabled = true
        commandCenter.pauseCommand.addTargetWithHandler { _ ->
            commandHandler.onPause()
            MPRemoteCommandHandlerStatusSuccess
        }
    }

    fun cleanup() {
        commandCenter.playCommand.removeTarget(null)
        commandCenter.pauseCommand.removeTarget(null)
    }
}
