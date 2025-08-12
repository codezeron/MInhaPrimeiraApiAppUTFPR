package com.example.minhaprimeiraapiapputfpr

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minhaprimeiraapiapputfpr.databinding.ActivityMainBinding
import com.example.minhaprimeiraapiapputfpr.service.RetrofitClient
import com.example.minhaprimeiraapiapputfpr.service.safeApiCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        fetchItems()
    }

    private fun fetchItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItems() }
            Log.d("MainActivity", "Result: $result")
        }
    }


    private fun setupView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}