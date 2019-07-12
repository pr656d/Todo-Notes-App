package com.premp.todonotesapp.clicklisteners

import com.premp.todonotesapp.db.Notes


interface ItemClickListener {
    fun onClick(notes: Notes)
    fun onUpdate(notes: Notes)
}
