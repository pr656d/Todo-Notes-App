package com.premp.todonotesapp.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.premp.todonotesapp.NotesApp
import com.premp.todonotesapp.R
import com.premp.todonotesapp.adapter.NotesAdapter
import com.premp.todonotesapp.clicklisteners.ItemClickListener
import com.premp.todonotesapp.db.Notes
import com.premp.todonotesapp.utils.AppConstant
import com.premp.todonotesapp.utils.PrefConstant
import com.premp.todonotesapp.workmanager.MyWorker
import java.util.*
import java.util.concurrent.TimeUnit

class MyNotesActivity : AppCompatActivity() {

    internal val TAG = "MyNotesActivity"

    private var fullName: String = ""
    private lateinit var fabAddNotes: FloatingActionButton
    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private var listNotes = ArrayList<Notes>()

    private val ADD_NOTES_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_notes)

        bindViews()
        setupSharedPreference()
        getIntentData()
        getDataFromDatabase()
        setupToolbarText()
        clickListeners()
        setupRecyclerView()
        setupWorkManager()
    }

    private fun setupWorkManager() {
        val constraint = Constraints.Builder()
                .build()
        val request = PeriodicWorkRequest
                .Builder(MyWorker::class.java, 1, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .build()
        WorkManager.getInstance().enqueue(request)

        // If we want to execute multiple workers in manner of
        // request1 -> request2     then
        // Our request should be of type OneTimeWorkRequest
        //  WorkManager.getInstance().beginWith(request).then(request2).enqueue()

        // Also if we want in manner of
        // (request1, request2, request3) -> request4   then
        //  WorkManager.getInstance()
        //      .beginWith(request1, request2, request3).then(request4).enqueue()
    }

    private fun getDataFromDatabase() {
        val notesApp = applicationContext as NotesApp
        val notesDao = notesApp.getNotesDb().notesDao()
        listNotes.addAll(notesDao.getAll())
    }

    private fun clickListeners() {
        fabAddNotes.setOnClickListener {
            //            setupDialogBox()
            val intent = Intent(this@MyNotesActivity, AddNotesActivity::class.java)
            startActivityForResult(intent, ADD_NOTES_CODE)
        }
    }

    private fun setupDialogBox() {
        val view = LayoutInflater.from(this@MyNotesActivity)
                .inflate(R.layout.add_notes_dialog_layout, null)
        val editTextTitle = view.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextDescription)
        val buttonSubmit = view.findViewById<Button>(R.id.buttonSubmit)
        val dialog = AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()

        buttonSubmit.setOnClickListener {
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val notes = Notes(title = title, description = description)
                listNotes.add(notes)
                addNotesToDb(notes)
            } else {
                Toast.makeText(this@MyNotesActivity, "Title or Description can't be empty",
                        Toast.LENGTH_LONG).show()
            }
            dialog.hide()
        }
        dialog.show()
    }

    private fun addNotesToDb(notes: Notes) {
        // insert notes in DB
        val notesApp = applicationContext as NotesApp
        val notesDao = notesApp.getNotesDb().notesDao()
        notesDao.insert(notes)
    }

    private fun setupRecyclerView() {
        val itemClickListener = object : ItemClickListener {
            override fun onClick(notes: Notes) {
                val intent = Intent(this@MyNotesActivity, DetailActivity::class.java)
                intent.putExtra(AppConstant.TITLE, notes.title)
                intent.putExtra(AppConstant.DESCRIPTION, notes.description)
                startActivity(intent)
            }

            override fun onUpdate(notes: Notes) {
                val notesApp = applicationContext as NotesApp
                val notesDao = notesApp.getNotesDb().notesDao()
                notesDao.updateNotes(notes)
            }
        }
        val notesAdapter = NotesAdapter(listNotes, itemClickListener)
        val linearLayoutManager = LinearLayoutManager(this@MyNotesActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewNotes.layoutManager = linearLayoutManager
        recyclerViewNotes.adapter = notesAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_NOTES_CODE) {
            val title = data?.getStringExtra(AppConstant.TITLE)
            val description = data?.getStringExtra(AppConstant.DESCRIPTION)
            val imagePath = data?.getStringExtra(AppConstant.IMAGE_PATH)

            val notes = Notes(title = title!!, description = description!!, imagePath = imagePath!!, isTaskCompleted = false)
            addNotesToDb(notes)
            listNotes.add(notes)
            recyclerViewNotes.adapter?.notifyItemChanged(listNotes.size - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.blogs) {
            Log.d(TAG, "Blog clicked")
            val intent = Intent(this@MyNotesActivity, BlogActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbarText() {
        if (supportActionBar != null)
            supportActionBar?.title = fullName
    }

    private fun getIntentData() {
        val intent = intent
        if (intent.hasExtra(AppConstant.FULL_NAME)) {
            fullName = intent.getStringExtra(AppConstant.FULL_NAME)
        }
        if (fullName.isEmpty()) {
            fullName = sharedPreferences.getString(PrefConstant.FULL_NAME, "")
        }
    }

    private fun bindViews() {
        fabAddNotes = findViewById(R.id.fabAddNotes)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
    }

    private fun setupSharedPreference() {
        sharedPreferences = getSharedPreferences(PrefConstant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}
