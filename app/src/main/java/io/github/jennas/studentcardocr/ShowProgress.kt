package io.github.jennas.studentcardocr

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView

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
        progressMessage!!.text = "준비"
    }

    fun setProgress(progress: Int, message: String) {
        progressBar!!.progress = progress
        progressMessage!!.text = message
    }
}