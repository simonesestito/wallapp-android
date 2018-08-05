package it.simonesestito.wallapp.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import it.simonesestito.wallapp.R

class MainActivity : AppCompatActivity() {
    private val defaultAppbarElevation by lazy {
        resources.getDimension(R.dimen.default_appbar_elevation)
    }
    private val scrollAppbarElevation by lazy {
        resources.getDimension(R.dimen.scroll_appbar_elevation)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val navController = findNavController(R.id.navHostFragment)
        setupActionBarWithNavController(this, navController)
        navController.addOnNavigatedListener { _, _ ->
            resetAppbar() // Reset appbar to default when fragment changed
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    private fun resetAppbar() {
        title = getString(R.string.app_name)
        hideAppbarElevation()
    }

    fun showAppbarElevation() {
        supportActionBar?.elevation = scrollAppbarElevation
    }

    fun hideAppbarElevation() {
        supportActionBar?.elevation = defaultAppbarElevation
    }
}
