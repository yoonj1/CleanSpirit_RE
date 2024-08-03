package com.example.guru2_re

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable,
    var isBlocked: Boolean = false
)
