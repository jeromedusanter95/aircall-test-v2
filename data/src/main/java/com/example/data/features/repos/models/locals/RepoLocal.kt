package com.example.data.features.repos.models.locals

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoLocal(@PrimaryKey val id: Long, @ColumnInfo(name = "name") val name: String)