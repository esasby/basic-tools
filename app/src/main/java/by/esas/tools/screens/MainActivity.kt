package by.esas.tools.screens

import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import by.esas.tools.R
import by.esas.tools.base.AppActivity
import by.esas.tools.baseui.Config.ERROR_MESSAGE_DIALOG
import by.esas.tools.databinding.ActivityMainBinding
import by.esas.tools.dialog_core.Config
import by.esas.tools.dialog_message.MessageDialog
import by.esas.tools.logger.Action
import by.esas.tools.screens.menu.MenuFragment
import by.esas.tools.topbarview.ITopbarHandler

/**
 * To use HasAndroidInjector with Activity do not forget to add
 *     AndroidInjection.inject(this)
 * in the fun onCreate(savedInstanceState: Bundle?)
 *
 * To use HasAndroidInjector with Fragment do not forget to add
 *     override fun onAttach(context: Context) {
 *         AndroidSupportInjection.inject(this)
 *         super.onAttach(context)
 *     }
 */
class MainActivity : AppActivity<MainVM, ActivityMainBinding>() {

    companion object {

        const val NEED_TO_UPDATE_MENU: String = "NEED_TO_UPDATE_MENU"
        const val NEED_TO_UPDATE_CURRENT_CASE: String = "NEED_TO_UPDATE_CURRENT_CASE"
        const val CURRENT_CASE_ID: String = "CURRENT_CASE_ID"
    }

    lateinit var navController: NavController
    lateinit var popupMenu: PopupMenu
    private var topDestination = R.id.menuFragment

    override fun provideViewModel(): MainVM {
        return ViewModelProvider(this, viewModelFactory.provideFactory()).get(MainVM::class.java)
    }

    override fun provideLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun provideRequestKeys(): List<String> {
        return listOf(ERROR_MESSAGE_DIALOG, MainVM.CASE_STATUS_DIALOG, MainVM.CLEAR_CASES_TEST_DATA_DIALOG)
    }

    override fun provideFragmentResultListener(requestKey: String): FragmentResultListener? {
        return when (requestKey) {
            //REMEMBER we can register result listener for CLEAR_CASES_TEST_DATA_DIALOG
            // in MenuFragment so it will receive the result right away
            MainVM.CASE_STATUS_DIALOG -> {
                FragmentResultListener { key, result ->
                    val actionName = result.getString(Config.DIALOG_USER_ACTION)
                    result.putString(MainVM.DIALOG_KEY, key)
                    if (!actionName.isNullOrBlank()) {
                        viewModel.handleAction(Action(actionName, result))
                    } else {
                        viewModel.enableControls()
                    }
                }
            }
            MainVM.CLEAR_CASES_TEST_DATA_DIALOG -> {
                FragmentResultListener { _, result ->
                    val actionName = result.getString(Config.DIALOG_USER_ACTION)
                    if (actionName == MessageDialog.USER_ACTION_POSITIVE_CLICKED)
                        supportFragmentManager.setFragmentResult(
                            MenuFragment.MENU_UPDATE,
                            bundleOf(Pair(MenuFragment.MENU_UPDATE, MenuFragment.MENU_UPDATE_KEY_CLEAR))
                        )
                }
            }
            else -> {
                super.provideFragmentResultListener(requestKey)
            }
        }
    }

    override fun handleAction(action: Action): Boolean {
        when (action.name) {
            NEED_TO_UPDATE_MENU -> {
                val bundle = Bundle()
                bundle.putBoolean(NEED_TO_UPDATE_MENU, true)
                intent.putExtras(bundle)
            }
            NEED_TO_UPDATE_CURRENT_CASE -> {
                val newId = action.parameters?.getInt(CURRENT_CASE_ID) ?: -1
                viewModel.currentCaseId = newId
            }
            else -> return super.handleAction(action)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()
        setupSettingsMenu()

        binding.aMainTopBar.setupHandler(object : ITopbarHandler {
            override fun onNavigationClick() {
                onBackPressed()
            }

            override fun onActionClick() {
                navController.currentDestination?.let {
                    if (it.id == topDestination)
                        popupMenu.show()
                    else
                        viewModel.openCaseStatusDialog(it.label.toString())
                }
            }
        })
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.updateMenuLive.observe(this) {
            handleAction(Action(NEED_TO_UPDATE_MENU))
        }
    }

    override fun handleTouchOutOfInputField(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN && navController.currentDestination?.id != R.id.utilKeyboardFragment) {
            super.handleTouchOutOfInputField(event)
        }
    }

    private fun setupNavigation() {
        navController = Navigation.findNavController(this, R.id.a_main_nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == topDestination) viewModel.currentCaseId = -1
            viewModel.hasBackBtn.set(destination.id != topDestination)
            viewModel.hasSettingsBtn.set(destination.id == topDestination)
            viewModel.title.set(destination.label.toString())
            binding.aMainTopBar.setEndActionViewVisibility(viewModel.currentCaseId != -1)
        }
    }

    private fun setupSettingsMenu() {
        popupMenu = PopupMenu(this, binding.aMainTopBar, Gravity.END)
        popupMenu.inflate(R.menu.settings_menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            return@setOnMenuItemClickListener when (menuItem.itemId) {
                R.id.menuUiSettings -> {
                    navController.navigate(R.id.baseuiThemeFragment)
                    true
                }
                R.id.menuUpdateTest -> {
                    viewModel.openClearCasesTestDataDialog()
                    true
                }
                else -> false
            }
        }
    }
}
