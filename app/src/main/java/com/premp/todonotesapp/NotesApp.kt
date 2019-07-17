package com.premp.todonotesapp

import android.app.Application
import com.androidnetworking.AndroidNetworking
import com.premp.todonotesapp.db.NotesDatabase

class NotesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
    }

    fun getNotesDb(): NotesDatabase {
        return NotesDatabase.getInstance(this)
    }
}