package one.maeum.synapse.ai
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MaeumSpeechRecognition(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var callbackAppend: ((text: String?) -> Unit)? = null
    private var callbackDone: ((text: String?) -> Unit)? = null
    private var callbackStop: (()->Unit)? = null
    private var callbackBeginSpeech: (()->Unit)? = null
    private var callbackStopSpeech: (()->Unit)? = null
    private var isListening = false

    fun setCallbacks(callbackAppend: (text: String?)->Unit,callbackDone: (text: String?)->Unit,callbackStop: ()->(Unit),callbackBeginSpeech: ()->(Unit),callbackStopSpeech: ()->(Unit)){
        this.callbackStop = callbackStop
        this.callbackAppend = callbackAppend
        this.callbackStopSpeech = callbackStopSpeech
        this.callbackBeginSpeech = callbackBeginSpeech
        this.callbackDone = callbackDone
    }

    fun start() {

        if (isPermissionGranted()) {
            if (speechRecognizer == null) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
                speechRecognizer?.setRecognitionListener(recognitionListener)
            }
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            speechRecognizer?.startListening(intent)
            isListening = true
        } else {
            requestPermission()
        }
    }

    fun stop() {
        isListening = false
        speechRecognizer?.stopListening()
        callbackStop?.invoke()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSIONS_REQUEST_RECORD_AUDIO)
    }

    private val recognitionListener = object : RecognitionListener {

        override fun onReadyForSpeech(p0: Bundle?) {}

        override fun onBeginningOfSpeech() {
            callbackBeginSpeech?.invoke()
        }

        override fun onRmsChanged(p0: Float) {}

        override fun onBufferReceived(p0: ByteArray?) {}

        override fun onEndOfSpeech() {
            callbackStopSpeech?.invoke()
        }

        override fun onError(errorCode: Int) {
            val errorMsg = when (errorCode) {
                SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                SpeechRecognizer.ERROR_CLIENT -> "Other client side errors"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                SpeechRecognizer.ERROR_NETWORK -> "Network related errors"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network operation timed out"
                SpeechRecognizer.ERROR_NO_MATCH -> "No recognition result matched"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                SpeechRecognizer.ERROR_SERVER -> "Server sends error status"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                else -> "Unknown error"
            }
           // Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(p0: Bundle?) {
            val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.get(0)
            callbackDone?.invoke(text)
        }

        override fun onPartialResults(p0: Bundle?) {
            if (isListening) {
                val matches = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.get(0)
                callbackAppend?.invoke(text)
            }
        }

        override fun onEvent(p0: Int, p1: Bundle?) {}

    }

    companion object {
        const val PERMISSIONS_REQUEST_RECORD_AUDIO = 1
    }

}
