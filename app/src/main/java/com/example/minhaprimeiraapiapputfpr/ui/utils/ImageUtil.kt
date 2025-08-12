package com.example.minhaprimeiraapiapputfpr.ui.utils

import android.widget.ImageView
import com.example.minhaprimeiraapiapputfpr.R
import com.squareup.picasso.Picasso

fun ImageView.loadUrl(imageUrl: String){
    Picasso.get()
        .load(imageUrl)
        .placeholder(R.drawable.ic_downloading)
        .error(R.drawable.ic_error)
        .transform(CircleTransform())
        .into(this)
}