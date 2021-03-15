package com.example.callapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.HashMap

interface CometChatFriendsService {
    @Headers("accept: application/json",
        "content-type: application/json")
    @POST("/userReg?id=0test&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
    fun addFriend(@Header("apikey") apiKey: String,
                  @Header("appid") appID: String,
                  @Body params: HashMap<String, List<String>>
    )
            : Call<Data>
}