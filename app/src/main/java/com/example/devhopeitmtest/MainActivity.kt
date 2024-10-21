package com.example.devhopeitmtest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

class MainActivity : AppCompatActivity(){


   private lateinit var remoteConfig : FirebaseRemoteConfig
   private val  splashDefaultTime = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

      //  remoteConfig = Firebase.remoteConfig
    //    val configsett= remoteConfigSettings {
        //    minimumFetchIntervalInSeconds = 3600
  //      }
        goTOHomeScreen()
       // remoteConfig.setConfigSettingsAsync(configsett)
     //   remoteConfig.setDefaultsAsync(R.xml.remote_config_default)

       // fetchConfig()
    }



    private fun fetchConfig() {
        val configSetting = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()

        remoteConfig.setConfigSettingsAsync(configSetting)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val splashTime = remoteConfig.getLong("splash_time")
                    Handler().postDelayed({
                        goTOHomeScreen()
                    }, splashTime.takeIf { it > 0 } ?: splashDefaultTime)
                }

            }
    }

    private fun goTOHomeScreen() {
        val intent  = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

}