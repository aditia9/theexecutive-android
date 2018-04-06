package com.ranosys.theexecutive.modules.productDetail

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseViewModel

/**
 * @Class ViewModel for product item.
 * @author Ranosys Technologies
 * @Date 06-Apr-2018
 */
class ProductItemViewModel(application: Application) : BaseViewModel(application){

    var clickedAddBtnId: MutableLiveData<ViewClass>? = null
        get() {
            field =  field ?: MutableLiveData()
            return field
        }

    fun btnClicked(view: View) {
        when (view.id) {
            R.id.btn_add_to_bag -> {
                clickedAddBtnId?.value = ViewClass(R.id.btn_add_to_bag, view.tag as Int)
            }
        }
    }

   data class ViewClass( var id : Int,var tag : Int)

}