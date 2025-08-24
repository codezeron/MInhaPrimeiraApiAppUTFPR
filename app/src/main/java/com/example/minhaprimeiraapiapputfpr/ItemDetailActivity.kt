package com.example.minhaprimeiraapiapputfpr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minhaprimeiraapiapputfpr.databinding.ActivityItemDetailBinding
import com.example.minhaprimeiraapiapputfpr.model.Item
import com.example.minhaprimeiraapiapputfpr.service.Result
import com.example.minhaprimeiraapiapputfpr.service.RetrofitClient
import com.example.minhaprimeiraapiapputfpr.service.safeApiCall
import com.example.minhaprimeiraapiapputfpr.ui.utils.loadUrl
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityItemDetailBinding

    private lateinit var item: Item
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupView()
        loadItem()
        setupGoogleMap()
    }

    companion object {

        private const val ARG_ID = "arg_id"

        fun newIntent(
            context: Context,
            itemId: String
        ) = Intent(context, ItemDetailActivity::class.java).apply {
            putExtra(ARG_ID, itemId)
        }
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.deleteCTA.setOnClickListener {
            deleteItem()
        }
    }

    private fun loadItem() {
        val itemId = intent.getStringExtra(ARG_ID) ?: ""

        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.getItemById(itemId) }

            Log.d("hello world", "item detail, $result")
            when (result) {
                is Result.Success -> {
                    item = result.data

                    // Update UI on the main thread without crashing
                    withContext(Dispatchers.Main) {
                        handleSuccess()
                    }
                }

                is Result.Error -> {
                    handleError()
                    Toast.makeText(
                        this@ItemDetailActivity,
                        "Item não encontrado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun deleteItem() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = safeApiCall { RetrofitClient.apiService.deleteItemById(item.id) }

            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        Toast.makeText(
                            this@ItemDetailActivity,
                            R.string.error_delete,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Success -> {
                        Toast.makeText(
                            this@ItemDetailActivity,
                            getString(R.string.success_delete), Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        }
    }

    private fun handleSuccess() {
        binding.name.text = item.value.name
        binding.age.text = getString(R.string.item_age, item.value.age.toString())
        binding.profession.setText(item.value.profession)
        binding.image.loadUrl(item.value.imageUrl)
    }

    private fun handleError() {

    }

    private fun setupGoogleMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        if (::item.isInitialized){
            // se o item foi inicializado
            loadItemLocationInGoogleMap()
        }
    }

    private fun loadItemLocationInGoogleMap() {
        item.value.location.apply {
                binding.googleMapContent.visibility = View.VISIBLE
                val latLong = LatLng(latitude, longitude)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLong)
                        .title(name)
                )
                mMap.moveCamera(newLatLngZoom(latLong, 15f))
                Log.d("ItemDetailActivity", "Location: $name, LatLng: $latLong" )
            }
        }
}