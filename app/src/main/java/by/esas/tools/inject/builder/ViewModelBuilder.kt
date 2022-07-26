package by.esas.tools.inject.builder

import androidx.lifecycle.ViewModel
import by.esas.tools.basedaggerui.factory.AssistedSavedStateViewModelFactory
import by.esas.tools.basedaggerui.qualifier.ViewModelKey
import by.esas.tools.screens.MainVM
import by.esas.tools.screens.menu.MenuVM
import by.esas.tools.screens.numpad.NumpadImageVM
import by.esas.tools.screens.pin_view.PinViewVM
import by.esas.tools.screens.saved_state_vm.SavedStateVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelBuilder {

    @Binds
    @IntoMap
    @ViewModelKey(MainVM::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MenuVM::class)
    abstract fun bindMenuViewModel(menuViewModel: MenuVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PinViewVM::class)
    abstract fun bindPinViewVM(pinViewVM: PinViewVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NumpadImageVM::class)
    abstract fun bindNumpadImageVM(numpadImageVM: NumpadImageVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SavedStateVM::class)
    abstract fun bindVMFactory(f: SavedStateVM.Factory): AssistedSavedStateViewModelFactory<out ViewModel>
}
