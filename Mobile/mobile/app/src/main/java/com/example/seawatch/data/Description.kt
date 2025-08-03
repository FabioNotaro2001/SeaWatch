package com.example.seawatch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "descriptions")
data class Description(@PrimaryKey var id:String,
                     var animale:String,
                     var specie:String, var descrizione:String)