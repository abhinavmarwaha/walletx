package com.abhinavmarwaha.walletx.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.abhinavmarwaha.walletx.archmodel.CardGroupsStore
import com.abhinavmarwaha.walletx.db.room.*
import com.abhinavmarwaha.walletx.ui.theme.DarkRed
import com.abhinavmarwaha.walletx.ui.theme.LightRed
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.android.closestDI
import org.kodein.di.instance

@Composable
fun AddCardView() {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val di: DI by closestDI(LocalContext.current)
    val cardGroupsStore: CardGroupsStore by di.instance()
    val cardDAO: CardDAO by di.instance()
    val cgRelationDao: CGRelationDao by di.instance()
    val vm = AddCardViewModel(cardGroupsStore)

    val imageData = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageData.value = it
        }

    val coroutineScope = rememberCoroutineScope()

    val selectedGroups = mutableListOf<Long>()

    Column {
        Spacer(Modifier.size(20.dp))
        Row{
            TextField(
                value = textState.value,
                onValueChange = { textState.value = it }
            )
            SmallButton(function = {
                val card = Card()
                card.title = textState.value.text
                card.image = imageData.value.toString()

                coroutineScope.launch {
                    val selected = mutableListOf<CardGroupRelation>()

                    selectedGroups.forEach{
                        selected.add(CardGroupRelation(guid = it,id = card.id))
                    }

                    cgRelationDao.insertRelations(selected)
                    cardDAO.insertCard(card)
                }
            }, text = "Save", color = DarkRed)
        }
        if(imageData.value==null)
        LongButton(function = {
            launcher.launch("image/*")
        }, text = "Add Image")
        imageData.let {
            val bitmap = remember { mutableStateOf<Bitmap?>(null) }
            val uri = it.value
            if (uri != null) {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images
                        .Media.getBitmap(context.contentResolver, uri)

                } else {
                    val source = ImageDecoder
                        .createSource(context.contentResolver, uri)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(400.dp)
                    )
                }
            }
        }
        Text("Groups")
        LazyRow(Modifier.align(Alignment.CenterHorizontally)){
            items(items = vm.groups) {
                item -> SmallButton(function = {
                if(selectedGroups.contains(item.guid))
                selectedGroups.remove(item.guid)
                else
                    selectedGroups.add(item.guid)
            }, text = item.group, color = if(selectedGroups.contains(item.guid)) DarkRed else LightRed)
            }
        }
        LongButton(function = { /*TODO*/ }, text = "Add Group")
    }

}

class AddCardViewModel(private val groupsStore: CardGroupsStore) : ViewModel() {
    val groups = mutableStateListOf<CardGroup>()

    init {
        viewModelScope.launch {
            groupsStore.getCardGroups().asFlow().collect {
                groups.addAll(it)
            }
        }
    }

}
