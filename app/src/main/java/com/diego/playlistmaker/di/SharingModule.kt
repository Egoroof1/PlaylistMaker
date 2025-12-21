package com.diego.playlistmaker.di

import com.diego.playlistmaker.sharing.data.impl.AndroidResourceProvider
import com.diego.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.diego.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.diego.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.diego.playlistmaker.sharing.domain.repository.ResourceProvider
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {
    // ResourceProvider
    single<ResourceProvider> {
        AndroidResourceProvider(androidContext())
    }

    // ExternalNavigator
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    // SharingInteractor
    single<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get(),
            resourceProvider = get()
        )
    }
}