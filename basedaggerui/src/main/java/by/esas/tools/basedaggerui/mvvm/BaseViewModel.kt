package by.esas.tools.basedaggerui.mvvm

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.esas.tools.basedaggerui.R
import by.esas.tools.logger.BaseErrorModel
import by.esas.tools.logger.BaseVMLogger
import by.esas.tools.logger.ILogger
import by.esas.tools.logger.handler.ErrorData
import by.esas.tools.logger.handler.ShowErrorType
import by.esas.tools.util.SwitchManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

abstract class BaseViewModel<E : Enum<E>, M : BaseErrorModel<E>> : ViewModel() {
    open val TAG: String = BaseViewModel::class.java.simpleName

    open var logger: ILogger<E, M> = BaseVMLogger(TAG, null)

    var switchableViewsList: () -> List<View?> = { emptyList() }
    protected open var switcher: SwitchManager = SwitchManager()

    val progressing = ObservableBoolean(false)
    val errorData: MutableLiveData<ErrorData<E, M>> = MutableLiveData<ErrorData<E, M>>()

    open fun hideProgress() {
        progressing.set(false)
    }

    open fun showProgress() {
        progressing.set(true)
    }

    override fun onCleared() {
        super.onCleared()
        logger.log("onCleared")
    }

    /**
     * Блокируем редактирование полей.
     */
    open fun disableControls(): Boolean {
        showProgress()
        var result: Boolean = true
        switchableViewsList().forEach { view ->
            view?.let { result = switcher.disableView(it) && result }
        }
        return result
    }

    /**
     * Возвращаем возможность редактировать поля.
     */
    open fun enableControls(): Boolean {
        hideProgress()
        var result: Boolean = true
        switchableViewsList().forEach { view ->
            view?.let { result = switcher.enableView(it) && result }
        }
        return result
    }

    open fun handleError(
        error: Throwable,
        showType: ShowErrorType = ShowErrorType.SHOW_ERROR_DIALOG,
        doOnDialogOK: () -> Unit = {}
    ) {
        errorData.postValue(ErrorData(throwable = error, showType = showType, doOnDialogOK = doOnDialogOK))
    }

    open fun handleError(
        error: M,
        showType: ShowErrorType = ShowErrorType.SHOW_ERROR_DIALOG,
        doOnDialogOK: () -> Unit = {}
    ) {
        errorData.postValue(ErrorData(model = error, showType = showType, doOnDialogOK = doOnDialogOK))
    }

    fun showError(msg: String, showType: ShowErrorType, alertDialogBuilder: MaterialAlertDialogBuilder? = null, doOnDialogOK: () -> Unit) {
        hideProgress()
        /*if (error.statusEnum == AppErrorStatusEnum.NET_SSL_HANDSHAKE) {
            logger.logError(SSLContext.getDefault().defaultSSLParameters.protocols?.contentToString() ?: "SSL protocols: Empty")
        }*/
        when (showType) {
            ShowErrorType.SHOW_NOTHING -> enableControls()
            ShowErrorType.SHOW_ERROR_DIALOG -> {
                alertDialogBuilder?.setTitle(R.string.error_title)
                    ?.setMessage(msg)
                    ?.setPositiveButton(R.string.common_ok_btn) { dialogInterface, _ ->
                        dialogInterface?.dismiss()
                        enableControls()
                        doOnDialogOK()
                    }?.create()?.show()
            }
            ShowErrorType.SHOW_ERROR_MESSAGE -> {
                logger.showMessage(msg)
                enableControls()
                doOnDialogOK()
            }
        }
    }
}