package com.diego.playlistmaker.sharing.domain.repository

interface ResourceProvider {
    fun getString(resourceId: Int): String
}