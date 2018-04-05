package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * Created by Mohammad Sunny on 5/4/18.
 */
class ProductDetailViewModel(application: Application): BaseViewModel(application) {

    var clickedAddBtnId: MutableLiveData<Int>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }

    fun btnClicked(view: View) {
        when (view.id) {
            R.id.btn_add_to_bag -> {
                clickedAddBtnId?.value = R.id.btn_add_to_bag
            }
        }
    }
}