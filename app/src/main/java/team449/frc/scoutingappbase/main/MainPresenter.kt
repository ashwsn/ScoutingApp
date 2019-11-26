package team449.frc.scoutingappbase.main

import android.os.AsyncTask
import androidx.navigation.Navigation.findNavController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import team449.frc.scoutingappbase.R
import team449.frc.scoutingappbase.helpers.editDialog
import team449.frc.scoutingappbase.helpers.info
import team449.frc.scoutingappbase.helpers.submitMatch
import team449.frc.scoutingappbase.managers.BluetoothManager
import team449.frc.scoutingappbase.managers.DataManager
import team449.frc.scoutingappbase.model.MatchShadow


interface Editor {
    fun edit(id: Long)
}

class MainPresenter(private val mainActivity: MainActivity): Editor {

    fun globalHelp() {
        help(R.string.help_global)
    }

    fun bluetooth() {
        GlobalScope.launch {
            BluetoothManager.connect("essuomelpmap")
        }
    }

    fun edit() {
        editDialog(mainActivity, DataManager.matchNames, this)
    }

    override fun edit(id: Long) {
        val mVM = mainActivity.matchViewModel
        DataManager.retrieveMatch(id)?.let {
            DataManager.stashCurrent(MatchShadow(mVM))
            mVM.load(it)
        }
    }

    fun sync() {
        GlobalScope.launch {
            BluetoothManager.write("test")
        }
    }

    fun clearData() {
        AsyncTask.execute{ DataManager.clear() }
    }

    fun settings() {
        findNavController(mainActivity, R.id.navhost).navigate(R.id.action_mainContainerFragment_to_altFragment)
    }

    fun submit() {
        submitMatch(mainActivity.matchViewModel, Runnable{ postSubmit() })
    }

    private fun postSubmit() {
        DataManager.recoverMatch()?.let { mainActivity.matchViewModel.load(it) } ?: mainActivity.matchViewModel.reset()
        mainActivity.moveToPrematch()
    }

    fun help(messageId: Int) {
        info(mainActivity, mainActivity.getString(R.string.help_title), mainActivity.getString(messageId))
    }

    fun onWindowFocusChange() {
        mainActivity.hideNav()
    }

    fun onBackPressed() {}
}