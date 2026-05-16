package com.diego.playlistmaker.di

import com.diego.playlistmaker.utils.BroadcastReceiverConnectivity
import org.koin.dsl.module

val broadcast = module {
    single {
        BroadcastReceiverConnectivity(
            get()
        )
    }
}