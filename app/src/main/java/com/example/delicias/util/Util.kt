package com.example.delicias.util

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.delicias.domain.Date
import com.example.delicias.domain.RestaurantMinimal
import com.example.delicias.domain.repository.RestaurantRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.Math.pow
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow
import kotlin.math.sqrt


class Util {
    companion object{
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
        private var locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        fun getDates(): Flow<ArrayList<Date>> = flow {
            var dateList = ArrayList<Date>()
            var calender = Calendar.getInstance(Locale.KOREA)

            for (i in 0..6) {
                var temp =  if (i == 0) 0 else 1

                calender.add(Calendar.DATE, temp)

                dateList.add(
                    Date(
                        getKorDayOfWeek(calender.get(Calendar.DAY_OF_WEEK)),
                        getDateString(calender.get(Calendar.DATE)), i == 0
                    )
                )
            }

            emit(dateList)
        }


        private fun getKorDayOfWeek(dayOfWeek: Int): String{
            var korDayOfWeek = ""

            when(dayOfWeek){
                1 -> korDayOfWeek = "일"
                2 -> korDayOfWeek = "월"
                3 -> korDayOfWeek = "화"
                4 -> korDayOfWeek = "수"
                5 -> korDayOfWeek = "목"
                6 -> korDayOfWeek = "금"
                7 -> korDayOfWeek = "토"
            }
            return korDayOfWeek
        }

        private fun getDateString(date: Int): String{
            var dateString = date.toString()

            if (dateString.length == 1){
                dateString = "0$dateString"
            }

            return dateString
        }

        suspend fun sortByLocation(context: Context, repository: RestaurantRepository) {
            withContext(Dispatchers.Main) {
                var restaurants = repository.getAllRestaurants().first()
                val currentLocation = getLocation(context)

                Collections.sort(restaurants, kotlin.Comparator { left, right ->

                    Log.d("delitag", "sortByLocation: ${currentLocation?.latitude} ${currentLocation?.longitude}")
                    if (Util.calculateRestaurantDistance
                            (currentLocation, left.latitude, left.longitude) >
                        Util.calculateRestaurantDistance
                            (currentLocation, right.latitude, right.longitude))
                        return@Comparator 1
                    else if (Util.calculateRestaurantDistance
                            (currentLocation, left.latitude, left.longitude) <
                        Util.calculateRestaurantDistance
                            (currentLocation, right.latitude, right.longitude))
                        return@Comparator -1
                    else return@Comparator 0
                })

                val restaurantIterator = restaurants.iterator()
                for ((index , value) in restaurantIterator.withIndex()) {
                    repository.updateDistanceOrderOfRestaurantById(value.id, index + 1)
                }
            }
        }

        fun getLocation(context: Context): Location? {
            var locationManager: LocationManager?
            var location: Location? = null
            try {
                locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager?
                val isGPSEnabled: Boolean =
                    locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
                val isNetworkEnabled: Boolean =
                    locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ?: false
                if (!isGPSEnabled && !isNetworkEnabled) {
                } else {
                    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
                    ) {
                    } else return null
                    if (isNetworkEnabled) {
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            locationListener
                        )
                        if (locationManager != null) {
                            location =
                                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        }
                    }
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager?.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                                locationListener
                            )
                            if (locationManager != null) {
                                location =
                                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("delitag", e.toString())
            }
            return location
        }

        fun calculateRestaurantDistance(currentLocation: Location?, restaurantLat: Double, restaurantLng: Double): Double {
            return sqrt((currentLocation?.latitude?.minus(restaurantLat)?:0.0).pow(2.0) + (currentLocation?.longitude?.minus(restaurantLng)
                ?:0.0.pow(2.0)))
        }
    }
}