package com.example.devhopeitmtest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class HomeActivity : AppCompatActivity() {

    private  val apiURL = "https://d1vzrdgjzuhzv7.cloudfront.net/fullscreen/bundle/779363139b5863e7865adb8b925a2ce18ad.json"
    private val  ALGORITHM: String = "BlowFish"
    private val encryptionKey = "TestHashKey"
    private var icons = listOf<AppInfo>()
    private lateinit var  recyclerView : RecyclerView
    private lateinit var adapter : IconsAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
               val response = async {
                   val client   = OkHttpClient()
                   val request = Request.Builder().url(apiURL).build()
                   client.newCall(request).execute().body?.string() ?: throw Exception("Error fetching API")
               }

                val responseString = response.await()

               // LogUtils.e("AXE1","Resp ata :- $responseString")
                val appinfoDetails = Gson().fromJson<AppInfoDetals>(responseString, AppInfoDetals::class.java)

                //gson.fromJson(responseString, listType)

                //LogUtils.e("AXE","Resp String : ${Gson().toJson(gson)}")
                appinfoDetails.androidApps.forEach {
                    LogUtils.e("AXE1","Resp ata :- ${ decrypt(encryptionKey,it.name.toByteArray())}")
                }
                val decrytedData = decrypt(encryptionKey,responseString.toByteArray())
                Log.d("Decrypted", decrytedData)
            }catch (e :Exception){
                e.printStackTrace()
            }
//                launch(Dispatchers.Main) {
//                    adapter = IconsAdapter(descrytedData){ icon ->
//                        val intent = Intent(this@HomeActivity, DetailActivity::class.java)
//                        intent.putExtra("iconData", icon)
//                        startActivity(intent)
//                    }
//                    recyclerView.adapter = adapter
//                }
//
//            }catch (e :Exception){
//                e.printStackTrace()
//                withContext(Dispatchers.Main){
//                    Toast.makeText(this@HomeActivity, "Error feching Data ", Toast.LENGTH_SHORT).show()
//                }
//            }

        }
    }



    private fun fetchFromAPI(apiURL: String): String {
        val client   = OkHttpClient()
        val request = Request.Builder().url(apiURL).build()
        return client.newCall(request).execute().body?.string() ?: throw Exception("Error fetching API")


    }

    fun decrypt(key: String, encryptedData: ByteArray?): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }

   // private fun parseIcons(data : S)
}