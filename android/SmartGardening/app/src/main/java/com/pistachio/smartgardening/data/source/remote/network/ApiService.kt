package com.pistachio.smartgardening.data.source.remote.network

import com.pistachio.smartgardening.BuildConfig
import com.pistachio.smartgardening.data.source.remote.response.ListPlantResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @Headers("Accept: application/json", "x-api-key: ${BuildConfig.API_KEY}")
    @POST("detection")
    fun postImage(
        @Part image: MultipartBody.Part,
    ): Call<ListPlantResponse>
}