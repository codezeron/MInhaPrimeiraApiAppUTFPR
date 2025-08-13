package com.example.minhaprimeiraapiapputfpr

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minhaprimeiraapiapputfpr.adapter.ItemAdapter
import com.example.minhaprimeiraapiapputfpr.databinding.ActivityMainBinding
import com.example.minhaprimeiraapiapputfpr.service.RetrofitClient
import com.example.minhaprimeiraapiapputfpr.service.safeApiCall
import com.example.minhaprimeiraapiapputfpr.service.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
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

            withContext(Dispatchers.Main) {
                binding.swipeRefreshLayout.isRefreshing = false
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${result.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is Result.Success -> {
                        val adapter = ItemAdapter(result.data) { item ->
                            startActivity(
                                ItemDetailActivity.newIntent(
                                    context = this@MainActivity,
                                    itemId = item.id
                                )
                            )
                        }
                        binding.recyclerView.adapter = adapter
                    }
                }
            }
        }
    }

    private fun setupView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            fetchItems()
        }
    }
}