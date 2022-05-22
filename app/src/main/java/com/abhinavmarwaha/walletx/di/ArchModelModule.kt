package com.abhinavmarwaha.walletx.di

import com.abhinavmarwaha.walletx.archmodel.*
//import com.abhinavmarwaha.walletx.archmodel.SettingsStore
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton


val archModelModule = DI.Module(name = "arch models") {
    bind<Repository>() with singleton { Repository(di) }
//    bind<SettingsStore>() with singleton { SettingsStore(di) }
    bind<CardsStore>() with singleton { CardsStore(di) }
    bind<CardGroupsStore>() with singleton { CardGroupsStore(di) }
    bind<NotesStore>() with singleton { NotesStore(di) }
    bind<CouponsStore>() with singleton { CouponsStore(di) }

}
