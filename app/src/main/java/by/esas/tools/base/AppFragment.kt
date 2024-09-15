package by.esas.tools.base

import android.content.Context
import android.view.View
import androidx.databinding.ViewDataBinding
import by.esas.tools.App
import by.esas.tools.BR
import by.esas.tools.basedaggerui.factory.InjectingViewModelFactory
import by.esas.tools.baseui.standard.StandardFragment
import by.esas.tools.checker.Checker
import by.esas.tools.checker.Checking
import by.esas.tools.logger.BaseLoggerImpl
import by.esas.tools.logger.ILogger
import by.esas.tools.logger.handler.ErrorMessageHelper
import by.esas.tools.utils.logger.ErrorModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class AppFragment<VM : AppVM, B : ViewDataBinding> :
    StandardFragment<VM, B, ErrorModel>(),
    HasAndroidInjector {

    @Inject
    lateinit var viewModelFactory: InjectingViewModelFactory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any?>

    override fun androidInjector(): AndroidInjector<Any?>? {
        return androidInjector
    }

    override var logger: ILogger<*> = BaseLoggerImpl(context = provideAppContext())

    override fun provideAppContext(): Context {
        return App.appContext
    }

    override fun provideVariableInd(): Int {
        return BR.viewModel
    }

    override fun provideErrorStringHelper(): ErrorMessageHelper<ErrorModel> {
        logger.logOrder("provideErrorStringHelper")
        return object : ErrorMessageHelper<ErrorModel> {

            override fun getErrorMessage(error: ErrorModel): String {
                return error.status
            }
        }
    }

    override fun provideChecks(): List<Checking> {
        logger.logOrder("provideChecks")
        return emptyList()
    }

    override fun provideChecker(): Checker? {
        logger.logOrder("provideChecker")
        return null
    }

    override fun provideSwitchableViews(): List<View?> {
        logger.logOrder("provideSwitchableViews")
        return emptyList()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}