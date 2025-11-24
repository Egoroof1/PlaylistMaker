package com.diego.playlistmaker.domain.usecases

import com.diego.playlistmaker.domain.repositories.LocalHistoryRepository

class ClearHistoryUseCase (
    private val historyRepository: LocalHistoryRepository
) {
    fun execute() = historyRepository.clearHistory()
}