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

    var title: ObservableField<String> = ObservableField()
    var subTitle: ObservableField<String> = ObservableField()
    var leftIcon: ObservableField<Int> = ObservableField()
    var isLeftIconVisible : ObservableField<Boolean> = ObservableField()
    var rightIcon: ObservableField<Int> = ObservableField()
    var cartCount: ObservableField<Int> = ObservableField()
    var isRightIconVisible : ObservableField<Boolean> = ObservableField()
    var showLogo : ObservableField<Boolean> = ObservableField()
    var titleBackground : ObservableField<Int> = ObservableField()
    var leftIconClicked: MutableLiveData<Int>? = MutableLiveData()
    var rightIconClicked: MutableLiveData<Int>? = MutableLiveData()


    init {
        subTitle.set("")
        leftIcon.set(android.R.color.transparent)
        isLeftIconVisible.set(true)
        rightIcon.set(android.R.color.transparent)
        cartCount.set(0)
        isRightIconVisible.set(true)
        titleBackground.set(0)
        showLogo.set(false)
    }

    fun onIconClick(view : View){
        when(view.id){
            R.id.toolbar_left_icon->{
                leftIconClicked?.value = view.id
            }

            R.id.toolbar_right_icon->{
                rightIconClicked?.value = view.id
            }
        }
    }

}