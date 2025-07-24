package io.github.moonggae.kmedia.controller

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive

// Audio Session Manager
class AudioSessionManager {
    @OptIn(ExperimentalForeignApi::class)
    fun setupAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryPlayback, null)
            audioSession.setActive(true, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun destroyAudioSession() {
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setActive(
                false,
                AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
