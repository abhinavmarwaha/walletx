package com.abhinavmarwaha.walletx

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.abhinavmarwaha.walletx.db.room.AppDatabase
import com.abhinavmarwaha.walletx.db.room.CardDAO
import com.abhinavmarwaha.walletx.db.room.CardGroupDAO
import com.abhinavmarwaha.walletx.di.archModelModule
import com.abhinavmarwaha.walletx.ui.theme.WalletXTheme
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.instance
import java.util.Collections.singleton

class MainActivity : ComponentActivity() , DIAware {
    override val di by DI.lazy {
        bind<AppDatabase>() with singleton { AppDatabase.getInstance(this@MainActivity) }
        bind<CardDAO>() with singleton { instance<AppDatabase>().cardDAO() }
        bind<CardGroupDAO>() with singleton { instance<AppDatabase>().cardGroupDAO() }

        import(archModelModule)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            WalletXTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WalletXTheme {
        Greeting("Android")
    }
}