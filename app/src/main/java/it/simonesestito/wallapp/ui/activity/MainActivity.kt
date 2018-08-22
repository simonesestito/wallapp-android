package it.simonesestito.wallapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.backend.service.PreviewService
import it.simonesestito.wallapp.utils.TAG

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

    override fun onStart() {
        super.onStart()
        stopService(Intent(this, PreviewService::class.java))
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_VIEW) {
            Log.d(TAG, "Received VIEW Intent with url: ${intent.data}")
            findNavController(R.id.navHostFragment).onHandleDeepLink(intent)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        hideAppbarElevation()
        val navController = findNavController(R.id.navHostFragment)
        setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    fun showAppbarElevation() {
        supportActionBar?.elevation = scrollAppbarElevation
    }

    fun hideAppbarElevation() {
        supportActionBar?.elevation = defaultAppbarElevation
    }
}
