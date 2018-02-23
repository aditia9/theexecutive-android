package com.ranosys.theexecutive

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.ranosys.theexecutive.base.BaseActivity

/**
 * Created by Mohammad Sunny on 25/1/18.
 */
class SplashActivity : BaseActivity() {

    val SPLASH_TIMEOUT = 1000
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        handler.postDelayed({
            kotlin.run {
                moveToHome()
            }
        }, SPLASH_TIMEOUT.toLong())
    }

    private fun moveToHome(){
        val intent = Intent(this@SplashActivity, UserActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("MissingSuperCall")
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null);
    }
}
