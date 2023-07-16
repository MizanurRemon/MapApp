package com.example.mapapp.Utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.mapapp.R
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import kotlin.math.roundToInt


object Helpers {
    fun generateIcon(color: Int, context: Context): Icon? {
        val infoIconDrawable = ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.baseline_location_on_24,
            null
        )!!

        val bitmap = infoIconDrawable
            .mutate()
            .apply { setTint(color) }
            .toBitmap()

        return IconFactory.getInstance(context)
            .fromBitmap(bitmap)

    }

    fun calculateDistance(distanceInMeters: Double): String {

        var text: String

        if (distanceInMeters > 1000) {
            text = "${((distanceInMeters / 1000).roundToInt()).toString()}km away"
        } else {
            text = "${distanceInMeters.roundToInt().toString()}m away"
        }

        return text
    }

}