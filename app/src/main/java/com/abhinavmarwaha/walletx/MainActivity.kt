package com.abhinavmarwaha.walletx

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhinavmarwaha.walletx.crypto.ImageCryptor.Companion.SHAsum
import com.abhinavmarwaha.walletx.onBoarding.AddLock
import com.abhinavmarwaha.walletx.db.room.*
import com.abhinavmarwaha.walletx.di.archModelModule
import com.abhinavmarwaha.walletx.lock.LockCallback
import com.abhinavmarwaha.walletx.lock.PatternLock
import com.abhinavmarwaha.walletx.models.Money
import com.abhinavmarwaha.walletx.models.globalState
import com.abhinavmarwaha.walletx.ui.*
import com.abhinavmarwaha.walletx.ui.compose.CardsView
import com.abhinavmarwaha.walletx.ui.compose.MoneyView
import com.abhinavmarwaha.walletx.ui.compose.NoteView
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.abhinavmarwaha.walletx.ui.theme.WalletXTheme
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import io.github.osipxd.datastore.encrypted.createEncrypted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.kodein.di.*
import org.kodein.di.android.closestDI
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


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

                    var sharedImage: Uri? = null
                    var sharedPDF: Uri? = null

                    // image/pdf from share
                    if (intent?.action == Intent.ACTION_SEND) {
                        if (intent.type?.startsWith("image/") == true) {
                            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                                sharedImage = it
                            }
                        } else if (intent.type?.compareTo("application/pdf") == 0) {
                            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
                                sharedPDF = it
                            }
                        }
                    }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { Home(navController, sharedImage, sharedPDF) }
                        composable("addCard/{id}") { navBackStackEntry ->
                            val id = navBackStackEntry.arguments?.getString("id")
                            if (id != null && id.all { Character.isDigit(it) })
                                AddCardView(navController, id.toLong(), null, null)
                            if (id != null) {
                                val decodedUri =
                                    URLDecoder.decode(id, StandardCharsets.UTF_8.toString())
                                if (id.compareTo(decodedUri) != 0)
                                    AddCardView(navController, null, Uri.parse(decodedUri), null)
                                else
                                    AddCardView(navController, null, null, id)
                            } else
                                AddCardView(navController, id?.toLong(), null, null)
                        }
                        composable("addCard") {
                            AddCardView(navController, null, null, null)
                        }
                        composable("allCards") { AllCards(navController) }
                        composable("allNotes") { AllNotes() }
                        composable("about") { About() }
                        composable("settings") { Settings(navController) }
                    }
                }
            }
        }
    }
}

private val PATTERN = stringPreferencesKey("pattern")

@OptIn(ExperimentalComposeUiApi::class, ExperimentalPermissionsApi::class)
@Composable
fun Home(navController: NavController, sharedImage: Uri?, sharedPDF: Uri?) {
    val context = LocalContext.current
    val di: DI by closestDI(LocalContext.current)
    val dataStore: DataStore<Preferences> by di.instance()
    val vm = HomeViewModel(dataStore)
    var correct by rememberSaveable {
        mutableStateOf(false)
    }
    val pattern: String? by vm.patternFlow.asLiveData().observeAsState()

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val readExternal = rememberPermissionState(android.Manifest.permission_group.STORAGE)
    val firstRun = remember { mutableStateOf(true) }

    if (pattern == null) {
        AddLock(navController, false)
    } else if (pattern!!.isEmpty()) Text("Loading")
    else {
        globalState.pattern = pattern
        if (!correct) {
            Box(modifier = Modifier.padding(paddingValues = PaddingValues(top = 50.dp))) {
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
        } else {
            val res = Column(
                Modifier
                    .fillMaxHeight()
                    .padding(20.dp)
                    .verticalScroll(state = ScrollState(0))
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Icon(
                        Icons.Filled.Info,
                        "About",
                        modifier = Modifier
                            .clickable { navController.navigate("about") }
                            .size(30.dp),
                        tint = DarkRed
                    )
                    Icon(
                        Icons.Filled.Settings,
                        "Settings",
                        modifier = Modifier
                            .clickable { navController.navigate("settings") }
                            .size(30.dp),
                        tint = DarkRed
                    )
                }
                MoneyView()
                CardsView(1, navController)
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
                if (showDialog)
                    AlertDialog(
                        properties = DialogProperties(
                            dismissOnBackPress = true,
                            dismissOnClickOutside = true
                        ),
                        onDismissRequest = {
                            setShowDialog(false)
                        },
                        title = {
                            Text("Grant")
                        },
                        text = {
                            Text("You need to grant Media permission for that")
                        },
                        confirmButton = {
                            Button(
                                onClick = {
//                                    setShowDialog(false)
                                    readExternal.launchPermissionRequest() // TODO Permission
                                    Log.e("PERMISSION", "ASKED")
                                },
                            ) {
                                Text("Confirm")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    setShowDialog(false)
                                },
                            ) {
                                Text("Nah")
                            }
                        },
                    )

            }
            if (sharedImage != null) {
                if (readExternal.status.shouldShowRationale) {
                    val encodedUrl =
                        URLEncoder.encode(sharedImage.toString(), StandardCharsets.UTF_8.toString())
                    navController.navigate("addCard/$encodedUrl")
                } else if (firstRun.value) {
                    setShowDialog(true)
                    firstRun.value = false
                }
            } else if (sharedPDF != null) {
                val descriptor = context.contentResolver.openFileDescriptor(sharedPDF, "r")
                val renderer = PdfRenderer(descriptor!!)
                val page: PdfRenderer.Page = renderer.openPage(0)
                val pageWidth = page.width
                val pageHeight = page.height
                val bitmap = Bitmap.createBitmap(
                    pageWidth,
                    pageHeight,
                    Bitmap.Config.ARGB_8888
                )
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
                val bitmapdata: ByteArray = bos.toByteArray()
                val hash = SHAsum(bitmapdata)
                val file = File(context.cacheDir, hash)
                file.writeBytes(bitmapdata)
                navController.navigate("addCard/$hash")
                page.close()
                renderer.close()
            }
            return res
        }
    }
}

class HomeViewModel constructor(private val dataStore: DataStore<Preferences>) : ViewModel() {
    val patternFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[PATTERN]
        }
}