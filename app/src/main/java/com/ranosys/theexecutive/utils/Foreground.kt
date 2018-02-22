package com.ranosys.theexecutive.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by ranosys on 24/1/18.
 */
class Foreground: Application.ActivityLifecycleCallbacks {

    private var foreground = false
    private var paused = true
    private var handler: Handler = Handler()
    private var listener: CopyOnWriteArrayList<Listener> = CopyOnWriteArrayList<Listener>()
    private var check: Runnable? = null
    //private val CHECK_DELAY = 500

    companion object {
        var CHECK_DELAY = 500
        var TAG = Foreground.javaClass.name
        var instance: Foreground? = null
        fun init(application: Application):Foreground{
            if(instance == null){
                instance = Foreground()
                application.registerActivityLifecycleCallbacks(instance)
            }
            return instance as Foreground
        }
        fun get(application: Application):Foreground{
            if(instance == null){
                init(application)
            }
            return instance as Foreground
        }
        fun get(context: Context):Foreground{
            if(instance == null){
                var ctx = context.applicationContext
                if(ctx is Application){
                    init(ctx )
                }
                throw IllegalStateException(
                        "Foreground is not initialised and " +
                                "cannot obtain the Application object");
            }
            return instance as Foreground
        }
        fun get(): Foreground{
            if(instance == null){
                throw IllegalStateException(
                        "Foreground is not initialised - invoke " + "at least once with parameterised init/get")
            }
            return instance as Foreground
        }
    }
    interface Listener{
        fun onBecameForeground()
        fun onBecameBackgourn()
    }
    fun addListener(listn: Listener){
        listener.add(listn)
    }
    fun removeListener(listn: Listener){
        listener.remove(listn)
    }

    override fun onActivityPaused(p0: Activity?) {
        paused = true
        handler.removeCallbacks(check)
        handler.postDelayed(Runnable { kotlin.run {
            if(foreground && paused){
                foreground = false
                for(l in listener){
                    try {
                        l.onBecameBackgourn()
                    }catch (e: Exception){
                        Log.e(TAG, "Exception threw")
                    }
                }
            }else{
                Log.i(TAG, "still foreground")
            }
        } }, CHECK_DELAY.toLong())
    }

    override fun onActivityResumed(p0: Activity?) {
        paused = false
        var wasBackground = !foreground
        foreground = true
        handler.removeCallbacks(check)
        if(wasBackground){
            for(l in listener){
                try {
                    l.onBecameBackgourn()
                }catch (e: Exception){
                    Log.e(TAG, "Listner threw exception")
                }
            }
        }else{
            Log.e(TAG, "still foreground")
        }
    }

    override fun onActivityStarted(p0: Activity?) {
    }

    override fun onActivityDestroyed(p0: Activity?) {
    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
    }

    override fun onActivityStopped(p0: Activity?) {
    }

    override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
    }
}