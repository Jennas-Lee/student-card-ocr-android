package io.github.jennas.studentcardocr

import android.graphics.*
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.toColor
import android.graphics.PorterDuff

import android.R.color

import android.graphics.BlendMode

import android.graphics.BlendModeColorFilter




class ShowProgress(progressBar: ProgressBar, progressMessage: TextView) {
    var progressBar: ProgressBar? = null
    var progressMessage: TextView? = null

    init {
        this.progressBar = progressBar
        this.progressMessage = progressMessage
    }

    fun startProgress() {
        progressBar!!.visibility = View.VISIBLE
        progressMessage!!.visibility = View.VISIBLE
        progressMessage!!.text = "연결중"
    }

    fun setProgress(progress: Int, message: String) {
        progressBar!!.progress = progress
        progressMessage!!.text = message
    }

    fun setError(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progressBar!!.progressDrawable.setColorFilter(BlendModeColorFilter(Color.RED, BlendMode.SRC_ATOP))
        } else {
            progressBar!!.progressDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
        }
        progressBar!!.progress = 100
        progressMessage!!.text = message
        progressMessage!!.setTextColor(Color.RED)
    }
}