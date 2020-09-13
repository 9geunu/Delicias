package com.example.delicias

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.delicias.ui.FavoritesFragment
import com.example.delicias.ui.HomeFragment
import com.example.delicias.ui.MapFragment
import com.example.delicias.ui.SettingFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = PagerAdapter(supportFragmentManager, lifecycle)
        view_pager.registerOnPageChangeCallback( PageChangeCallback() )
        nav_view.setOnNavigationItemSelectedListener { navigationSelected(it) }
    }

    private inner class PagerAdapter(fm: FragmentManager, lc: Lifecycle): FragmentStateAdapter(fm, lc) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> FavoritesFragment()
                2 -> MapFragment()
                3 -> SettingFragment()
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
                0 -> R.id.action_home
                1 -> R.id.action_favorite
                2 -> R.id.action_map
                3 -> R.id.action_setting
                else -> error("no such position: $position")
            }
        }
    }
}
