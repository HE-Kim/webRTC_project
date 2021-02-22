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
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.activity_join.JoinBtn

class AddActivity : AppCompatActivity() {

    var firebaseRef = Firebase.database.getReference("pairing")
    var username =""
    var pid = ""
    var stbname=""
    var channel=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val VerifyEdit = findViewById<View>(R.id.VerifyEdit) as EditText

        username = intent.getStringExtra("username")!!


        Btn.setOnClickListener {
            pid = VerifyEdit.text.toString()
            println("테스트 버튼 클릭 pid $pid")
            pid_check()
        }

        infoBtn.setOnClickListener {
            stbname = nameEdit.text.toString()
            channel = channelEdit.text.toString()
            initinfo()

        }

        //에디트에 입력된것 받아오기
        //파이어 베이스에서 찾기


    }

    private fun pid_check() {


        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children.iterator()
                var key: String?

                while (children.hasNext()) { // 다음 값이 있으면
                    key = children.next().key // 다음 데이터 반환

                    println("테스트 key $key / pid $pid")
                    if(key==pid)
                    {
                        firebaseRef.child(pid).child("user").setValue(username)
                        Toast_add()
                        //파이어베이스에 친구 추가 해주기
                        //그전에 자기 정보 등록하기 / 채널 선택 / 상대방에게 보여질 이름 /
                    }

                }
                return

            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })
    }

    private fun Toast_add() {
        Toast.makeText(this, "페어링 성공", Toast.LENGTH_SHORT).show()
        Verifylayout.visibility = View.GONE
        infolayout.visibility = View.VISIBLE
    }
    private fun Toast_info() {
        Toast.makeText(this, "등록 완료", Toast.LENGTH_SHORT).show()

    }

    private fun initinfo() {

        firebaseRef.child(pid).child("STB").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseRef = Firebase.database.getReference("users")
                firebaseRef.child("${snapshot.value}").child("info").child("friends").
                child(username).child("name").setValue(stbname)
                firebaseRef.child("${snapshot.value}").child("info").child("friends").
                child(username).child("channel").setValue(channel)

                firebaseRef.child(username).child("info").child("friends").
                child("${snapshot.value}").child("channel").setValue(channel)
                success()
                //firebaseRef.child(username).child("info").child("friends").child("${snapshot.value}").child("name").setValue(stbname)

            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })


    }

    private fun success() {
        firebaseRef = Firebase.database.getReference("pairing")
        firebaseRef.child(pid).child("success").setValue(true)
        Toast_info()
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)

    }
}