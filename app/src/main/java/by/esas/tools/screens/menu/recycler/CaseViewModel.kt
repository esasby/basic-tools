package by.esas.tools.screens.menu.recycler

import androidx.databinding.ObservableField
import by.esas.tools.entity.CaseItemInfo
import by.esas.tools.recycler.BaseItemViewModel

class CaseViewModel: BaseItemViewModel<CaseItemInfo>() {
    val caseName = ObservableField<String>("")
    val caseModules = ObservableField<String>("")

    override fun bindItem(item: CaseItemInfo, position: Int) {
        this.position.set(position)
        caseName.set(item.name)
        caseModules.set(item.modules.joinToString().lowercase())
    }
}