package com.pistachio.smartgardening.data.source.remote.network

import com.pistachio.smartgardening.data.source.remote.response.ListPlantResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
//    @Headers("Authorization: token 12345")
    @Multipart
    @Headers("Accept: application/json")
    @POST("detection")
    fun postImage(
        @Part image: MultipartBody.Part,
    ): Call<ListPlantResponse>
}