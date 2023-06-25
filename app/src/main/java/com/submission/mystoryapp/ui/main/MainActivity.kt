package com.submission.mystoryapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.submission.mystoryapp.R
import com.submission.mystoryapp.data.local.entity.StoryEntity
import com.submission.mystoryapp.databinding.ActivityMainBinding
import com.submission.mystoryapp.di.ViewModelFactory
import com.submission.mystoryapp.ui.auth.AuthActivity
import com.submission.mystoryapp.ui.maps.GMapsActivity
import com.submission.mystoryapp.ui.story.DetailFragment
import com.submission.mystoryapp.ui.viewmodel.AuthViewModel
import com.submission.mystoryapp.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var barToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val homeFragmentTag = "HomeFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = binding!!.drawerLayout
        navigationView = binding!!.navViewHome

        val preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val currentFragmentId = preferences.getInt("currentFragmentId", R.id.action_home)
        navigationView.menu.findItem(currentFragmentId)?.isChecked = true

        setDrawer()
        setupModel()

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_container)
            navigationView.menu.findItem(R.id.action_home)?.isChecked = currentFragment is HomeFragment
        }

        if (savedInstanceState == null) {
            showHomeFragment()
        } else {
            val homeFragment = supportFragmentManager.findFragmentByTag(homeFragmentTag)
            if (homeFragment != null) {
                supportFragmentManager.beginTransaction().show(homeFragment).commit()
            } else {
                showHomeFragment()
            }
        }

        @Suppress("DEPRECATION")
        val story = intent.getParcelableExtra<StoryEntity>("story")
        if (story != null) {
            val detailStoryFragment = DetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("story", story)
            detailStoryFragment.arguments = bundle
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, detailStoryFragment)
                .commit()
            navigationView.menu.findItem(R.id.action_home)?.isChecked = false
        }
    }

    private fun showHomeFragment() {
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_container, homeFragment, homeFragmentTag)
            .commit()
    }

    private fun setupModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (barToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDrawer() {
        barToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open_menu, R.string.close_menu)
        drawerLayout.addDrawerListener(barToggle)
        barToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        barToggle.setToolbarNavigationClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        navigationView.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.action_home -> {
                    menu.isChecked = true
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_container, homeFragment, homeFragmentTag)
                        .commit()
                }
                R.id.action_location -> {
                    val intent = Intent(this@MainActivity, GMapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.action_about -> {
                    Toast.makeText(this, R.string.about_detail, Toast.LENGTH_SHORT).show()
                }
                R.id.action_logout -> {
                    authViewModel.logout()
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}