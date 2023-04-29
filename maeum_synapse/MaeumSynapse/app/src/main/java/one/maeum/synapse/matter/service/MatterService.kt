package one.maeum.synapse.matter.service
import one.maeum.synapse.matter.model.response.MatterActionResponse
import one.maeum.synapse.matter.model.response.MatterStateResponse
import one.maeum.synapse.matter.model.response.MatterVerbalOutput
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface MatterService {


    // /matter/verbal/input ?text POST

    @POST("/matter/verbal/input")
    suspend fun sendTextToAI(
        @Query("text") text: String?
    ) : Call<MatterVerbalOutput>?

    @POST("/matter/emotions/set")
    suspend fun setEmotions(
        @Query("emotion") emotion: String?
    ) : MatterActionResponse?

    // /matter/state GET
    @GET("/matter/state")
    suspend fun getState() : MatterStateResponse?

    companion object {
        const val PATH_BLINKING = "/matter/lifesimulator/blinking"
        const val PATH_MIMICS = "/matter/lifesimulator/mimics"
        const val PATH_RASA = "/matter/verbal/rasa"
        const val PATH_AUTO = "/matter/lifesimulator/auto"
        const val PATH_RIGHT = "/matter/motoric/right"
        const val PATH_LEFT = "/matter/motoric/left"


        const val ENDPOINT_ON = "/on"
        const val ENDPOINT_OFF = "/off"
    }

    @POST("$PATH_BLINKING$ENDPOINT_ON")
    suspend fun startBlinking(): MatterActionResponse?

    @POST("$PATH_BLINKING$ENDPOINT_OFF")
    suspend fun stopBlinking(): MatterActionResponse?

    @POST("$PATH_MIMICS$ENDPOINT_ON")
    suspend fun startMimics(): MatterActionResponse?

    @POST("$PATH_MIMICS$ENDPOINT_OFF")
    suspend fun stopMimics(): MatterActionResponse?

    @POST("$PATH_RASA$ENDPOINT_ON")
    suspend fun startRasa(): MatterActionResponse?

    @POST("$PATH_RASA$ENDPOINT_OFF")
    suspend fun stopRasa(): MatterActionResponse?

    @POST("$PATH_AUTO$ENDPOINT_ON")
    suspend fun startAuto(): MatterActionResponse?

    @POST("$PATH_AUTO$ENDPOINT_OFF")
    suspend fun stopAuto(): MatterActionResponse?

    @POST("$PATH_RIGHT$ENDPOINT_ON")
    suspend fun rightOn(): MatterActionResponse?

    @POST("$PATH_RIGHT$ENDPOINT_OFF")
    suspend fun rightOff(): MatterActionResponse?

    @POST("$PATH_LEFT$ENDPOINT_ON")
    suspend fun leftOn(): MatterActionResponse?

    @POST("$PATH_LEFT$ENDPOINT_OFF")
    suspend fun leftOff(): MatterActionResponse?

}