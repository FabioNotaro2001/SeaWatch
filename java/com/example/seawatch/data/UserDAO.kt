package com.example.seawatch.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE mail=:mail")
    fun getUserByMail(mail:String): User

    @Query("UPDATE users SET nome=:name WHERE mail=:mail")
    fun setNameUserByMail(mail:String, name:String): Unit

    @Query("UPDATE users SET cognome=:cognome WHERE mail=:mail")
    fun setSurnameUserByMail(mail:String, cognome:String): Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(utente: User)

    @Query("DELETE FROM users")
    suspend fun deleteAll()


}