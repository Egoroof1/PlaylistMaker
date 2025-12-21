package com.diego.playlistmaker.sharing.domain.impl

import com.diego.playlistmaker.R
import com.diego.playlistmaker.sharing.domain.repository.ExternalNavigator
import com.diego.playlistmaker.sharing.domain.repository.SharingInteractor
import com.diego.playlistmaker.sharing.domain.model.EmailData
import com.diego.playlistmaker.sharing.domain.repository.ResourceProvider

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val resourceProvider: ResourceProvider
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
        return resourceProvider.getString(R.string.practicum_ru)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            email = resourceProvider.getString(R.string.email_developers),
            subject = resourceProvider.getString(R.string.subject_to_email),
            message = resourceProvider.getString(R.string.message_to_email)
        )
    }

    private fun getTermsLink(): String {
        return resourceProvider.getString(R.string.practicum_offer_ru)
    }
}