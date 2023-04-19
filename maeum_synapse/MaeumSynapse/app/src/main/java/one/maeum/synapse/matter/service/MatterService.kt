package one.maeum.synapse.matter.service
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

    // /matter/state GET
    @GET("/matter/state")
    suspend fun getState() : MatterStateResponse?

}