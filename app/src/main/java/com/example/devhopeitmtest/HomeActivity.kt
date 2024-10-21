package com.example.devhopeitmtest

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class HomeActivity : AppCompatActivity() {

    private val apiURL =
        "https://d1vzrdgjzuhzv7.cloudfront.net/fullscreen/bundle/779363139b5863e7865adb8b925a2ce18ad.json"
    val ALGORITHM = "Blowfish"
    val ENCRYPTION_MODE = "Blowfish/ECB/PKCS5Padding"
    val encryptionKey = "TestHashKey"
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageview : ImageView
    private lateinit var adapter: AndroidAppAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        imageview = findViewById(R.id.home_banaer)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        recyclerView.setHasFixedSize(true)
       // adapter.notifyDataSetChanged()
        fetchData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = async {
                    val client = OkHttpClient()
                    val request = Request.Builder().url(apiURL).build()
                    client.newCall(request).execute().body?.string()
                        ?: throw Exception("Error fetching API")
                }

                val responseString = response.await()
                val appinfoDetails =
                    Gson().fromJson<AppInfoDetals>(responseString, AppInfoDetals::class.java)

                var decryptedList  = appinfoDetails.androidApps.map {
                    AndroidApp(
                        name = decryptBlowfish(it.name?.replace("\n", ""), encryptionKey),
                        app_url = decryptBlowfish(it.app_url?.replace("\n", ""), encryptionKey),
                        banner = decryptBlowfish(it.banner?.replace("\n", ""), encryptionKey),
                        logo = decryptBlowfish(it.logo?.replace("\n", ""),encryptionKey),
                        pkg =  decryptBlowfish(it.pkg?.replace("\n", ""),encryptionKey),
                        publisher  = decryptBlowfish(it.publisher?.replace("\n", ""),encryptionKey),
                        scheme = decryptBlowfish(it.scheme?.replace("\n", ""), encryptionKey),
                        showcompoptsad = decryptBlowfish(it.showcompoptsad?.replace("\n", ""), encryptionKey),
                        showindex = decryptBlowfish(it.showindex?.replace("\n", ""), encryptionKey),
                        showmidad = decryptBlowfish(it.showmidad?.replace("\n", ""), encryptionKey),
                        showondetail = decryptBlowfish(it.showondetail?.replace("\n", ""), encryptionKey),
                        subtitle = decryptBlowfish(it.subtitle?.replace("\n", ""), encryptionKey)
                    )
                }

                decryptedList.forEach {
                    LogUtils.e("data", it.logo)
                }


                adapter = AndroidAppAdapter(decryptedList) {
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(FitCenter(), RoundedCorners(16))
                    Glide.with(this@HomeActivity)
                        .load(it.banner)
                        .apply(requestOptions)
                        .skipMemoryCache(true)//for caching the image url in case phone is offline
                        .into(imageview)
                    Log.d("TAG", "onClick: ${it.name}")
                }

                recyclerView.adapter = adapter

        //      adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }





    fun decryptBlowfish(encryptedData: String?, key: String): String {
        if (encryptedData.isNullOrEmpty()) return "encrypted data is null"
        return try {
            val keySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), ALGORITHM)
            val cipher = Cipher.getInstance(ENCRYPTION_MODE)
            cipher.init(Cipher.DECRYPT_MODE, keySpec)


            val encryptedByte =
                android.util.Base64.decode(encryptedData.trim(), android.util.Base64.DEFAULT)
            val decryptedBytes = cipher.doFinal(encryptedByte)

            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            "No encrypted data found"
        }
    }


}