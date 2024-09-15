package by.esas.tools.utils.logger

import android.util.Log
import android.widget.Toast
import by.esas.tools.App
import by.esas.tools.logger.ILogger

class LoggerImpl : ILogger<ErrorModel> {

    override var currentTag: String = "LoggerImpl"
    override fun setTag(tag: String) {
        this.currentTag = tag
    }

    override fun showMessage(msg: String, length: Int) {
        Toast.makeText(App.instance, msg, length).show()
    }

    override fun showMessage(msgRes: Int, length: Int) {
        showMessage(App.appContext.getString(msgRes), length)
    }

    override fun log(tag: String, msg: String, level: Int) {
        Log.w(currentTag, msg)
    }

    override fun logError(throwable: Throwable) {
        Log.e(currentTag, throwable.message, throwable)
    }

    override fun logError(msg: String) {
        Log.e(currentTag, msg)
    }

    override fun logInfo(msg: String) {
        Log.i(currentTag, msg)
    }

    override fun sendLogs(msg: String) {
    }

    override fun logError(error: ErrorModel) {
        Log.e(currentTag, error.status)
    }

    override fun logCategory(category: String, tag: String, msg: String) {
        Log.e(tag, "$category: $msg")
    }
}