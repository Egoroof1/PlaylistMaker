package com.diego.playlistmaker.data.remote.dto

/**
 * следует создавать отдельный класс для каждого запроса,
 * поля которого — все параметры для соответствующего запроса,
 * даже если такой параметр всего один
 */
data class TracksSearchRequest(val expression: String)
