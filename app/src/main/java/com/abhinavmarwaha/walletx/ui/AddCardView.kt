package com.abhinavmarwaha.walletx.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.abhinavmarwaha.walletx.archmodel.CardGroupsStore
import com.abhinavmarwaha.walletx.crypto.ImageCryptor
import com.abhinavmarwaha.walletx.db.room.*
import com.abhinavmarwaha.walletx.models.globalState
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.abhinavmarwaha.walletx.ui.theme.LightRed
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import java.io.File
import java.io.InputStream

@OptIn(ExperimentalUnitApi::class)
@Composable
fun AddCardView(navController: NavController, id: Long?, uri: Uri? = null, hash: String?) {

    val textState = rememberSaveable { mutableStateOf("") }
    val di: DI by closestDI(LocalContext.current)
    val cardGroupsStore: CardGroupsStore by di.instance()
    val cardDAO: CardDAO by di.instance()
    val cgRelationDao: CGRelationDao by di.instance()
    val groups = cardGroupsStore.getCardGroups().collectAsState(listOf())

    val context = LocalContext.current

    val camBitmap = remember { mutableStateOf<Bitmap?>(null) }

    val newEditImage = remember {
        mutableStateOf(false)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            camBitmap.value = it
        }

    val coroutineScope = rememberCoroutineScope()

    val selectedGroups = remember { mutableStateListOf<Long>() }
    val card = remember { mutableStateOf<Card?>(null) }
    if (id != null) {
        val vm = EditCardViewModel(cardDAO, id, card) {
            try {
                textState.value = card.value!!.title
                if (!newEditImage.value) {
                    val byteArray =
                        ImageCryptor(globalState.pattern!!).decryptBitmap(
                            card.value!!.image,
                            context
                        )
                    camBitmap.value = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    } else if (hash != null) {
        LaunchedEffect(Unit){
            // TODO UI getting Refreshed continuously
//            if (!newEditImage.value) {
                val file = File(context.cacheDir, hash)
                val byteArray = file.readBytes()
                camBitmap.value = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//            }
        }
    }

    if (uri != null) {
        val stream: InputStream = context.contentResolver.openInputStream(uri)!!
        camBitmap.value = BitmapFactory.decodeStream(stream);
    }

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (id != null) SmallButton(function = {
            val result = coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    cardDAO.deleteCards(listOf(id))
                }
            }
            result.let {
                navController.popBackStack()
            }
        }, text = "Delete", color = DarkRed, modifier = Modifier)
        Spacer(Modifier.size(20.dp))
        SmallButton(function = {
            if (selectedGroups.size == 0) {
                Toast.makeText(
                    context,
                    "Select atleast one group",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val card = Card()
                if (id != null) {
                    card.id = id
                }
                card.title = textState.value
//                val path = MediaUtils.getRealPathFromURI_API19(context,imageData.value)
                val cryptedFile =
                    ImageCryptor(globalState.pattern!!).encryptBitmap(camBitmap.value!!, context)
                card.image = cryptedFile

                coroutineScope.launch {
                    val selected = mutableListOf<CardGroupRelation>()
                    val result = withContext(Dispatchers.IO) {
                        val id = cardDAO.upsertCard(card)
                        card.id = id
                        selectedGroups.forEach {
                            selected.add(CardGroupRelation(guid = it, id = card.id))
                        }
                        cgRelationDao.insertRelations(selected)
                    }
                    result.let {
                        navController.popBackStack()
                    }
                }
            }
        }, text = "Save", color = DarkRed, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.size(20.dp))
        TextField(
            modifier = Modifier.border(BorderStroke(2.dp, Color.White)),
            value = textState.value,
            onValueChange = { textState.value = it },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
            )
        )
        Spacer(Modifier.size(20.dp))
        Box(Modifier.align(Alignment.CenterHorizontally)) {
            LongButton(function = {
                                  navController.navigate("scanner")
            }, text = "Scan", Modifier)
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.align(Alignment.CenterHorizontally)) {
            LongButton(function = {
                newEditImage.value = true
                cameraLauncher.launch()
            }, text = if (camBitmap.value == null) "Add Image" else "Edit Image", Modifier)
        }
        Spacer(Modifier.height(10.dp))
        camBitmap.let {
            val data = it.value
            if (data != null) {
                Image(
                    bitmap = data.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(400.dp)
                )
            }
        }
        Spacer(Modifier.size(10.dp))
        Text(
            "Groups",
            Modifier
                .align(Alignment.Start)
                .padding(horizontal = 10.dp),
            color = Color.White,
            fontSize = TextUnit(4f, TextUnitType.Em)
        )
        Spacer(Modifier.size(20.dp))
        LazyRow(Modifier.align(Alignment.CenterHorizontally)) {
            items(items = groups.value) { item ->
                SmallButton(
                    function = {
                        if (selectedGroups.contains(item.guid))
                            selectedGroups.remove(item.guid)
                        else
                            selectedGroups.add(item.guid)
                    },
                    text = item.group,
                    color = if (selectedGroups.contains(item.guid)) DarkRed else LightRed,
                    modifier = Modifier
                )

            }
        }
    }
}

class EditCardViewModel(
    cardDAO: CardDAO,
    private val id: Long,
    card: MutableState<Card?>,
    setting: () -> Unit
) : ViewModel() {
    var cardFlow = cardDAO.loadCard(id)

    init {
        viewModelScope.launch {
            cardFlow.collect {
                card.value = it
                setting()
            }
            Log.e("init", id.toString())
        }

    }
}