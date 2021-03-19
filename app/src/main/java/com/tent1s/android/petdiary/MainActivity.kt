package com.tent1s.android.petdiary

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.tent1s.android.petdiary.databinding.ActivityMainBinding
import com.tent1s.android.petdiary.databinding.ActivityStartBinding
import com.tent1s.android.petdiary.ui.main_activity.head.HeadFragment
import com.tent1s.android.petdiary.ui.main_activity.head.Postman
import timber.log.Timber

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by viewBinding(ActivityMainBinding::bind, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val arg = MainActivityArgs.fromBundle(intent.extras!!).namePet
        try {
            (HeadFragment() as Postman).mailToFragment(arg)
        } catch (ignored: ClassCastException) {
            Timber.e("Error provide arg to fragment")
        }



        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_head, R.id.navigation_events, R.id.navigation_photo, R.id.navigation_documents
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.mainNavBottomNavigationView.setupWithNavController(navController)



    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.main_navigation)
        return navController.navigateUp()
    }

}