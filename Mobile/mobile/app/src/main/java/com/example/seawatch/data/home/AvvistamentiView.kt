package com.example.seawatch

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "avvistamentiDaVedere")
data class AvvistamentiDaVedere(@PrimaryKey var id:String,
                                var avvistatore:String,
                                var data:String,
                                var numeroEsemplari:String,
                                var latid:String,
                                var long:String,
                                var animale:String,
                                var specie:String,
                                var mare:String,
                                var vento:String,
                                var note:String,
                                var img:String,
                                var nome:String,
                                var cognome:String,
                                var online:Boolean
                               ) {
}
