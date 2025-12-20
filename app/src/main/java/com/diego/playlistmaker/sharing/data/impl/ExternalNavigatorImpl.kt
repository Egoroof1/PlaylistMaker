package com.diego.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.diego.playlistmaker.sharing.domain.model.EmailData
import com.diego.playlistmaker.sharing.domain.repository.ExternalNavigator

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(str: String) {
        Log.d("ExternalNavigator", "shareLink: $str")
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, str)
                // Добавляем флаг для запуска из любого контекста
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            Log.e("ExternalNavigator", "Error sharing link", e)
        }
    }

    override fun openLink(str: String) {
        Log.d("ExternalNavigator", "openLink: $str")
        try {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(str)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(agreementIntent)
        } catch (e: Exception) {
            Log.e("ExternalNavigator", "Error opening link", e)
        }
    }

    override fun openEmail(emailData: EmailData) {
        Log.d("ExternalNavigator", "openEmail: ${emailData.email}")
        try {
            val uri = Uri.parse("mailto:${emailData.email}")
                .buildUpon()
                .appendQueryParameter("subject", emailData.subject)
                .appendQueryParameter("body", emailData.message)
                .build()

            val emailIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(emailIntent)
        } catch (e: Exception) {
            Log.e("ExternalNavigator", "Error opening email", e)
        }
    }
}