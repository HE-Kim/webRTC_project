package com.example.callapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GitHubClient {

//    @GET("/users/{user}/repos")
    @POST("{user}?id=test1234&passwd=user1234!&role=50&name=테스트4&contact1=010&contact2=1111&contact3=2222")
    fun reposForUser(@Path("user") user:String): Call<List<GitHubRepo>>
}