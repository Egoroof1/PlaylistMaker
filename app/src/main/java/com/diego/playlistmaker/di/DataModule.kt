package com.diego.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.diego.playlistmaker.media.data.database.AppDatabase
import com.diego.playlistmaker.search.data.network.TrackDtoRetrofit
import com.diego.playlistmaker.search.data.network.api.ITunesApi
import com.diego.playlistmaker.search.data.network.retrofit.RetrofitTrackWeb
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    // ITunesApi (Retrofit сервис)
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    // SharedPreferences для истории
    single {
        androidContext().getSharedPreferences("history_settings", Context.MODE_PRIVATE)
    }

    // Gson
    factory { Gson() }

    // TrackDtoRetrofit реализация
    single<TrackDtoRetrofit> {
        RetrofitTrackWeb(get())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database2.db"
        ).build()
    }
}