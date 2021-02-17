package com.example.callapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddActivity : AppCompatActivity() {

    var firebaseRef = Firebase.database.getReference("users")
    var username =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        username = intent.getStringExtra("username")!!

        //에디트에 입력된것 받아오기
        //파이어 베이스에서 찾기


    }
}