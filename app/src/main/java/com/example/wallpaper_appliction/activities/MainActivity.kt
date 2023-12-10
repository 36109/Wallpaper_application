package com.example.wallpaper_appliction.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.wallpaper_appliction.Fragments.*
import com.example.wallpaper_appliction.R
import com.example.wallpaper_appliction.adapter.viewpageradapter
import com.example.wallpaper_appliction.databinding.ActivityMainBinding
import com.example.wallpaper_appliction.helperclass.Slidepagetransformer
import com.example.wallpaper_appliction.helperclass.connectivitybroadcast
import com.example.wallpaper_appliction.viewmodel.searchviewmodel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.marsad.stylishdialogs.StylishAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    private val broadcastReceiver = connectivitybroadcast()
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    private val viewModel by viewModels<searchviewmodel>()
    private val connectivityReceiverIntentFilter = IntentFilter("internet")
    private val connectivityReceiverListener = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "internet") {
                val isConnected = intent.getBooleanExtra("is_connected", false)
                if (!isConnected) {
                    val pDialog = StylishAlertDialog(context, StylishAlertDialog.ERROR)
                    pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
                    pDialog.setTitleText("NO INTRENET")
                        .setContentText("Please check your internet connection")
                        .setCancellable(false)
                        .show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        registerReceiver(connectivityReceiverListener, connectivityReceiverIntentFilter)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        drawerLayout = findViewById(R.id.drawerlayout)
        val navigationview = findViewById<NavigationView>(R.id.navigationview)
        navigationview.setNavigationItemSelectedListener(this)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        binding.viewpagerHome.setPageTransformer(Slidepagetransformer())

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                tab?.view?.setBackgroundResource(R.drawable.tab_bg)
                binding.viewpagerHome.setCurrentItem(tab!!.position, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.view!!.setBackgroundResource(android.R.color.transparent)
            }


            override fun onTabReselected(tab: TabLayout.Tab?) {
                try {
                    // Leave this method empty or with a comment indicating no action is needed
                } catch (e: Exception) {
                    // Log any exceptions or errors
                    Log.e("TabReselectedError", "An error occurred when the tab was reselected", e)
                }
            }
        })

        val categoriesfragment = arrayListOf<Fragment>(
            VideoWallpaper(),
            Galleryvideos(),
            Hdwallpaper(),
            Categories(),
            Downloads(),

            )
        binding.viewpagerHome.isUserInputEnabled = true
        val viewpageradapter =
            viewpageradapter(categoriesfragment, supportFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewpageradapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                3 -> tab.text = "Categories"
                4 -> tab.text = "Downloads"
                1 -> tab.text = "Galleryvideos"
                2 -> tab.text = "Hdwallpaper"
                0 -> tab.text = "Videowallpapers"

            }
        }.attach()


    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.search_action)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                val searchFragment = Search()
                val bundle = Bundle()
                bundle.putString("query", query)
                searchFragment.arguments = bundle

                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(searchView.windowToken, 0)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, searchFragment)
                    .addToBackStack(null)
                    .commit()

                viewModel.loadNextPage(query.toString())

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle query text change

                return true
            }

        })



        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                supportFragmentManager.popBackStack()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(connectivityReceiverListener)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.hdwallpapers -> change_tab_index(2)
            R.id.downloads -> change_tab_index(4)
            R.id.galleryvideos -> change_tab_index(1)
            R.id.videowallpapers -> change_tab_index(0)
            R.id.categories -> change_tab_index(3)
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun change_tab_index(index: Int) {
        binding.viewpagerHome.setCurrentItem(index, true)
    }

}
