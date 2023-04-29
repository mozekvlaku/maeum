package one.maeum.synapse.matter.model.response

import com.google.gson.annotations.SerializedName

@kotlinx.serialization.Serializable
data class MatterActionResponse(
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Number,
)
