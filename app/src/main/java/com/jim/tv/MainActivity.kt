package com.jim.tv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.jim.tv.databinding.ActivityMainBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<VideoViewModel>()

    private var dialog: AlertDialog? = null
    private lateinit var player: YouTubePlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).let {
            binding = it
            setContentView(binding.root)
        }

        initPlayerFunction()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.youtubePlayerView.release()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showMenuDialog()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun initPlayerFunction() {
        viewModel.nowSelectedID.observe(this) {
            menuButtonClickFunction()
        }

        binding.youtubePlayerView.enterFullScreen()

        lifecycle.addObserver(binding.youtubePlayerView)
        binding.youtubePlayerView.addYouTubePlayerListener(addYouTubePlayerListener)
    }

    private fun showMenuDialog() {
        if (dialog == null) {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_menu, null)
            val contentView = view.findViewById<LinearLayout>(R.id.main_view)
            val nameArray = resources.getStringArray(R.array.video_name_arrays)
            val idArray = resources.getStringArray(R.array.video_id_arrays)

            for (i in nameArray.indices) {
                val button = Button(this)
                button.text = nameArray[i]
                button.setOnClickListener {
                    viewModel.nowSelectedID.value = idArray[i]
                }
                contentView.addView(button)
            }

            dialog = AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton(
                    "關閉App"
                ) { dialog, _ ->
                    dialog?.dismiss()
                    exitProcess(0)
                }
                .setNegativeButton(
                    "取消"
                ) { dialog, _ -> dialog?.dismiss() }.create()
        }

        if (dialog!!.isShowing) {
            dialog!!.dismiss()
        }
        dialog!!.show()
    }

    private fun menuButtonClickFunction() {
        player.loadVideo(viewModel.nowSelectedID.value.toString(), 0f)

        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    private val addYouTubePlayerListener = object : AbstractYouTubePlayerListener() {

        override fun onReady(youTubePlayer: YouTubePlayer) {
            super.onReady(youTubePlayer)

            player = youTubePlayer
            viewModel.nowSelectedID.value = resources.getStringArray(R.array.video_id_arrays)[0]
        }
    }
}