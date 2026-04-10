package com.diego.playlistmaker.sharing.domain.repository

interface SharingInteractor {
    fun shareApp(str: String)
    fun openTerms()
    fun openSupport()
}