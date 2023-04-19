package one.maeum.synapse.matter.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatterVerbalOutput(
    @SerialName("code") val code: Int,
    @SerialName("message") val message: String,
    @SerialName("returned") val returned: String
)
