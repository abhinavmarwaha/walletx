package com.abhinavmarwaha.walletx


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhinavmarwaha.walletx.db.room.*
import com.abhinavmarwaha.walletx.di.archModelModule
import com.abhinavmarwaha.walletx.lock.LockedActivity
import com.abhinavmarwaha.walletx.models.Money
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
import org.kodein.di.*
import java.io.File

class MainActivity : ComponentActivity(), DIAware {


    override val di by DI.lazy {
        val context = this@MainActivity

        bind<AppDatabase>() with singleton { AppDatabase.getInstance(context) }
        bind<CardDAO>() with singleton { instance<AppDatabase>().cardDao() }
        bind<CardGroupDAO>() with singleton { instance<AppDatabase>().cardGroupDao() }
        bind<CGRelationDao>() with singleton { instance<AppDatabase>().cgRelationDao() }
        bind<NotesDao>() with singleton { instance<AppDatabase>().notesDao() }

        AeadConfig.register();

        val aead = AndroidKeysetManager.Builder()
            .withSharedPref(context, "master_keyset", "master_key_preference")
            .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri("android-keystore://master_key")
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)

        val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.createEncrypted(aead) {
            File(context.filesDir, "datastore.preferences_pb")
        }

        bind<Money>() with singleton { Money(dataStore) }

        import(archModelModule)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WalletXTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { Home(navController) }
                        composable("addFeed") { AddCardView(navController) }
                        composable("allCards") { AllCards() }
                        composable("allNotes") { AllNotes() }
                    }
                }
            }
        }
    }
}

@Composable
fun Home(navController: NavController) {

    var firtsTime = remember { mutableStateOf(true) }
    if (firtsTime.value) {
        LocalContext.current.startActivity(Intent(LocalContext.current, LockedActivity::class.java))
        firtsTime.value = false
    }

    Column(Modifier.fillMaxHeight()) {
        MoneyView()
        CardsView("main")
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 30.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            LongButton({ navController.navigate("allCards") }, "All")
            Spacer(Modifier.size(10.dp))
            SmallButton({
                navController.navigate("addFeed") {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, "Add", color = DarkRed)
        }
        NoteView(1)
        Box(Modifier.padding(vertical = 30.dp)) {
            LongButton({ navController.navigate("allNotes") }, "All")
        }
    }
}
