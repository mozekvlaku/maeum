package one.maeum.synapse.matter.enum

import androidx.annotation.StringRes
import one.maeum.synapse.R

enum class EmotionsEnum(@StringRes val nameRes: Int) {
    Neutral(R.string.Neutral),
    Happy(R.string.Happy),
    Sad(R.string.Sad),
    Angry(R.string.Angry),
    Curious(R.string.Curious),
    Disgusted(R.string.Disgusted),
    Fearful(R.string.Fearful),
    Suspicious(R.string.Suspicious),
    Surprised(R.string.Surprised),
    Sleepy(R.string.Sleepy)
}