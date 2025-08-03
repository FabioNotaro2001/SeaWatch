package com.example.seawatch

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.seawatch.data.*

@Database(entities=[AvvistamentiDaCaricare::class, Favourite::class, User::class, AvvistamentiDaVedere::class, Description::class, Place::class], version=6)
abstract class SWDatabase:RoomDatabase(){
    abstract fun avvistamentiDAO():AvvistamentiDAO
    abstract fun favouriteDAO(): FavouriteDAO
    abstract fun userDAO(): UserDAO
    abstract fun avvistamentiViewDAO(): AvvistamentiViewDAO
    abstract fun descriptionDAO():DescriptionDAO
    abstract fun placesDAO():PlacesDAO

    companion object{
        @Volatile
        private var INSTANCE:SWDatabase?=null
        fun getDatabase(context: Context):SWDatabase{
            return INSTANCE?: synchronized(this){
                val instance= Room.databaseBuilder(context.applicationContext, SWDatabase::class.java,"SWDatabase").build()
                INSTANCE=instance
                instance
            }
        }
    }
}