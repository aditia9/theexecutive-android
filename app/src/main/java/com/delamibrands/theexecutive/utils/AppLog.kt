import AppLog.APP_LOG_TAG
import android.util.Log
import com.delamibrands.theexecutive.BuildConfig

/**
 * Created by Sunny on 27/3/18.
 *
 * An Android Logging class.
 * This class provides all @Log class basic functions including basic TAG or custom TAG.
 * Also supports for printStackTrace() Methods for @Exception class.
 *
 * @property APP_LOG_TAG the basic tag you want to provide for your project.
 */
object AppLog {

    const val APP_LOG_TAG: String = "DELAMI_BRANDS"

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg)
        }
    }

    fun d(msg: String) {
        this.d(APP_LOG_TAG, msg)
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun e(msg: String) {
        this.d(APP_LOG_TAG, msg)
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg)
        }
    }

    fun w(msg: String) {
        this.d(APP_LOG_TAG, msg)
    }

    fun printStackTrace(ex: Exception) {
        if (BuildConfig.DEBUG) {
            ex.printStackTrace()
        }
    }

    fun printStackTrace(ex: Throwable) {
        if (BuildConfig.DEBUG) {
            ex.stackTrace
        }
    }


}

