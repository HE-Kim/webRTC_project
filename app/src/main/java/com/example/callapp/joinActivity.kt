package com.example.callapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*


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



        val cf = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = getResources().openRawResource(R.raw.server)
        var ca: Certificate? = null
        try {
            ca = cf.generateCertificate(caInput)
            println("ca=" + (ca as X509Certificate?)!!.subjectDN)
        } catch (e: CertificateException) {
            e.printStackTrace()
        } finally {
            caInput.close()
        }
        val keyStoreType = KeyStore.getDefaultType()
        var keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        if (ca == null) {

        }
        keyStore.setCertificateEntry("ca", ca)





        val userIdEdit = findViewById<View>(R.id.userIdEdit) as EditText
        val usernameEdit = findViewById<View>(R.id.usernameEdit) as EditText
        val userPwEdit = findViewById<View>(R.id.userPwEdit) as EditText
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)


        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + Arrays.toString(
                trustManagers
            )

        }
        val hostnameVerifier = HostnameVerifier { _, session ->
            HttpsURLConnection.getDefaultHostnameVerifier().run {
                verify("https://13.125.233.161:6443", session)
            }
        }
        val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory = sslContext.socketFactory
        val client1: OkHttpClient.Builder = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)



        client1.hostnameVerifier(HostnameVerifier { hostname, session -> true })





        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl("https://13.125.233.161:6443/")
            .client(client1.build())
            .addConverterFactory(GsonConverterFactory.create())


        val retrofit: Retrofit = builder.build()

        val client: GitHubClient = retrofit.create(GitHubClient::class.java)

        val call: Call<List<GitHubRepo>> = client.reposForUser("userReg")

        call.enqueue(object : Callback<List<GitHubRepo>> {
            override fun onFailure(call: Call<List<GitHubRepo>>, t: Throwable) {
                Log.e("debugTest", "error:(${t.message})")
            }

            override fun onResponse(
                call: Call<List<GitHubRepo>>,
                response: Response<List<GitHubRepo>>
            ) {
                val repos: List<GitHubRepo>? = response.body()
                var reposStr = ""

                repos?.forEach { it ->
                    reposStr += "$it\n"
                }
                println("로그: ${response.body().toString()}")

                // textView.text = reposStr
            }
        })



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

    fun getPinnedCertSslSocketFactory(context: Context): SSLSocketFactory? {
        try {

            val cf = CertificateFactory.getInstance("X.509")
            val caInput: InputStream = getResources().openRawResource(R.raw.server)
            var ca: Certificate? = null
            try {
                ca = cf.generateCertificate(caInput)
                println("ca=" + (ca as X509Certificate?)!!.subjectDN)
            } catch (e: CertificateException) {
                e.printStackTrace()
            } finally {
                caInput.close()
            }
            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            if (ca == null) {
                return null
            }
            keyStore.setCertificateEntry("ca", ca)
            val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
            val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
            tmf.init(keyStore)
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, tmf.trustManagers, null)
            return sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


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
        firebaseRef.child("$userID").child("info").child("connection").setValue("false") // 가능한지
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