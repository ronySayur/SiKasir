package com.project.sikasir.menu

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.project.sikasir.R

class loadingScreen : AppCompatActivity() {
    val STARTUP_DELAY = 300
    val ANIM_ITEM_DURATION = 1000
    val ITEM_DELAY = 300

    private val animationStarted = false
    private val splashTime: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        super.onCreate(savedInstanceState)
        setContentView(R.layout.load_screen)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus || animationStarted) {
            return
        }
        animate()
        super.onWindowFocusChanged(hasFocus)
    }

    private fun animate() {
        val logoImageView: ImageView = findViewById<ImageView>(R.id.img_logo)
        val container = findViewById<ViewGroup>(R.id.container)
        ViewCompat.animate(logoImageView)
            .translationY(-450f)
            .setStartDelay(STARTUP_DELAY.toLong())
            .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                DecelerateInterpolator(1.2f)
            ).start()
        for (i in 0 until container.childCount) {
            val v: View = container.getChildAt(i)
            var viewAnimator: ViewPropertyAnimatorCompat
            viewAnimator = if (v !is Button) {
                ViewCompat.animate(v)
                    .translationY(50f).alpha(1f)
                    .setStartDelay(ITEM_DELAY * i + 500.toLong())
                    .setDuration(1000)
            } else {
                ViewCompat.animate(v)
                    .scaleY(1f).scaleX(1f)
                    .setStartDelay(ITEM_DELAY * i + 500.toLong())
                    .setDuration(500)
            }
            viewAnimator.setInterpolator(DecelerateInterpolator()).start()
        }

        Handler().postDelayed(Runnable { // going to next activity
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }, splashTime)
    }
}