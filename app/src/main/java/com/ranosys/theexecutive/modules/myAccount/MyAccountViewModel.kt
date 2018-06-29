package com.ranosys.theexecutive.modules.myAccount

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.SavedPreferences

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
            AppRepository.notificationCount(request, object: ApiCallback<Int>{
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