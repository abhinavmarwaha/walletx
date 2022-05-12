package com.abhinavmarwaha.walletx.di

import com.abhinavmarwaha.walletx.archmodel.CardGroupsStore
import com.abhinavmarwaha.walletx.archmodel.CardsStore
import com.abhinavmarwaha.walletx.archmodel.Repository
import com.abhinavmarwaha.walletx.archmodel.SettingsStore
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton


val archModelModule = DI.Module(name = "arch models") {
    bind<Repository>() with singleton { Repository(di) }
    bind<SettingsStore>() with singleton { SettingsStore(di) }
    bind<CardsStore>() with singleton { CardsStore(di) }
    bind<CardGroupsStore>() with singleton { CardGroupsStore(di) }

}
