package com.diego.playlistmaker.player.models

enum class PlayerState {
    DEFAULT,      // Начальное состояние
    PREPARING,    // Готовится
    PREPARED,     // Готов к воспроизведению
    PLAYING,      // Воспроизводится
    PAUSED,       // На паузе
    ERROR         // Ошибка
}