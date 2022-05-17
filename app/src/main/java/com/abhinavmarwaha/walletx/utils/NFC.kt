package com.abhinavmarwaha.walletx.utils

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.widget.Toast

fun nfc(context: Context){
    try {
        // nfc process start
        val nfcAdapter: NfcAdapter? by lazy {
            NfcAdapter.getDefaultAdapter(context)
        }

        var pendingIntent = PendingIntent.getActivity(
            context, 0, Intent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        try {
            ndef.addDataType("text/plain")
        } catch (e: IntentFilter.MalformedMimeTypeException) {
            throw RuntimeException("fail", e)
        }
        var intentFiltersArray = arrayOf(ndef)
        if (nfcAdapter == null) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("NF.")
            builder.setPositiveButton("Cancel", null)
            val myDialog = builder.create()
            myDialog.setCanceledOnTouchOutside(true)
            myDialog.show()

        } else if (!nfcAdapter!!.isEnabled) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("NFC")
            builder.setMessage("NFC")
            builder.setNegativeButton("Cancel", null)
            val myDialog = builder.create()
            myDialog.setCanceledOnTouchOutside(false)
            myDialog.show()
        }
    }
    catch (ex:Exception)
    {
        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
    }
}

fun readNFC(intent: Intent, context: Context){
    val action = intent.action
    if (NfcAdapter.ACTION_NDEF_DISCOVERED == action) {

        val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        with(parcelables) {
            try {
                val inNdefMessage = intent.extras?.get("something") as NdefMessage
                val inNdefRecords = inNdefMessage.records
                //if there are many records, you can call inNdefRecords[1] as array
                var ndefRecord_0 = inNdefRecords[0]
                var inMessage = String(ndefRecord_0.payload)
//                shopid = inMessage.drop(3);
//                txtviewshopid.setText("SHOP ID: " + shopid)
                print(inNdefMessage)

                ndefRecord_0 = inNdefRecords[1]
                inMessage = String(ndefRecord_0.payload)
//                machineid = inMessage.drop(3);
//                txtviewmachineid.setText("MACHINE ID: " + machineid)


                if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action
                    || NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action
                ) {

                    val tag =
                        intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return
                    val ndef = Ndef.get(tag) ?: return

                } else {
                    try {


                        ndefRecord_0 = inNdefRecords[2]
                        inMessage = String(ndefRecord_0.payload)

                    }
                    catch (ex:Exception){
                        Toast.makeText(context, "User ID not written!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex: Exception) {
                Toast.makeText(
                    context,
                    "There are no Machine and Shop information found!, please click write data to write those!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}