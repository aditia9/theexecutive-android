package com.ranosys.theexecutive.activities

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableField
import android.view.View
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseViewModel


/**
 * Created by nikhil on 7/3/18.
 */
class ToolbarViewModel(application: Application): BaseViewModel(application) {

    var title: ObservableField<String> = ObservableField<String>()
    var leftIcon: ObservableField<Int> = ObservableField<Int>()
    var isLeftIconVisible : ObservableField<Boolean> = ObservableField()
    var rightIcon: ObservableField<Int> = ObservableField<Int>()
    var isRightIconVisible : ObservableField<Boolean> = ObservableField()
    var leftIconClicked: MutableLiveData<Int>? = MutableLiveData()


    init {
        leftIcon.set(android.R.color.transparent)
        isLeftIconVisible.set(true)
        rightIcon.set(android.R.color.transparent)
        isRightIconVisible.set(true)
    }

    fun onIconClick(view : View){
        when(view.id){
            R.id.toolbar_left_icon->{
                leftIconClicked?.value = view.id
            }
        }
    }

}