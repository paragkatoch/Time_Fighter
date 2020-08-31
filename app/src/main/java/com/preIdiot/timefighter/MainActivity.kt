package com.preIdiot.timefighter

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    var saveValues = longArrayOf(0,0)
    val current = this
    var score = 0
    var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal var initialCountDown = 60000L
    internal var countDownInterval = 1000L
    internal var timeLeftOnTimer = 60000L
    internal lateinit var tapMeButton: Button
    internal lateinit var scoreTextView: TextView
    internal lateinit var timeTextView: TextView

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tapMeButton = findViewById(R.id.TapMe)
        scoreTextView = findViewById(R.id.Score)
        timeTextView = findViewById(R.id.Time)

        if (savedInstanceState != null) {
            saveValues[0] = savedInstanceState.getLong(SCORE_KEY)
            saveValues[1] = savedInstanceState.getLong(TIME_LEFT_KEY)
            Log.i("MainActivity", "onCreate Called $score $timeLeftOnTimer")
            //resetGame(score,timeLeftOnTimer-1000)
        } else {
            resetGame()
        }
        tapMeButton.setOnClickListener { view ->

            val bounceAnimation = AnimationUtils.loadAnimation(current,R.anim.bounce)
            view.startAnimation(bounceAnimation)

            incrementScore()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i("MainActivity", "onSavedInstanceState Called")
        outState.putLong(SCORE_KEY,score.toLong())
        outState.putLong(TIME_LEFT_KEY,timeLeftOnTimer)

       // countDownTimer.cancel()

    }

    override fun onStart() {
        super.onStart()
        Log.i("MainActivity", "onStart Called ${saveValues[0]} ${saveValues[1]}")
        if(saveValues[1] != 0L)
        resetGame(saveValues[0].toInt(),saveValues[1])
    }

    override fun onStop() {
        super.onStop()
        saveValues[0] = score.toLong()
        saveValues[1] = timeLeftOnTimer
        Log.i("MainActivity", "onStop Called ${saveValues[0]} ${saveValues[1]}")
        countDownTimer.cancel()
    }

    private fun resetGame(gameScore:Int = 0,time: Long = initialCountDown) {
        score = gameScore
        scoreTextView.text = getString(R.string.score, score)

        val initialTimeLeft = time / 1000
        timeTextView.text = getString(R.string.time, initialTimeLeft)

        countDownTimer = object : CountDownTimer(time, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1000
                timeTextView.text = getString(R.string.time, timeLeft)

                if(timeLeft <=10) {
                    val blinkTime = AnimationUtils.loadAnimation(current, R.anim.blinktime)
                    timeTextView.startAnimation(blinkTime)
                }
            }

            override fun onFinish() {
                endGame()
            }
        }
        if (time == initialCountDown)
            gameStarted = false
        else
            startGame()
    }

    private fun incrementScore() {
        if(!gameStarted)
            startGame()
        score++
        scoreTextView.text = getString(R.string.score,score)

        val blinkAnimation = AnimationUtils.loadAnimation(current,R.anim.blinkscore)
        scoreTextView.startAnimation(blinkAnimation)
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame() {
        Toast.makeText(current,getString(R.string.gameEndMessage, score), Toast.LENGTH_LONG).show()
        resetGame()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.aboutmenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if(item.itemId == R.id.aboutMenu)
            showInfo()
        return true
    }
    @SuppressLint("StringFormatInvalid")
    private fun showInfo() {
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(current)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }
}