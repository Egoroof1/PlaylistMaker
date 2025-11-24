package com.diego.playlistmaker.data.remote.dto

/**
 * Ответ от API iTunes
 * Это родительский класс ответов от сервера.
 * От него должны наследоваться все классы,
 * описывающие параметры ответа,
 * даже если такой класс всего один
 */
open class Response(
//    @SerializedName("resultCount") val resultCount: Int,
//    @SerializedName("results") val results: List<NetworkTrackDTO>
) {
    var resultCode = 0
}
