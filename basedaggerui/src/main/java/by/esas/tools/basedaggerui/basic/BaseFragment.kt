/*
 * Copyright 2021 Electronic Systems And Services Ltd.
 * SPDX-License-Identifier: Apache-2.0
 */

package by.esas.tools.basedaggerui.basic

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import by.esas.tools.basedaggerui.R
import by.esas.tools.basedaggerui.inject.factory.InjectingViewModelFactory
import by.esas.tools.dialog.BaseBottomDialogFragment
import by.esas.tools.dialog.BaseDialogFragment
import by.esas.tools.logger.Action
import by.esas.tools.logger.BaseErrorModel
import by.esas.tools.logger.BaseLoggerImpl
import by.esas.tools.logger.ILogger
import by.esas.tools.logger.handler.ErrorAction
import by.esas.tools.logger.handler.ErrorHandler
import by.esas.tools.logger.handler.ShowErrorType
import by.esas.tools.util.SwitchManager
import by.esas.tools.util.defocusAndHideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<E : Enum<E>, M : BaseErrorModel<E>> : DaggerFragment() {
    companion object {
        val TAG: String = BaseFragment::class.java.simpleName
    }

    @Inject
    lateinit var viewModelFactory: InjectingViewModelFactory

    protected open var logger: ILogger<E, *> = BaseLoggerImpl(TAG, null)
    protected open var hideKeyboardOnStop: Boolean = true
    protected open var switcher: SwitchManager = SwitchManager()
    protected var permissionsLauncher: ActivityResultLauncher<Array<String>>? = null
    protected var activityActionLauncher: ActivityResultLauncher<Action>? = null

    //region providing methods

    abstract fun provideLayoutId(): Int

    abstract fun provideSwitchableViews(): List<View?>

    abstract fun provideAppContext(): Context

    abstract fun provideErrorHandler(): ErrorHandler<E, M>

    protected open fun provideProgressBar(): View? = null

    protected open fun providePermissions(): Array<String> = emptyArray()

    protected open fun provideMaterialAlertDialogBuilder(): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AppTheme_CustomMaterialDialog
        ).setCancelable(false)
    }

    protected open fun providePermissionResultCallback(): ActivityResultCallback<Map<String, Boolean>> {
        return ActivityResultCallback<Map<String, Boolean>> { result ->
            logger.logOrder("ActivityResultCallback onActivityResult")
            if (result?.all { it.value } == true) {
                logger.logInfo("${result.toList().joinToString()} all granted")
            }
        }
    }

    //endregion

    //region fragment lifecycle methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.logOrder("onCreate")

        permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            providePermissionResultCallback()
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        logger.logOrder("onCreateView")
        return inflater.inflate(provideLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.logOrder("onViewCreated")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        logger.logOrder("onViewStateRestored")
    }

    override fun onStart() {
        super.onStart()
        logger.logOrder("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger.logOrder("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.logOrder("onPause")
    }


    override fun onStop() {
        super.onStop()
        logger.logOrder("onStop")
        if (hideKeyboardOnStop)
            hideKeyboard(activity)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        logger.logOrder("onSaveInstanceState")
    }

    //endregion  fragment lifecycle methods

    //region dialogs

    protected open fun onShowDialog(dialog: BaseDialogFragment<*, *>?) {
        logger.logInfo("try to showDialog ${dialog != null}")
        if (dialog != null) {
            onShowDialog(dialog, dialog.TAG)
        }
    }

    protected open fun onShowDialog(dialog: BaseBottomDialogFragment<*, *>?) {
        logger.logInfo("try to showDialog ${dialog != null}")
        if (dialog != null) {
            onShowDialog(dialog, dialog.TAG)
        }
    }

    protected open fun onShowDialog(dialog: DialogFragment, tag: String) {
        logger.logInfo("try to showDialog $tag")
        val prevWithSameTag: Fragment? = childFragmentManager.findFragmentByTag(tag)
        if (prevWithSameTag != null && prevWithSameTag is BaseDialogFragment<*, *>
            && prevWithSameTag.getDialog() != null && prevWithSameTag.getDialog()?.isShowing == true
            && !prevWithSameTag.isRemoving
        ) {
            //there is currently showed dialog fragment with same TAG
        } else {
            //there is no currently showed dialog fragment with same TAG
            dialog.show(childFragmentManager, tag)
        }
    }

    //endregion dialogs

    open fun handleError(action: ErrorAction<E, M>?) {
        logger.logOrder("handleError ${action != null} && !${action?.handled}")
        if (action != null && !action.handled) {
            val msg = when {
                action.throwable != null -> provideErrorHandler().getErrorMessage(action.throwable!!)
                action.model != null -> provideErrorHandler().getErrorMessage(action.model!!)
                else -> "Error"
            }
            action.handled = true

            showError(msg, action.showType, action.getSubAction())
        }
    }

    protected open fun showError(msg: String, showType: String, action: Action?) {
        hideProgress()
        when (showType) {
            ShowErrorType.SHOW_NOTHING.name -> enableControls()
            ShowErrorType.SHOW_ERROR_DIALOG.name -> {
                provideMaterialAlertDialogBuilder().setTitle(R.string.error_title)
                    .setMessage(msg)
                    .setPositiveButton(R.string.common_ok_btn) { dialogInterface, _ ->
                        dialogInterface?.dismiss()
                        if (action != null)
                            handleAction(action)
                        enableControls()
                    }.create().show()
            }
            ShowErrorType.SHOW_ERROR_MESSAGE.name -> {
                showMessage(msg)
                if (action != null)
                    handleAction(action)
                enableControls()
            }
        }
    }

    /**
     * Method handles action
     * @return Boolean (false in case if action was not handled by this method and true if it was handled)
     */
    open fun handleAction(action: Action): Boolean {
        logger.logInfo("default handleAction $action")
        when (action.name) {
            Action.ACTION_FINISH -> {
                handleFinishAction(action.parameters)
                action.handled = true
            }
            Action.ACTION_CHECK_PERMISSIONS -> {
                checkPermissions(providePermissions(), false)
                action.handled = true
            }
            Action.ACTION_CHECK_AND_REQUEST_PERMISSIONS -> {
                checkPermissions(providePermissions(), true)
                action.handled = true
            }
            Action.ACTION_ENABLE_CONTROLS -> {
                enableControls()
                hideProgress()
                action.handled = true
            }
            Action.ACTION_DISABLE_CONTROLS -> {
                showProgress()
                disableControls()
                action.handled = true
            }
            Action.ACTION_HIDE_KEYBOARD -> {
                hideKeyboard(requireActivity())
                action.handled = true
            }
            else -> {
                // return false in case if action was not handled by this method
                return false
            }
        }
        return true
    }

    protected open fun handleFinishAction(parameters: Bundle?) {
        activity?.finish()
    }

    protected open fun checkPermissions(permissions: Array<String>, request: Boolean): Boolean {
        val denied: MutableList<String> = mutableListOf()
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED)
                denied.add(permission)
        }
        logger.logInfo("All denied permissions: ${denied.joinToString()}")
        return if (denied.isNotEmpty() && request) {
            logger.logOrder("requestPermissions")
            permissionsLauncher?.launch(denied.toTypedArray())
            false
        } else {
            true
        }
    }

    //region helping methods

    protected open fun showMessage(text: String, duration: Int = Toast.LENGTH_SHORT) {
        logger.logInfo(text)
        logger.showMessage(text, duration)
    }

    protected open fun showMessage(textId: Int, duration: Int = Toast.LENGTH_SHORT) {
        logger.logInfo(provideAppContext().resources.getString(textId))
        logger.showMessage(textId, duration)
    }

    protected open fun enableControls() {
        logger.logInfo("enableControls")
        provideSwitchableViews().forEach { switchable ->
            if (switchable != null)
                switcher.enableView(switchable)
        }
    }

    protected open fun disableControls() {
        logger.logInfo("disableControls")
        provideSwitchableViews().forEach { switchable ->
            if (switchable != null)
                switcher.disableView(switchable)
        }
    }

    protected open fun hideSystemUi(activity: Activity?) {
        logger.logInfo("hideSystemUi")
        activity?.onWindowFocusChanged(true)
    }

    protected open fun hideKeyboard(activity: Activity?) {
        logger.logInfo("hideKeyboard")
        defocusAndHideKeyboard(activity)
    }

    protected open fun hideProgress() {
        logger.logInfo("hideProgress")
        provideProgressBar()?.visibility = View.INVISIBLE
    }

    protected open fun showProgress() {
        logger.logInfo("showProgress")
        provideProgressBar()?.visibility = View.VISIBLE
    }

    //endregion
}