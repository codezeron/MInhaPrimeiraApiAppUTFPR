package com.example.minhaprimeiraapiapputfpr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minhaprimeiraapiapputfpr.databinding.ActivityItemDetailBinding
import com.example.minhaprimeiraapiapputfpr.model.Item
import com.example.minhaprimeiraapiapputfpr.service.Result
import com.example.minhaprimeiraapiapputfpr.service.RetrofitClient
import com.example.minhaprimeiraapiapputfpr.service.safeApiCall
import com.example.minhaprimeiraapiapputfpr.ui.utils.loadUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var item : Item
    private lateinit var binding: ActivityItemDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupView()
        loadItem()
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItemById(itemId) }

            Log.d("hello world", "item detail, $result")
            when(result){
                is Result.Success -> {
                    item = result.data

                    // Update UI on the main thread without crashing
                    withContext(Dispatchers.Main) {
                        handleSuccess()
                    }
                }
                is Result.Error -> {
                    handleError()
                    Toast.makeText(this@ItemDetailActivity, "Item não encontrado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleSuccess(){
        binding.name.text = item.value.name
        binding.age.text = getString(R.string.item_age, item.value.age.toString())
        binding.profession.setText(item.value.profession)
        binding.image.loadUrl(item.value.imageUrl)
    }

    private fun handleError(){

    }

    companion object {

        private const val ARG_ID = "arg_id"

        fun newIntent(
            context : Context,
            itemId: String
        ) = Intent(context, ItemDetailActivity::class.java).apply {
            putExtra(ARG_ID, itemId)
        }
    }
}