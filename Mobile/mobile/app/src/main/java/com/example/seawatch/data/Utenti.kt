package com.example.seawatch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(@PrimaryKey var mail:String,
                     var nome:String,
                     var cognome:String,
                     var password:String,
                     var sale:String)