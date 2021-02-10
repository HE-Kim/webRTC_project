package com.example.callapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.ktx.database

var username = ""
var userpw = ""
var check_username = false
var check_userpw = false
var firebaseRef = Firebase.database.getReference("users")
var val_PW=false

class MainActivity : AppCompatActivity() {

    private val permissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)  // 권한 가져오기
    private val requestcode = 1

    val nameTest = "TEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (!isPermissionGranted()) {  //권한이 부여되었니?
            askPermissions() //true --> askPermisstions으로 ㄱㄱ

        }

        Firebase.initialize(this) // 파이어베이스 initㅎㅏ는 녀석

        joinBtn.setOnClickListener {
            val intent = Intent(this, joinActivity::class.java)
            startActivity(intent)
        }



        loginBtn.setOnClickListener {
            username = usernameEdit.text.toString()
            userpw = userpwEdit.text.toString()
            check_username = false
            check_userpw = false


            if (username == "") {
                Toast.makeText(this, "You did not enter ID", Toast.LENGTH_SHORT).show()
            }
            if (userpw == "") {
                Toast.makeText(this, "You did not enter PW", Toast.LENGTH_SHORT).show()
            }

            println("테스트1 check_username : $check_username")


          //  val ID_val = check_ID()
          //  println("테스트- check_ID 함수 val_PW: $ID_val")

            firebaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    println("테스트- 파에어베이스 진입")

                    val children = snapshot.children.iterator()
                    var key: String?
                    check_username = false
                    check_userpw = false

                    while (children.hasNext()) { // 다음 값이 있으면
                        key = children.next().key // 다음 데이터 반환
                        if (!key.isNullOrEmpty()) {
                            check_username = username == key
                           // println("테스트 username: $username / key: $key / check_username : $check_username")

                            if (check_username) {

                               // println("테스트 1 if 성공")
                                firebaseRef.child(username).child("info").child("pw")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            //println("테스트 2 파이어 베이스")

                                            val value = snapshot.value
                                           // println("테스트 3 : ${snapshot.key} / ${snapshot.value}")

                                            if (value == userpw) {
                                                check_userpw=true
                                                intent()  //이걸 밖으로 빼는 작업을 해야할 듯함!
                                                //intent 함수
                                                //println("테스트4 pw 성공")
                                            } else {
                                              //  Toast_wrong_pw(check_userpw)
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            println("Failed to read value.")
                                        }
                                    })
                                break;
                            } else {
                              //  Toast_wrong_id(check_username)
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })

            println("테스트2 check_username : $check_username")

        }


    }

    private fun check_ID(): Boolean {

        val_PW=false


        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val children = snapshot.children.iterator()
                var key: String?
                check_username = false
                check_userpw = false


                while (children.hasNext()) { // 다음 값이 있으면
                    key = children.next().key // 다음 데이터 반환
                    if (!key.isNullOrEmpty()) {
                        check_username = username == key
                        // println("테스트 username: $username / key: $key / check_username : $check_username")

                        if (check_username) {

                            val_PW=check_PW()
                            println("테스트- check_ID 함수 while 안 val_PW: $val_PW")

                            // 브레이크 안하고 그냥 return 하면 어떨까~?
                            break;
                        } else {
                            // TF 가려서 위에서 토스트 해주기 // 안그럼 자꾸 toast 띄움
                            //Toast_wrong_id(check_username)
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        println("테스트- check_ID 함수 val_PW: $val_PW")
        return val_PW
    }

    private fun check_PW(): Boolean {
        check_userpw = false


        firebaseRef.child(username).child("info").child("pw")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val value = snapshot.value

                    if (value == userpw) {
                        check_userpw = true
                        println("테스트- check_PW 함수 while 안 val_PW: $check_userpw ")
                        //intent()
                        //intent 함수
                        //println("테스트4 pw 성공")
                    } else {
                        // tf 보낼꺼 생각하기
                        //Toast_wrong_pw(check_userpw)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })
        println("테스트- check_ID 함수 username: $check_userpw")

        return check_userpw
    }

    private fun Toast_wrong_id(check_username: Boolean) {
        if (!check_username)
            Toast.makeText(this, "Wrong ID ", Toast.LENGTH_SHORT).show()
    }

    private fun Toast_wrong_pw(check_userpw: Boolean) {
        if (!check_userpw)
            Toast.makeText(this, "Wrong PW ", Toast.LENGTH_SHORT).show()
    }

    private fun intent() {
        val intent = Intent(this, CallActivity::class.java)
        intent.putExtra("username", username)
        //   intent.putExtra("userpw", userpw)
        startActivity(intent)
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestcode
        )  // 외부저장소(카메라, 오디오)에 접근하려면 이걸 해여함!
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
            // 연결이 안되엉ㅆ다면 실패!
        }

        return true // 연결 성공하면
    }


    private fun setID() {
        // firebaseRef.child(nameTest).child("test").setValue("success")
        firebaseRef = Firebase.database.getReference("$username")
        firebaseRef.child("TEST").setValue("success")

        /*
        firebaseRef.child("A").child("test").setValue("success")
        firebaseRef.child("B").child("test").setValue("success")
        firebaseRef.child("C").child("test").setValue("success")
        */

    }
}