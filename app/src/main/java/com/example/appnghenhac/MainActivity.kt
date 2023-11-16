package com.example.appnghenhac

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var textNameSong : TextView
    lateinit var textNameSinger : TextView
    lateinit var imageButtonPlay : ImageButton
    lateinit var imageButtonPause : ImageButton
    lateinit var imageButtonViewLyrics : ImageButton
    lateinit var seekBar: SeekBar
    lateinit var elapsedTime : TextView
    lateinit var remainingTime : TextView
    lateinit var textLyrics : TextView
    val handler = Handler()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textNameSong = findViewById(R.id.textViewNameSong)
        textNameSinger = findViewById(R.id.textViewNameSinger)
        imageButtonPlay = findViewById(R.id.imageButtonPlay)
        imageButtonPause = findViewById(R.id.imageButtonPause)
        imageButtonViewLyrics = findViewById(R.id.imageButtonViewLyrics)
        seekBar = findViewById(R.id.seekBar)
        elapsedTime = findViewById(R.id.elapsedTimeView)
        remainingTime = findViewById(R.id.totalDurationView)
        textLyrics = findViewById(R.id.textViewLyrics)

        val resourceId: Int = resources.getIdentifier("chaulenba", "raw", packageName)
        mediaPlayer = MediaPlayer.create(this, resourceId)



        val lyricsText = readLyricsFromFile()
        textLyrics.text = lyricsText


        imageButtonPlay.setOnClickListener {
            if (mediaPlayer != null && !mediaPlayer.isPlaying) {
                try {
                    mediaPlayer.start()
                } catch (e: IllegalStateException) {
                    // Xử lý lỗi khi start MediaPlayer
                    e.printStackTrace()
                }
            }
        }

        imageButtonPause.setOnClickListener {
            if (mediaPlayer != null && mediaPlayer.isPlaying) {
                try {
                    mediaPlayer.pause()
                } catch (e: IllegalStateException) {
                    // Xử lý lỗi khi pause MediaPlayer
                    e.printStackTrace()
                }
            }
        }


        var isLyricsVisible = false

        imageButtonViewLyrics.setOnClickListener {

            isLyricsVisible = !isLyricsVisible

            if (isLyricsVisible) {
                textLyrics.visibility = View.VISIBLE
            } else {
                textLyrics.visibility = View.GONE
            }
        }
        seekBar.max = mediaPlayer.duration

        // Cập nhật tiến trình của SeekBar theo thời gian
        updateSeekBar()

        // Bắt sự kiện thay đổi tiến trình của SeekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Nếu tiến trình được thay đổi bởi người dùng, chuyển đến vị trí mới của MediaPlayer
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Không cần xử lý
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Không cần xử lý
            }
        })
    }

    private fun updateSeekBar() {
        // Cập nhật tiến trình của SeekBar mỗi giây
        handler.postDelayed(object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                updateTextViews()
                handler.postDelayed(this, 1000) // Cập nhật mỗi giây
            }
        }, 0)
    }

    private fun updateTextViews() {
        val elapsedTimeString = millisecondsToTime(mediaPlayer.currentPosition.toLong())
        val remainingTimeString = millisecondsToTime((mediaPlayer.duration - mediaPlayer.currentPosition).toLong())

        elapsedTime.text = elapsedTimeString
        remainingTime.text = "-$remainingTimeString"
    }

    private fun millisecondsToTime(milliseconds: Long): String {
        val format = SimpleDateFormat("mm:ss", Locale.getDefault())
        return format.format(Date(milliseconds))
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }



    fun readLyricsFromFile(): String {
        val inputStream = resources.openRawResource(R.raw.lyrics)
        return inputStream.bufferedReader().use { it.readText() }
    }

}