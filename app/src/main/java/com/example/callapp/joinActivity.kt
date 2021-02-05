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
    var overlapID = false // f==중복x, t==중복o

    var userID = ""
    var username = ""
    var userpw = ""

    var check_id = false
    var check_name = false
    var check_pw = false

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
            overlapID = false

            userID_check()
            username_check()
            userPw_check()
/*
            if (userID == "")
                Toast.makeText(this, "You did not enter ID", Toast.LENGTH_SHORT).show()
            else {
                firebaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val children = snapshot.children.iterator()
                        var key: String?

                        while (children.hasNext()) { // 다음 값이 있으면
                            key = children.next().key // 다음 데이터 반환
                            overlapID = userID == key
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("Failed to read value.")
                    }
                })

                if (overlapID == true)
                    Toast.makeText(this, "Your ID is overlapped.", Toast.LENGTH_SHORT).show()
                else
                    firebaseRef = Firebase.database.getReference("$userID")

            }
*/
            /*
            if (username == "")
                Toast.makeText(this, "You did not enter name", Toast.LENGTH_SHORT).show()
            else
                firebaseRef.child("info").child("username").setValue("$username")*/
            /*
                 if (userpw == "")
                     Toast.makeText(this, "You did not enter password", Toast.LENGTH_SHORT).show()
                 else
                     firebaseRef.child("info").child("pw").setValue("$userpw")
     */

            // 그 다음 LOGIN page로 넘어가기 intent

            /*
            if (userID != "" && username != "" && userpw != "" && overlapID == false) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }
            */


            if (check_id&&check_name&&check_pw) {
                var UUID=UUID.randomUUID().toString()
                firebaseRef.child("$userID").child("UUID").setValue("$UUID")
                firebaseRef.child("$userID").child("info").child("outgoing").setValue("none") // 발신
                firebaseRef.child("$userID").child("info").child("receive").setValue("none") // 수신

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
            }


        }

    }


    //중복되는 부분 해결해야함
    private fun userID_check() {
        overlapID=false

        if (userID == "")
            Toast.makeText(this, "You did not enter ID", Toast.LENGTH_SHORT).show()
        else {
            println("테스트 key:  else 진입")

            firebaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children.iterator()
                    var key: String?

                    while (children.hasNext()) { // 다음 값이 있으면
                        key = children.next().key // 다음 데이터 반환
                        overlapID = userID == key
                     //   if(overlapID)
                        println("테스트 key:  $key // overlapID: $overlapID")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })

            if (overlapID == true) {
                Toast.makeText(this, "Your ID is overlapped.", Toast.LENGTH_SHORT).show()
                check_id = false
            } else {
               // firebaseRef = Firebase.database.getReference("$userID") // 이걸 추가하는 방식으로 수정 해야함
                firebaseRef.child("$userID").setValue("test")
                check_id = true
            }

        }

    }



    private fun username_check() {
        if (username == "")
            Toast.makeText(this, "You did not enter name", Toast.LENGTH_SHORT).show()
        else if (check_id == true) {
            firebaseRef.child("$userID").child("info").child("username").setValue("$username")
            check_name = true
        }
    }

    private fun userPw_check() {
        if (userpw == "")
            Toast.makeText(this, "You did not enter password", Toast.LENGTH_SHORT).show()
        else if (check_id == true) {
            firebaseRef.child("$userID").child("info").child("pw").setValue("$userpw")
            check_pw = true
        }
    }
}

