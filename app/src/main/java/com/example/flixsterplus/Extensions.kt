package com.example.flixsterplus

import android.content.Context

/** Converts dp to pixels. */
fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
