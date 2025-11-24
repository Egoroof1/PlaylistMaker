package com.diego.playlistmaker.domain.impl

import com.diego.playlistmaker.domain.api.TracksInteractor
import com.diego.playlistmaker.domain.repositories.TracksRepository

class TracksInteractorImpl (private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(
        expression: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        /**
         * executor.execute {
         *             consumer.consume(repository.searchTracks(expression))
         *         }
         * И если вы не хотите использовать executor, то можно создавать поток обычным образом.
         * На работе приложения это особо не скажется:
         */
        Thread {
            try {
                val tracks = repository.searchTracks(expression)
                if (tracks.isNotEmpty()) {
                    consumer.onSuccess(tracks)
                } else {
                    // ПУСТОЙ РЕЗУЛЬТАТ - показываем "Ничего не найдено"
                    consumer.onError("NOT_FOUND")
                }
            } catch (e: Exception) {
                // СЕТЕВАЯ ИЛИ СЕРВЕРНАЯ ОШИБКА - показываем "Проблемы со связью"
                if (e.message?.startsWith("HTTP_ERROR_") == true) {
                    consumer.onError("NETWORK_ERROR")
                } else {
                    consumer.onError("NETWORK_ERROR")
                }
            }
        }.start()
    }
}

/**
 * В конструктор передаётся экземпляр класса, реализующего MoviesRepository.
 * Конкретная реализация также будет определяться при инициализации.
 *
 * Метод репозитория searchMovies() вызывает одноимённый метод сетевого клиента,
 * в котором выполняется синхронный сетевой запрос.
 * Поскольку Interactor — последний рубеж перед слоем представления, здесь нужно создать
 * поток ради выполнения запроса. Для запуска новых потоков мы снова используем executor.
 *
 * Здесь можно было бы пересортировать список фильмов, отфильтровать, убрав ненужные результаты
 * поиска, но в нашем примере этого не требуется. Поэтому сразу передаём полученный
 * результат в метод consume().
 */