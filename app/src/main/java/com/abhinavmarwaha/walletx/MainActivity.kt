package com.abhinavmarwaha.walletx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhinavmarwaha.walletx.OnBoarding.Addlock
import com.abhinavmarwaha.walletx.db.room.*
import com.abhinavmarwaha.walletx.di.archModelModule
import com.abhinavmarwaha.walletx.lock.LockCallback
import com.abhinavmarwaha.walletx.lock.PatternLock
import com.abhinavmarwaha.walletx.models.Money
import com.abhinavmarwaha.walletx.models.globalState
import com.abhinavmarwaha.walletx.ui.AddCardView
import com.abhinavmarwaha.walletx.ui.AllCards
import com.abhinavmarwaha.walletx.ui.AllNotes
import com.abhinavmarwaha.walletx.ui.compose.CardsView
import com.abhinavmarwaha.walletx.ui.compose.MoneyView
import com.abhinavmarwaha.walletx.ui.compose.NoteView
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.abhinavmarwaha.walletx.ui.theme.WalletXTheme
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import io.github.osipxd.datastore.encrypted.createEncrypted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.*
import org.kodein.di.android.closestDI
import java.io.File

class MainActivity : ComponentActivity(), DIAware {


    override val di by DI.lazy {
        val context = this@MainActivity

        bind<AppDatabase>() with singleton { AppDatabase.getInstance(context) }
        bind<CardDAO>() with singleton { instance<AppDatabase>().cardDao() }
        bind<CardGroupDAO>() with singleton { instance<AppDatabase>().cardGroupDao() }
        bind<CGRelationDao>() with singleton { instance<AppDatabase>().cgRelationDao() }
        bind<NotesDao>() with singleton { instance<AppDatabase>().notesDao() }

        AeadConfig.register()

        val aead = AndroidKeysetManager.Builder()
            .withSharedPref(context, "master_keyset", "master_key_preference")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)

        bind<DataStore<Preferences>>() with singleton {
            PreferenceDataStoreFactory.createEncrypted(aead) {
                File(context.filesDir, "datastore.preferences_pb")
            }
        }
        val dataStore: DataStore<Preferences> by instance()

        bind<Money>() with singleton { Money(dataStore) }

        import(archModelModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletXTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { Home(navController) }
                        composable("addCard") { AddCardView(navController) }
                        composable("allCards") { AllCards() }
                        composable("allNotes") { AllNotes() }
                    }
                }
            }
        }
    }
}

private val PATTERN = stringPreferencesKey("pattern")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Home(navController: NavController) {
    val di: DI by closestDI(LocalContext.current)
    val dataStore: DataStore<Preferences> by di.instance()
    val vm = HomeViewModel(dataStore)
    var correct by rememberSaveable {
        mutableStateOf(false)
    }
    val pattern: String? by vm.patternFlow.asLiveData().observeAsState()

    if (pattern == null) {
        Addlock()
    } else if (pattern!!.isEmpty()) Text("Loading")
    else {
        globalState.pattern = pattern
        if (!correct) {
            Box(modifier = Modifier.padding(paddingValues = PaddingValues(top = 50.dp))){
                PatternLock(
                    size = 400.dp,
                    key = ArrayList(pattern!!.toCharArray().map { it.digitToInt() }),
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
                            correct = isCorrect
                        }
                    }
                )
            }
        } else
            Column(
                Modifier
                    .fillMaxHeight()
                    .padding(20.dp)) {
                MoneyView()
                CardsView("main")
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    LongButton({ navController.navigate("allCards") }, "All", Modifier.padding())
                    Spacer(Modifier.size(10.dp))
                    SmallButton({
                        navController.navigate("addCard")
                    }, "Add", color = DarkRed, modifier = Modifier)
                }
                NoteView(1)
                LongButton(
                    { navController.navigate("allNotes") },
                    "All",
                    Modifier
                        .padding(vertical = 30.dp)
                        .align(Alignment.CenterHorizontally)
                )

            }
    }
}

class HomeViewModel constructor(private val dataStore: DataStore<Preferences>) : ViewModel() {
    val patternFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PATTERN]
        }
}