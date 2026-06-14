package com.example.instrumenttrainer.domain.model

enum class AppLanguage(val tag: String) {
    PL("pl"),
    EN("en"),
    ;

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag } ?: PL
    }
}

data class AppSettings(
    val darkTheme: Boolean = false,
    val language: AppLanguage = AppLanguage.PL,
)
