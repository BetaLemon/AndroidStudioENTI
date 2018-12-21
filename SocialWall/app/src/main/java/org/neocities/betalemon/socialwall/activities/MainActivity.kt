package org.neocities.betalemon.socialwall.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import org.neocities.betalemon.socialwall.fragments.BoardFragment
import org.neocities.betalemon.socialwall.fragments.NewsFragment
import org.neocities.betalemon.socialwall.fragments.ProfileFragment
import org.neocities.betalemon.socialwall.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabs.setOnNavigationItemSelectedListener {tab->

            lateinit var fragment: Fragment
            when(tab.itemId){
                R.id.tab_board ->{
                    fragment = BoardFragment()
                }
                R.id.tab_news ->{
                    fragment = NewsFragment()
                }
                R.id.tab_profile ->{
                    fragment = ProfileFragment()
                }
                else->{
                    fragment = BoardFragment()
                    Log.e("MainActivity", "Trying to go to non existing fragment with tabId " + tab.itemId)
                }
            }
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.commit()
            true
        }

        tabs.selectedItemId = R.id.tab_board
    }
}
