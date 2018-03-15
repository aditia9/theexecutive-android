package com.ranosys.theexecutive.utils

import android.content.Context
import android.support.v4.app.Fragment
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import java.util.*

/**
 * Created by Mohammad Sunny on 22/2/18.
 */
object FragmentUtils {

        var sFragmentStack: Stack<String>? = Stack()

        fun addFragment(context: Context, fragment: Fragment, fragmentId: String){
            val activity: BaseActivity = context as BaseActivity
            if(fragment != getCurrentFragment(activity)){
                activity.supportFragmentManager.beginTransaction()
                        .add(R.id.main_container, fragment, fragmentId)
                        .addToBackStack(fragmentId)
                        .commit()
                       sFragmentStack?.add(fragmentId)
            }
        }

        fun replaceFragment(context: Context, fragment: Fragment, fragmentId: String){
            val activity: BaseActivity = context as BaseActivity
            if(fragment != getCurrentFragment(activity)){
                activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.main_container, fragment, fragmentId)
                        .addToBackStack(fragmentId)
                        .commit()
                sFragmentStack?.add(fragmentId)
            }
        }

        fun getCurrentFragment(baseActivity: BaseActivity): Fragment? {
            val currentFragment = baseActivity.supportFragmentManager.findFragmentById(R.id.main_container)
            return currentFragment
        }

}