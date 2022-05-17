package com.abhinavmarwaha.walletx.onBoarding

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.abhinavmarwaha.walletx.db.room.AppDatabase
import com.abhinavmarwaha.walletx.lock.LockCallback
import com.abhinavmarwaha.walletx.lock.PatternLock
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import kotlin.collections.ArrayList

@ExperimentalComposeUiApi
@Composable
fun AddLock(navController: NavController, editing: Boolean) {
    val context = LocalContext.current
    val di: DI by closestDI(LocalContext.current)
    val dataStore: DataStore<Preferences> by di.instance()
    val vm = AddLockVM(context,dataStore, navController, editing)
    var first = remember {
        mutableListOf<Int>()
    }
    val title = remember {
        mutableStateOf("Add Pattern Lock")
    }
    val addNo = remember {
        mutableStateOf(0)
    }

    Column() {
        Text(
            title.value,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 50.dp),
            textAlign = TextAlign.Center
        )
        PatternLock(
            size = 400.dp,
            key = arrayListOf(0, 1, 2),
            dotColor = Color.White,
            dotRadius = 18f,
            lineColor = Color.White,
            lineStroke = 12f,
            callback = object : LockCallback {
                override fun onStart() {
                }

                override fun onProgress(index: Int) {
                }

                override fun onEnd(result: ArrayList<Int>, isCorrect: Boolean) {
                    if (addNo.value == 0) {
                        first.addAll(result)
                        addNo.value++
                        title.value = "Pattern Lock Confirm"
                        Toast.makeText(context, "Pattern Again", Toast.LENGTH_SHORT).show()
                    } else if (addNo.value == 1) {
                        val firstString = first.joinToString(separator = "") { it.toString() }
                        val resultString = result.joinToString(separator = "") { it.toString() }
                        if (firstString.compareTo(resultString) == 0) {
                            vm.completeOnBoarding(result)
                        } else {
                            first.clear()
                            addNo.value = 0
                            title.value = "Add Pattern Lock"
                            Toast.makeText(context, "Wrong, add new pattern", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

                }
            }
        )
    }


}

private val PATTERN = stringPreferencesKey("pattern")

class AddLockVM(
    private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val navController: NavController,
    private val editing: Boolean
) : ViewModel() {
    fun completeOnBoarding(pattern: ArrayList<Int>) {
        viewModelScope.launch {
            dataStore.edit { store ->
                val newPassword = pattern.joinToString(separator = "") { it.toString() }
                store[PATTERN] = newPassword
                if(editing) {
                    AppDatabase.getInstance(context).query("PRAGMA rekey = '$newPassword';", emptyArray()) // TODO test
                    navController.popBackStack()
                }
                Toast.makeText(context, "Pattern Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
}