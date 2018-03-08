package com.ranosys.theexecutive.activities

import android.app.Application
import android.databinding.ObservableField
import com.ranosys.theexecutive.base.BaseViewModel


/**
 * Created by nikhil on 7/3/18.
 */
class ToolbarViewModel(application: Application): BaseViewModel(application) {

    var title: ObservableField<String> = ObservableField<String>()
    var icon: ObservableField<Int> = ObservableField<Int>()
    var isIconVisible : ObservableField<Boolean> = ObservableField()

    init {
        icon.set(android.R.color.transparent)
        isIconVisible.set(true)
    }

}