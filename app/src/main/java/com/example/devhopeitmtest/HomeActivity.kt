package com.example.devhopeitmtest

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipDescription
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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
    private lateinit var tvName : TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvpublisher : TextView
    private lateinit var adapter: AndroidAppAdapter
    private lateinit var buttonDetails : Button
    private lateinit var decryptedList : List<AndroidApp>
    private val CHANNEL_ID = "notification_channel"
    private val notificationPermissionCode = 1001
    var permissions =
      arrayOf<String>(android.Manifest.permission.POST_NOTIFICATIONS)



    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        imageview = findViewById(R.id.home_banaer)
        recyclerView = findViewById(R.id.recycler_view)
        buttonDetails = findViewById(R.id.details_button)
        tvName = findViewById(R.id.tvName)
        tvDescription = findViewById(R.id.tvDescription)
        tvpublisher = findViewById(R.id.tvpublisher)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        recyclerView.setHasFixedSize(true)
        createNotificationChannel()
        callNotfication()
        fetchData()
    }

    private fun callNotfication() {
        // Request notification permission
        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        } else {
            callButton()
        }
    }


    private fun callButton() {
        buttonDetails.setOnClickListener(View.OnClickListener {
            showLocalNotification()
        })
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, permissions.toString()) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(this,
            permissions, notificationPermissionCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            notificationPermissionCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, you can now fetch app data
                    callButton()
                } else {
                    requestNotificationPermission()
                }
            }
        }
    }



    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val descriptionText = "Channel for local notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun showLocalNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, DetailActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your icon
            .setContentTitle("DevHopeItMTest")
            .setContentText("You are now viewing details in DevHopeItMTest")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
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

                 decryptedList  = appinfoDetails.androidApps.map {
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
                    tvName.text = it.name
                    tvDescription.text = it.subtitle
                    tvpublisher.text = it.publisher
                }

                withContext(Dispatchers.Main){
                    recyclerView.adapter = adapter

                }
             //   recyclerView.adapter = adapter

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