package com.zacharee1.systemuituner.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.mikepenz.materialdrawer.util.ExperimentalNavController
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.dialogs.AnimatedMaterialAlertDialogBuilder
import com.zacharee1.systemuituner.fragments.intro.ExtraPermsSlide
import com.zacharee1.systemuituner.util.addAnimation
import com.zacharee1.systemuituner.util.hasDump
import kotlinx.android.synthetic.main.activity_persistent.*

class DemoModeActivity : AppCompatActivity() {
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_demo_mode)

        setSupportActionBar(toolbar)
        toolbar.addAnimation()

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!hasDump) {
            ExtraPermsRetroactive.start(this, ExtraPermsSlide::class.java)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @ExperimentalNavController
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.search)
        searchItem.isVisible = false

        val helpItem = menu.findItem(R.id.help)
        helpItem.isVisible = true
        helpItem.setOnMenuItemClickListener {
            AnimatedMaterialAlertDialogBuilder(this)
                .setTitle(R.string.help)
                .setMessage(R.string.sub_demo_desc)
                .setPositiveButton(android.R.string.ok, null)
                .show()
            false
        }

        return true
    }
}