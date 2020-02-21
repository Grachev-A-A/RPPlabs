package ru.mirea.ikbo1218.grachev.lab1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (savedInstanceState == null) {
            Thread {
                Thread.sleep(2000)
                //if(!this.isDestroyed) {
                    val i = Intent(this, MainActivity::class.java)
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    startActivity(i)
                //}
            }.start()
        }

    }
}
