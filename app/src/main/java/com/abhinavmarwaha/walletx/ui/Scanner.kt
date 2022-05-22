package com.abhinavmarwaha.walletx.ui

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.MutableLiveData
import com.abhinavmarwaha.walletx.MainActivity
import com.abhinavmarwaha.walletx.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory

private fun Context.getActivity(): AppCompatActivity = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> MainActivity().getActivity()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Scanner (){
    val context = LocalContext.current
    lateinit var barcodeView: DecoratedBarcodeView
    val text = MutableLiveData("")

    val readExternal = rememberPermissionState(Manifest.permission_group.CAMERA)

    val root = LayoutInflater.from(context).inflate(R.layout.scanner, null)
    barcodeView = root.findViewById(R.id.barcode_scanner)
    val formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39)
    barcodeView.barcodeView.decoderFactory = DefaultDecoderFactory(formats)
//    val beepManager = BeepManager(context.)
    barcodeView.initializeFromIntent(context.getActivity().intent)

    val callback = object : BarcodeCallback {
        override fun barcodeResult(result: BarcodeResult) {
            if (result.text == null || result.text == text.value) {
                return
            }
            text.value = result.text
//            beepManager.playBeepSoundAndVibrate()
        }
    }
    barcodeView.decodeContinuous(callback)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        AndroidView(modifier = Modifier.fillMaxSize(),
            factory = {
                root
            })
        if (text.value!!.isNotBlank()) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = text.value!!,
                color = Color.White,
                style = MaterialTheme.typography.h4
            )
        }
    }

    fun permission(){
        if (readExternal.status.shouldShowRationale) {
            readExternal.launchPermissionRequest()
        }
    }

}

