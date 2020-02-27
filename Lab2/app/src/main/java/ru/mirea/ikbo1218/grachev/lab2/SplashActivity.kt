package ru.mirea.ikbo1218.grachev.lab2

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    private lateinit var r:DataReader
    private var changed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (savedInstanceState == null) {
            Thread{
                r = DataReader
                changeToMA()
            }.start()

            Thread {
                Thread.sleep(2000)
                changeToMA()

            }.start()

        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean("changed", changed)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        changed = savedInstanceState.getBoolean("changed")
    }

    private fun changeToMA(){
        if(!changed) {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            changed = true
            finish()

        }

    }

}
