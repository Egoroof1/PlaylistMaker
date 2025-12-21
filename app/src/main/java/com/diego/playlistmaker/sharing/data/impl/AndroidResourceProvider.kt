package com.diego.playlistmaker.sharing.data.impl

import android.content.Context
import com.diego.playlistmaker.sharing.domain.repository.ResourceProvider

class AndroidResourceProvider(
    private val context: Context
) : ResourceProvider {

    override fun getString(resourceId: Int): String {
        return context.getString(resourceId)
    }
}