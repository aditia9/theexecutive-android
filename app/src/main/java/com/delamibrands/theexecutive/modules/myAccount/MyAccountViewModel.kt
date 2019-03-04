package com.delamibrands.theexecutive.modules.myAccount

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.delamibrands.theexecutive.api.AppRepository
import com.delamibrands.theexecutive.api.interfaces.ApiCallback
import com.delamibrands.theexecutive.base.BaseViewModel
import com.delamibrands.theexecutive.utils.Constants
import com.delamibrands.theexecutive.utils.GlobalSingelton
import com.delamibrands.theexecutive.utils.SavedPreferences

/**
 * @Details
 * @Author Ranosys Technologies
 * @Date 29-Jun-2018
 */
class MyAccountViewModel(application: Application) : BaseViewModel(application){

    var notificationCount: MutableLiveData<Int> = MutableLiveData()

    fun getNotificationCount() {

        val deviceId = SavedPreferences.getInstance()?.getStringValue(Constants.ANDROID_DEVICE_ID_KEY)

        deviceId?.run {
            val request = MyAccountDataClass.NotificationCountRequest(
                    deviceId = deviceId
            )
            AppRepository.notificationCount(request, object: ApiCallback<Int> {
                override fun onException(error: Throwable) {

                }

                override fun onError(errorMsg: String) {

                }

                override fun onSuccess(t: Int?) {
                    GlobalSingelton.instance?.notificationCount?.set(t)
                    notificationCount.value = t
                }

            })
        }

    }


}