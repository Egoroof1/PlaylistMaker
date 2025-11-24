package com.diego.playlistmaker.domain

import com.diego.playlistmaker.data.remote.ITunesApi
import com.diego.playlistmaker.data.remote.dto.Response
import com.diego.playlistmaker.data.remote.dto.TracksSearchRequest
import com.diego.playlistmaker.data.remote.network.NetworkClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient: NetworkClient {
    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val imdbService = retrofit.create(ITunesApi::class.java)

    /**
     * можно сделать один общий метод в котором будем обрабытывать (какой Request пришёл)
     * а можно делать разные методы для разных запросов
     */

    override fun doRequest(dto: Any): Response {
        // проверяем что за запрос пришёл может быть и TrackGetRequest и тд(CRUD) (если бы была наша БД)
        when (dto){
            is TracksSearchRequest -> {
                val resp = imdbService.searchSongs(dto.expression).execute()
                /*
                 * Метод enqueue() выполняет запрос в отдельном потоке — асинхронно,
                 * и для получения ответа мы использовали Callback. Этот вариант нам не подходит,
                 * потому что метод doRequest() интерфейса обязательно должен вернуть результат
                 * выполнения запроса в том же потоке, в котором он был вызван. И для этого мы
                 * используем метод синхронного выполнения запроса execute()
                 */

                val body = resp.body() ?: Response()

                return body.apply { resultCode = resp.code() }
            }
            else -> return Response().apply { resultCode = 400 }
        }
    }
}