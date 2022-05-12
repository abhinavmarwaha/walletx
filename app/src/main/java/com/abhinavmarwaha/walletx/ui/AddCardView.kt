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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.abhinavmarwaha.walletx.ui.widgets.LongButton
import com.abhinavmarwaha.walletx.ui.widgets.SmallButton

@Composable
fun AddCardView() {
    val textState = remember { mutableStateOf(TextFieldValue()) }

    val imageData = remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            imageData.value = it
        }

    val groups = listOf("Ids","Cards")

    Column {
        Spacer(Modifier.size(20.dp))
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it }
        )
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
            items(groups) {
                item -> SmallButton(function = { /*TODO*/ }, text = item)
            }
        }
        LongButton(function = { /*TODO*/ }, text = "Add Group")
    }

}