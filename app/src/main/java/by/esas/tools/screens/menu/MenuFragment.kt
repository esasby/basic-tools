package by.esas.tools.screens.menu

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.esas.tools.R
import by.esas.tools.base.AppFragment
import by.esas.tools.databinding.FragmentMenuBinding
import by.esas.tools.inputfieldview.InputFieldView
import by.esas.tools.screens.menu.recycler.CaseAdapter
import by.esas.tools.util.defocusAndHideKeyboard

class MenuFragment : AppFragment<MenuVM, FragmentMenuBinding>() {

    override val fragmentDestinationId: Int = R.id.menuFragment

    override fun provideLayoutId(): Int {
        return R.layout.fragment_menu
    }

    override fun provideViewModel(): MenuVM {
        return ViewModelProvider(this, viewModelFactory.provideFactory())[MenuVM::class.java]
    }

    val caseAdapter = CaseAdapter(
        onClick = { item ->
            logger.logInfo("${item.name} clicked")
//            item.direction?.let { navigate(it) }
            navController?.navigate(item.id)
        }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCaseRecycler()
        setupSearchView()
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.casesListLive.observe(viewLifecycleOwner){ list ->
            caseAdapter.setItems(list)
        }
    }

    private fun setupSearchView() {
        binding.fMenuCasesSearch.apply {
            setupEditorActionListener(object : InputFieldView.EditorActionListener {
                override fun onActionClick() {
                    defocusAndHideKeyboard(activity)
                    viewModel.onSearchChanged(viewModel.search)
                }
            })
            setStartIconClickListener(object : InputFieldView.IconClickListener{
                override fun onIconClick() {
                    viewModel.onSearchChanged(viewModel.search)
                }
            })
            setEndIconClickListener(object : InputFieldView.IconClickListener{
                override fun onIconClick() {
                    viewModel.clearSearch()
                }
            })
        }
    }

    private fun setupCaseRecycler(){
        binding.fMenuRecycler.apply {
            adapter = caseAdapter
            layoutManager = LinearLayoutManager(this@MenuFragment.requireContext())
        }
        binding.fMenuRecycler.hasFixedSize()
    }

//    private fun setCases() {
//        viewModel.allCases.clear()
//        addCaseItem(
//            R.id.loggerFragment,
//            R.string.case_label_logger,
//            listOf(Modules.LOGGER),
//            MenuFragmentDirections.actionMenuFragmentToLoggerFragment()
//        )
//        addCaseItem(
//            R.id.timeparserFragment,
//            R.string.case_label_timeparser,
//            listOf(Modules.TIMEPARSER),
//            MenuFragmentDirections.actionMenuFragmentToTimeParserFragment()
//        )
//        addCaseItem(
//            R.id.biometricDecryptionFragment,
//            R.string.case_label_biometric_decryption,
//            listOf(Modules.BIOMETRIC_DECRYPTION),
//            MenuFragmentDirections.actionMenuFragmentToBiometricDecryptionFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_domain,
//            listOf(Modules.DOMAIN),
//            MenuFragmentDirections.actionMenuFragmentToDomainCaseFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_baseui_theme,
//            listOf(Modules.BASE_UI),
//            MenuFragmentDirections.actionMenuFragmentToBaseuiThemeFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_baseui,
//            listOf(Modules.BASE_UI),
//            MenuFragmentDirections.actionMenuFragmentToBaseuiFunctionalityFragment()
//        )
//        addCaseItem(
//            R.id.baseuiNavigationFragment,
//            R.string.case_label_baseui_navigation,
//            listOf(Modules.BASE_UI),
//            MenuFragmentDirections.actionMenuFragmentToBaseuiNavigationFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_bottom_dialog,
//            listOf(Modules.DIALOG, Modules.INPUTFIELD_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToBottomDialogFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_dynamic_message_dialog,
//            listOf(Modules.DIALOG),
//            MenuFragmentDirections.actionMenuFragmentToDynamicMessageDialogFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_inputfield_view,
//            listOf(Modules.INPUTFIELD_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToInputfieldViewFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_inputfield_view_start_icon,
//            listOf(Modules.INPUTFIELD_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToInputfieldViewStartIconFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_inputfield_view_end_icon,
//            listOf(Modules.INPUTFIELD_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToInputfieldViewEndIconFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_checker,
//            listOf(Modules.CHECKER, Modules.INPUTFIELD_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToCheckerFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_pin_view,
//            listOf(Modules.PIN_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToPinViewFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_dynamic_pin_view,
//            listOf(Modules.PIN_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToDynamicPinViewFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_saved_state,
//            listOf(Modules.BASE_DAGGER_UI, Modules.BASE_UI),
//            MenuFragmentDirections.actionMenuFragmentToSavedStateFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_numpad_image,
//            listOf(Modules.NUMPAD),
//            MenuFragmentDirections.actionMenuFragmentToNumpadImageFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_numpad_text,
//            listOf(Modules.NUMPAD),
//            MenuFragmentDirections.actionMenuFragmentToNumpadTextFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_util_keyboard,
//            listOf(Modules.UTIL),
//            MenuFragmentDirections.actionMenuFragmentToUtilKeyboardFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_utils,
//            listOf(Modules.UTIL),
//            MenuFragmentDirections.actionMenuFragmentToUtilUtilsFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_switch_manager,
//            listOf(Modules.UTIL, Modules.CUSTOMSWITCH),
//            MenuFragmentDirections.actionMenuFragmentToUtilSwitchManagerFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_custom_switch,
//            listOf(Modules.CUSTOMSWITCH),
//            MenuFragmentDirections.actionMenuFragmentToCustomSwitchFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_dynamic_custom_switch,
//            listOf(Modules.CUSTOMSWITCH),
//            MenuFragmentDirections.actionMenuFragmentToCustomSwitchProgramFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_listheader,
//            listOf(Modules.LISTHEADER, Modules.CUSTOMSWITCH),
//            MenuFragmentDirections.actionMenuFragmentToListheaderFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_dynamic_listheader,
//            listOf(Modules.LISTHEADER),
//            MenuFragmentDirections.actionMenuFragmentToDynamicListheaderFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_topbar_view,
//            listOf(Modules.TOPBAR_VIEW),
//            MenuFragmentDirections.actionMenuFragmentToTopbarFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_cardline,
//            listOf(Modules.CARDLINE),
//            MenuFragmentDirections.actionMenuFragmentToCardlineFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_dynamic_cardline,
//            listOf(Modules.CARDLINE),
//            MenuFragmentDirections.actionMenuFragmentToDynamicCardlineFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_recycler_base,
//            listOf(Modules.RECYCLER),
//            MenuFragmentDirections.actionMenuFragmentToRecyclerFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_recycler_sticky,
//            listOf(Modules.RECYCLER),
//            MenuFragmentDirections.actionMenuFragmentToStickyCaseFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_recycler_simple,
//            listOf(Modules.RECYCLER),
//            MenuFragmentDirections.actionMenuFragmentToSimpleRecyclerFragment()
//        )
//        addCaseItem(
//            -1,
//            R.string.case_label_recycler_custom,
//            listOf(Modules.RECYCLER),
//            MenuFragmentDirections.actionMenuFragmentToCustomRecyclerFragment()
//        )
//        viewModel.updateAdapter(viewModel.allCases)
//    }

}