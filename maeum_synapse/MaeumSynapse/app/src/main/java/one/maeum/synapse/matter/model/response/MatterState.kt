package one.maeum.synapse.matter.model.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import java.sql.Time

@Serializable
data class MatterStateResponse(
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Number,
    @SerializedName("result") val result: MatterState?
)
@Serializable
data class MatterState(
    @SerializedName("ep") val ep: Ep,
    @SerializedName("mm") val mm: Mm,
    @SerializedName("vm") val vm: Vm,
    @SerializedName("me") val me: Me,
    @SerializedName("vr") val vr: Vr,
    @SerializedName("lt") val lt: Lt,
    @SerializedName("vb") val vb: Vb,
    @SerializedName("cd") val cd: Cd,
    @SerializedName("le") val le: Le,
    @SerializedName("vp") val vp: Vp
)
@Serializable
data class Ep(
    @SerializedName("pipeline") val pipeline: List<Any>,
    @SerializedName("em_state") val em_state: String,
    @SerializedName("em_state_int") val em_state_int: Int
)
@Serializable
data class Mm(
    @SerializedName("nestor_online") val nestor_online: Boolean,
    @SerializedName("nestor_state") val nestor_state: NestorState

)
@Serializable
data class NestorState (
    @SerializedName("right") val right: Boolean,
    @SerializedName("left") val left: Boolean
)

@Serializable
data class Vp(
    @SerializedName("visual_online") val visualOnline: Boolean
)
@Serializable
data class Me(
    @SerializedName("doing_mimics") val doingMimics: Boolean
)
@Serializable
data class Le(
    @SerializedName("activated_looking") val activatedLooking: Boolean,
    @SerializedName("can_do_headmove") val canDoHeadmove: Boolean,
    @SerializedName("x_eyes") val xEyes: Float,
    @SerializedName("x_axis") val xAxis: Float,
)
@Serializable
data class Vm(
    @SerializedName("memory") val memory: Memory,
    @SerializedName("view") val view: View
)
@Serializable
data class Memory(
    @SerializedName("people") val people: List<Person>,
    @SerializedName("objects") val objects: List<Obj>
)
@Serializable
data class Person(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String,
    @SerializedName("probability") val probability: Int,
    @SerializedName("emotion_current") val emotion_current: String,
    @SerializedName("maeum_emotional_relation") val maeum_emotional_relation: String,
    @SerializedName("foreground") val foreground: Boolean = false,
    @SerializedName("in_view") val inView: Boolean = false
)
@Serializable
data class Obj(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String,
    @SerializedName("probability") val probability: Double,
    @SerializedName("count") val count: Int,
    @SerializedName("relation") val relation: String,
    @SerializedName("cadence") val cadence: Int
)
@Serializable
data class View(
    @SerializedName("people") val people: List<String>,
    @SerializedName("foreground") val foreground: String
)
@Serializable
data class Vr(
    @SerializedName("message_history") val message_history: List<Message>,
    @SerializedName("rasa_online") val rasa_online: Boolean,
    @SerializedName("tts_online") val tts_online: Boolean
)
@Serializable
data class Message(
    @SerializedName("type") val type: String,
    @SerializedName("participantId") val participantId: String,
    @SerializedName("myself") val myself: Boolean,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: TimeMaeum
)

@Serializable
data class TimeMaeum(
    @SerializedName("year") val year: Number,
    @SerializedName("month") val month: Number,
    @SerializedName("day") val day: Number,
    @SerializedName("hour") val hour: Number,
    @SerializedName("minute") val minute: Number,
    @SerializedName("second") val second: Number
)
@Serializable
data class Lt(
    @SerializedName("cortexSettings") val cortexSettings: CortexSettings,
    @SerializedName("nestorSettings") val nestorSettings: NestorSettings,
    @SerializedName("matterSettings") val matterSettings: MatterSettings,
    @SerializedName("biomimeticSettings") val biomimeticSettings: BiomimeticSettings
)
@Serializable
data class CortexSettings(
    @SerializedName("verbalCortexRasa") val verbalCortexRasa: VerbalCortexRasa,
    @SerializedName("verbalCortexTTS") val verbalCortexTTS: VerbalCortexTTS
)
@Serializable
data class VerbalCortexRasa(
    @SerializedName("useOnlyGPT") val useOnlyGPT: Boolean,
    @SerializedName("ipAddress") val ipAddress: String,
    @SerializedName("port") val port: String,
    @SerializedName("path") val path: String,
    @SerializedName("apiKeyGPT") val apiKeyGPT: String
)
@Serializable
data class NestorSettings(
    @SerializedName("ipAddress") val ipAddress: String,
    @SerializedName("port") val port: String
)
@Serializable
data class MatterSettings(
    @SerializedName("restPort") val restPort: String,
    @SerializedName("wsPort") val wsPort: String
)
@Serializable
data class BiomimeticSettings(
    @SerializedName("blinking") val blinking: Boolean
)
@Serializable
data class VerbalCortexTTS(
    @SerializedName("ipAddress") val ipAddress: String,
    @SerializedName("port") val port: String
)
@Serializable
data class Vb(
    @SerializedName("blinking") val blinking: Boolean
)
@Serializable
data class Cd(
    @SerializedName("dispatch") val dispatch: List<Any>
)
