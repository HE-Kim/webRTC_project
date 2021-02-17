package com.example.callapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*
import java.util.*

class joinActivity : AppCompatActivity() {

    var firebaseRef = Firebase.database.getReference("users")

    var uniqueId = ""


    var userID = ""
    var username = ""
    var userpw = ""

    var check_id = false
    var check_name = false
    var check_pw = false

    val arrList: MutableList<String> = mutableListOf<String>("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val userIdEdit = findViewById<View>(R.id.userIdEdit) as EditText
        val usernameEdit = findViewById<View>(R.id.usernameEdit) as EditText
        val userPwEdit = findViewById<View>(R.id.userPwEdit) as EditText

        JoinBtn.setOnClickListener {
            //edittext 값이 다 들어갔는지 확인
            userID = userIdEdit.text.toString()
            username = usernameEdit.text.toString()
            userpw = userPwEdit.text.toString()

            check_id = false
            check_name = false
            check_pw = false


            userID_check()
        }

    }


    //중복되는 부분 해결해야함
    private fun userID_check() {



        if (userID == "")
            Toast.makeText(this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show()
        else {

            firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children.iterator()
                    var key: String?
                    arrList.clear()

                    while (children.hasNext()) { // 다음 값이 있으면
                        key = children.next().key // 다음 데이터 반환

                        if (!key.isNullOrEmpty()) {
                            arrList.add(key)
                        }
                    }

                    check_overlap(arrList)
                    return

                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })
        }
    }


    private fun check_overlap(arrList: MutableList<String>) {

        if (arrList.contains(userID)) {
            Toast.makeText(this, "중복된 ID 입니다.", Toast.LENGTH_SHORT).show()

        } else {
            check_id = true
            username_check()
        }
    }

    private fun info() {
        var UUID = UUID.randomUUID().toString()
        firebaseRef.child("$userID").child("UUID").setValue("$UUID")
        firebaseRef.child("$userID").child("info").child("outgoing").setValue("none") // 발신
        firebaseRef.child("$userID").child("info").child("receive").setValue("none") // 수신
        firebaseRef.child("$userID").child("info").child("friends").setValue("none") // 친구
        firebaseRef.child("$userID").child("info").child("isAvailable").setValue("none") // 가능한지
        firebaseRef.child("$userID").child("info").child("type").setValue("APP")
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }


    private fun userPw_check() {
        if (userpw == "")
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
        else if (check_id == true) {
            firebaseRef.child("$userID").child("info").child("pw").setValue("$userpw")
            check_pw = true
            info()
        }
    }

    private fun username_check() {
        if (username == "")
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show()
        else if (check_id == true) {
            firebaseRef.child("$userID").child("info").child("username").setValue("$username")
            check_name = true
            userPw_check()
        }
    }
}