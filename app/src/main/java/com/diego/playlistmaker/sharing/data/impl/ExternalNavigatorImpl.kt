package com.diego.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.diego.playlistmaker.sharing.domain.model.EmailData
import com.diego.playlistmaker.sharing.domain.repository.ExternalNavigator

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(str: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, str)
                // Добавляем флаг для запуска из любого контекста
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun openLink(str: String) {
        try {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(str)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(agreementIntent)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun openEmail(emailData: EmailData) {
        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
                putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
                putExtra(Intent.EXTRA_TEXT, emailData.message)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(emailIntent)
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}