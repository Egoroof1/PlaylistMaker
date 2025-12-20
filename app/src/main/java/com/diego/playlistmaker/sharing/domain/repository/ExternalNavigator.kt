package com.diego.playlistmaker.sharing.domain.repository

import com.diego.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator {
    fun shareLink(str: String)

    fun openLink(str: String)

    fun openEmail(emailData: EmailData)
}