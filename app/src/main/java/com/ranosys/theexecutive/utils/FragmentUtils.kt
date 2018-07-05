package com.ranosys.theexecutive.utils

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.DashBoardActivity
import com.ranosys.theexecutive.base.BaseActivity
import java.util.*

/**
 * Created by Mohammad Sunny on 22/2/18.
 */
object FragmentUtils {

    private var sFragmentStack: Stack<String>? = Stack()

    fun addFragment(context: Context?, fragment: Fragment?, bundle : Bundle?, fragmentId: String?, isAdded : Boolean?) : Fragment?{
        return fragment?.apply {
            context?.run {
                val activity: BaseActivity = context as BaseActivity
                if (fragment != getCurrentFragment(activity)) {
                    bundle?.run {
                        fragment.arguments = bundle
                    }
                    val transaction = activity.supportFragmentManager.beginTransaction()
                    transaction.add(R.id.main_container, fragment, fragmentId)
                    if (isAdded!!) {
                        transaction.addToBackStack(fragmentId)
                    }
                    transaction.commit()
                    sFragmentStack?.add(fragmentId)
                }
            }

        }
    }

    fun replaceFragment(context: Context, fragment: Fragment, bundle : Bundle?, fragmentId: String, isAdded : Boolean){
        val activity: BaseActivity = context as BaseActivity
        if(fragment != getCurrentFragment(activity)){
            bundle?.run{
                fragment.arguments = bundle
            }
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_container, fragment, fragmentId)
            if(isAdded){
                transaction.addToBackStack(fragmentId)
            }
            transaction.commit()
            sFragmentStack?.add(fragmentId)
        }
    }

    fun getCurrentFragment(baseActivity: BaseActivity): Fragment? {
        return baseActivity.supportFragmentManager.findFragmentById(R.id.main_container)
    }

    fun popFragment(baseActivity: DashBoardActivity){
        baseActivity.supportFragmentManager.popBackStack()
    }
}