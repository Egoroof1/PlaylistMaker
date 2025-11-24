package com.diego.playlistmaker.data.remote.network

import com.diego.playlistmaker.data.remote.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}