package io.github.jennas.studentcardocr

import android.os.Build
import android.widget.ProgressBar
import android.widget.TextView
import android.graphics.PorterDuff
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color


class ShowProgress(progressBar: ProgressBar, progressMessage: TextView) {
    var progressBar: ProgressBar? = null
    var progressMessage: TextView? = null

    init {
        this.progressBar = progressBar
        this.progressMessage = progressMessage
    }

    fun setProgress(progress: Int, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progressBar!!.progressDrawable.setColorFilter(
                BlendModeColorFilter(
                    Color.BLUE,
                    BlendMode.SRC_ATOP
                )
            )
        } else {
            progressBar!!.progressDrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP)
        }
        progressBar!!.progress = progress
        progressMessage!!.text = message
        progressMessage!!.setTextColor(Color.BLUE)
    }

    fun setError(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progressBar!!.progressDrawable.setColorFilter(
                BlendModeColorFilter(
                    Color.RED,
                    BlendMode.SRC_ATOP
                )
            )
        } else {
            progressBar!!.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
        }
        progressBar!!.progress = 100
        progressMessage!!.text = message
        progressMessage!!.setTextColor(Color.RED)
    }
}