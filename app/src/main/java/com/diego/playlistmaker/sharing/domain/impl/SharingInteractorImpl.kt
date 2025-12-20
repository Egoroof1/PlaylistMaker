package com.diego.playlistmaker.sharing.domain.impl

import android.content.Context
import com.diego.playlistmaker.R
import com.diego.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor
import com.diego.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return context.getString(R.string.practicum_ru)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.email_developers),
            subject = context.getString(R.string.subject_to_email),
            message = context.getString(R.string.message_to_email)
        )
    }

    private fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer_ru)
    }
}