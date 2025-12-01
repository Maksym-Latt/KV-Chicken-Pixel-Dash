package com.chicken.pixeldash.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.chicken.pixeldash.R
import com.chicken.pixeldash.data.settings.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAudioController @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsRepository: SettingsRepository
) : AudioController {

    private var musicVolume: Float = 100.toVolume()
    private var soundVolume: Float = 100.toVolume()

    init {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            settingsRepository.musicVolumeFlow.collect { value ->
                musicVolume = value.toVolume()
                updateMusicVolume()
            }
        }

        CoroutineScope(Dispatchers.Main.immediate).launch {
            settingsRepository.soundVolumeFlow.collect { value ->
                soundVolume = value.toVolume()
                updateSoundVolume()
            }
        }
    }

    private fun updateMusicVolume() {
        musicPlayer?.setVolume(musicVolume, musicVolume)
    }

    private fun updateSoundVolume() {
        sfxPlayer?.setVolume(soundVolume, soundVolume)
    }

    private var currentMusic: MusicTrack? = null
    private var currentSound: SoundCue? = null
    private var musicPlayer: MediaPlayer? = null
    private var sfxPlayer: MediaPlayer? = null

    // ---------------------- PUBLIC API ----------------------

    override fun playMenuMusic() {
        playMusic(MusicTrack.MenuTheme)
    }

    override fun playGameMusic() {
        playMusic(MusicTrack.GameLoop)
    }

    override fun stopMusic() {
        musicPlayer?.run {
            stop()
            release()
        }
        musicPlayer = null
        currentMusic = null
    }

    override fun pauseMusic() {
        musicPlayer?.takeIf { it.isPlaying }?.pause()
    }

    override fun resumeMusic() {
        musicPlayer?.let { player ->
            currentMusic?.let { track -> player.setVolume(track.normalizedMusicVolume(), track.normalizedMusicVolume()) }
            player.start()
        }
    }

    override fun setMusicVolume(percent: Int) {
        musicVolume = percent.toVolume()
        currentMusic?.let { track ->
            val adjusted = track.normalizedMusicVolume()
            musicPlayer?.setVolume(adjusted, adjusted)
        }
    }

    override fun setSoundVolume(percent: Int) {
        soundVolume = percent.toVolume()
        currentSound?.let { cue ->
            val adjusted = cue.normalizedSoundVolume()
            sfxPlayer?.setVolume(adjusted, adjusted)
        }
    }

    override fun playGameWin() {
        playSound(SoundCue.VictoryFanfare)
    }

    override fun playChickenHit() {
        playSound(SoundCue.ChickenHit)
    }

    override fun playCollectEgg() {
        playSound(SoundCue.CollectEgg)
    }

    override fun playChickenJump() {
        playSound(SoundCue.ChickenJump)
    }

    // ---------------------- INTERNAL IMPLEMENTATION ----------------------

    private fun playMusic(track: MusicTrack) {
        if (currentMusic == track && musicPlayer != null) {
            val adjusted = track.normalizedMusicVolume()
            musicPlayer?.setVolume(adjusted, adjusted)
            if (musicPlayer?.isPlaying != true) {
                musicPlayer?.start()
            }
            return
        }

        stopMusic()

        musicPlayer = MediaPlayer.create(context, track.resId).apply {
            isLooping = true
            val adjusted = track.normalizedMusicVolume()
            setVolume(adjusted, adjusted)
            setOnCompletionListener(null)
            start()
        }
        currentMusic = track
    }

    private fun playSound(effect: SoundCue) {
        sfxPlayer?.run {
            stop()
            release()
        }
        sfxPlayer = MediaPlayer.create(context, effect.resId).apply {
            isLooping = false
            val adjusted = effect.normalizedSoundVolume()
            setVolume(adjusted, adjusted)
            setOnCompletionListener {
                it.release()
                if (sfxPlayer === it) {
                    sfxPlayer = null
                    currentSound = null
                }
            }
            start()
        }
        currentSound = effect
    }

    private fun Int.toVolume(): Float = (this.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)

    private fun MusicTrack.normalizedMusicVolume(): Float = musicVolume.adjustWith(MUSIC_NORMALIZATION[this])

    private fun SoundCue.normalizedSoundVolume(): Float = soundVolume.adjustWith(SOUND_NORMALIZATION[this])

    private fun Float.adjustWith(gain: Float?): Float = (this * (gain ?: 1f)).coerceIn(0f, 1f)

    // ---------------------- ENUMS ПОД res/raw ----------------------

    private enum class MusicTrack(@RawRes val resId: Int) {
        // res/raw/menu_theme.mp3
        MenuTheme(R.raw.menu_theme),

        // res/raw/game_loop.mp3
        GameLoop(R.raw.game_loop)
    }

    private enum class SoundCue(@RawRes val resId: Int) {
        VictoryFanfare(R.raw.sfx_victory_fanfare),

        ChickenHit(R.raw.sfx_chicken_hit),

        CollectEgg(R.raw.sfx_chicken_collect_egg),

        ChickenJump(R.raw.sfx_chicken_jump)
    }

    companion object {
        private val MUSIC_NORMALIZATION = mapOf(
            MusicTrack.MenuTheme to 0.8f,
            MusicTrack.GameLoop to 0.75f,
        )

        private val SOUND_NORMALIZATION = mapOf(
            SoundCue.VictoryFanfare to 0.9f,
            SoundCue.ChickenHit to 0.9f,
            SoundCue.CollectEgg to 0.9f,
            SoundCue.ChickenJump to 0.9f,
        )
    }
}
