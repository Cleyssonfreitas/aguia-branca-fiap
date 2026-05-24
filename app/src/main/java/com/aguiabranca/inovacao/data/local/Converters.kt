package com.aguiabranca.inovacao.data.local

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("|")

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.takeIf { it.isNotBlank() }?.split("|") ?: emptyList()
    }
}

