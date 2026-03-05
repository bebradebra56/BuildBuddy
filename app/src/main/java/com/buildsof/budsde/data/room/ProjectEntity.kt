package com.buildsof.budsde.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.buildsof.budsde.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Date

// Singleton Gson instance with custom TypeAdapters
object GsonProvider {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(WorkConfig::class.java, WorkConfigTypeAdapter())
        .create()
}

// TypeToken workarounds for ProGuard/R8
private class RoomListTypeToken : TypeToken<List<Room>>()
private class NoteListTypeToken : TypeToken<List<Note>>()

@Entity(tableName = "projects")
@TypeConverters(Converters::class)
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String,
    val startDate: Long,
    val currency: String,
    val photoUri: String?,
    val roomsJson: String,
    val budget: Double,
    val notesJson: String
)

class Converters {
    private val gson = GsonProvider.gson
    private val roomListType: Type = RoomListTypeToken().type
    private val noteListType: Type = NoteListTypeToken().type
    
    @TypeConverter
    fun fromRoomList(value: List<Room>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toRoomList(value: String): List<Room> {
        return gson.fromJson(value, roomListType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromNoteList(value: List<Note>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toNoteList(value: String): List<Note> {
        return gson.fromJson(value, noteListType) ?: emptyList()
    }
}

fun Project.toEntity(): ProjectEntity {
    val gson = GsonProvider.gson
    return ProjectEntity(
        id = id,
        name = name,
        address = address,
        startDate = startDate.time,
        currency = currency.name,
        photoUri = photoUri,
        roomsJson = gson.toJson(rooms),
        budget = budget,
        notesJson = gson.toJson(notes)
    )
}

fun ProjectEntity.toProject(): Project {
    val gson = GsonProvider.gson
    val roomsType = RoomListTypeToken().type
    val notesType = NoteListTypeToken().type
    
    return Project(
        id = id,
        name = name,
        address = address,
        startDate = Date(startDate),
        currency = Currency.valueOf(currency),
        photoUri = photoUri,
        rooms = gson.fromJson(roomsJson, roomsType) ?: emptyList(),
        budget = budget,
        notes = gson.fromJson(notesJson, notesType) ?: emptyList()
    )
}
