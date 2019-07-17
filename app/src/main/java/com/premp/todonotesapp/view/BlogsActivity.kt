package com.premp.todonotesapp.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.premp.todonotesapp.R
import com.premp.todonotesapp.adapter.BlogsAdapter
import com.premp.todonotesapp.model.JsonResponse

class BlogsActivity : AppCompatActivity() {

    val TAG = "BlogsActivity"

    lateinit var recyclerViewBlogs: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blogs)
        bindViews()
        getBlogs()
    }

    private fun getBlogs() {
        AndroidNetworking.get("http://www.mocky.io/v2/5926ce9d11000096006ccb30")
                .setPriority(Priority.HIGH)
                .build()
                .getAsObject(JsonResponse::class.java, object : ParsedRequestListener<JsonResponse>{
                    override fun onResponse(response: JsonResponse?) {
                        setupRecyclerView(response)
                    }

                    override fun onError(anError: ANError?) {
                        Log.d(TAG, anError?.localizedMessage)
                    }   
                })
    }

    private fun bindViews() {
        recyclerViewBlogs = findViewById(R.id.recyclerViewBlogs)
    }

    private fun setupRecyclerView(response: JsonResponse?) {
        val blogAdapter = BlogsAdapter(response!!.data)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        recyclerViewBlogs.layoutManager = linearLayoutManager
        recyclerViewBlogs.adapter = blogAdapter
    }
}
