package com.diego.playlistmaker.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.diego.playlistmaker.search.domain.models.UserRequestParam
import com.diego.playlistmaker.search.domain.use_case.SearchTracksWebUseCas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BroadcastReceiverConnectivity(
    private val searchTracksWebUseCase: SearchTracksWebUseCas
): BroadcastReceiver() {

    private var tick = 0
    private companion object{
        const val ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != ACTION) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                searchTracksWebUseCase.execute(UserRequestParam("test"))

                if (tick > 0){
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Подключение к интернету восстановлено",
                            Toast.LENGTH_SHORT
                        ).show()

                        tick--
                    }
                }


            } catch (e: Exception){
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Отсутствует подключение к интернету",
                        Toast.LENGTH_SHORT
                    ).show()

                    tick++
                }
            }

        }

    }
}