package com.example.seawatch.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDAO: UserDAO) {
    val all: Flow<List<User>> = userDAO.getAllUsers()

    @WorkerThread
    suspend fun insert(user:User){
        userDAO.insert(user)
    }

    @WorkerThread
    suspend fun deleteAll(){
        userDAO.deleteAll()
    }

    @WorkerThread
    suspend fun getUserByMail(mail:String){
        userDAO.getUserByMail(mail)
    }

    @WorkerThread
    suspend fun setSurnameUserByMail(mail:String, cognome:String){
        userDAO.setSurnameUserByMail(mail, cognome)
    }

    @WorkerThread
    suspend fun setNameUserByMail(mail:String, nome:String){
        userDAO.setNameUserByMail(mail, nome)
    }
}