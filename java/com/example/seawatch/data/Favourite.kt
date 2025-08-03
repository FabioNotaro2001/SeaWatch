package com.example.seawatch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite")
data class Favourite(@PrimaryKey var id:String,
                                    var utente:String,
                                    var avvistamento:String)