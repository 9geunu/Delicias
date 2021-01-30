package com.example.delicias.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.delicias.R
import com.example.delicias.ui.favorites.FavoritesFragment
import com.example.delicias.ui.home.HomeFragment
import com.example.delicias.ui.map.MapFragment
import com.example.delicias.ui.setting.SettingFragment
import com.example.delicias.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    val adapter = PagerAdapter(supportFragmentManager, lifecycle)
    var homeFragment: HomeFragment
    var favoritesFragment: FavoritesFragment
    var mapFragment: MapFragment
    var settingFragment: SettingFragment
    val scope = CoroutineScope(Dispatchers.IO)
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    init {
        homeFragment = HomeFragment()
        favoritesFragment = FavoritesFragment()
        mapFragment = MapFragment()
        settingFragment = SettingFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = adapter
        view_pager.registerOnPageChangeCallback( PageChangeCallback() )
        nav_view.setOnNavigationItemSelectedListener { navigationSelected(it) }
    }

    inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm, lc) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    homeFragment
                }
                1 -> {
                    favoritesFragment
                }
                2 -> {
                    mapFragment
                }
                3 -> {
                    settingFragment
                }
                else -> error("no such position: $position")
            }
        }
    }

    private fun navigationSelected(item: MenuItem): Boolean {
        val checked = item.setChecked(true)
        when (checked.itemId) {
            R.id.action_home -> {
                view_pager.currentItem = 0
                return true
            }
            R.id.action_favorite -> {
                view_pager.currentItem = 1
                return true
            }
            R.id.action_map -> {
                view_pager.currentItem = 2
                return true
            }
            R.id.action_setting -> {
                view_pager.currentItem = 3
                return true
            }
        }
        return false
    }

    private inner class PageChangeCallback: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            nav_view.selectedItemId = when (position) {
                0 -> {
                    scope.launch {
                        homeFragment.refresh()
                    }

                    view_pager.isUserInputEnabled = true
                    R.id.action_home
                }
                1 -> {
                    scope.launch {
                        favoritesFragment.refresh()
                    }

                    view_pager.isUserInputEnabled = true
                    R.id.action_favorite
                }
                2 -> {
                    view_pager.isUserInputEnabled = false
                    R.id.action_map
                }
                3 -> {
                    view_pager.isUserInputEnabled = true
                    R.id.action_setting
                }
                else -> error("no such position: $position")
            }
        }
    }

    override fun onBackPressed() {
        when(view_pager.currentItem){
            0 -> {
                super.onBackPressed()
            }
            1 -> {
                view_pager.currentItem = 0
            }
            2 -> {
                if (mapFragment.onBackPress())
                    view_pager.currentItem = 0
            }
            3 -> {
                view_pager.currentItem = 0
            }
        }
    }

    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            var check_result = true

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Toast.makeText(
                        this,
                        "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE ->
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    Log.d("delitag", "onActivityResult : GPS 활성화 되있음")
                    checkRunTimePermission()
                    return
                }
            Constants.REQUEST_SEARCH_RESULT -> {
                mapFragment.goToDestination(data?.getStringExtra("DestinationRestaurant"))
            }
        }
    }

    private fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(
                    this,
                    "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_LONG
                ).show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton("취소") { dialog, id ->
            dialog.cancel()
        }
        builder.create().show()
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager?
        return (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
}
