package com.riuir.calibur.net

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String): Observable<String>

    @Multipart
    @POST("xxxx/xxxx") //This is imaginary URL
    fun updateImage(@Part("name") name: RequestBody,
                    @Part image: MultipartBody.Part): Observable<String>
}
