package com.ranosys.theexecutive.fragments.Profile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.firebase.database.FirebaseDatabase
import com.ranosys.theexecutive.BR
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.utils.SavedPreferences
import kotlinx.android.synthetic.main.fragment_profile.*

/**
 * Created by Mohammad Sunny on 5/2/18.
 */
class ProfileFragment: BaseFragment() {

    var savedPreferences : SavedPreferences? = null
    var profileViewModel: ProfileViewModel? = null
    var userMailId : String? = null
    var user: ProfileDataClass.UserProfile? = null
    var database : FirebaseDatabase? = null

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
    }

    override fun onResume() {
        super.onResume()
        setTitle()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu?.clear()
        getActivity().getMenuInflater().inflate(R.menu.fragment_menu, menu);
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_edit -> {
                if(item.title == "Edit"){
                    enableFields(true)
                    item.title = "Save"
                    return true
                }else{
                    profileViewModel?.saveUserProfile()
                    item.title = "Edit"
                    enableFields(false)
                    return true
                }
            }
            else-> {
             return  super.onOptionsItemSelected(item)
            }
        }
    }

    private fun saveProfileDetails() {

    }

    private fun enableFields(isenable: Boolean) {
        tv_user_name.isEnabled = isenable
        et_bloodgroup.isEnabled = isenable
        et_usercity.isEnabled = isenable
        et_gender.isEnabled = isenable
        et_usermobile.isEnabled = isenable
        et_userstate.isEnabled = isenable
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        profileViewModel = ProfileViewModel(activity.application, database!!)
        profileViewModel!!.getFromServer()
        observeUserData()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun observeUserData() {
        profileViewModel?.userData?.observe(this, Observer<ProfileDataClass.UserProfile>{
            data ->
            Log.e("Username",data?.name)
            profileViewModel?.name?.set(data?.name)
            profileViewModel?.emailId?.set(data?.email)
            profileViewModel?.gender?.set(data?.gender)
            profileViewModel?.city?.set(data?.city)
            profileViewModel?.state?.set(data?.state)
            profileViewModel?.mobile?.set(data?.mobile)
        })
    }


    override fun getTitle(): String? {
        return "Profile"
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile
    }

    override fun getBindingVariable(): Int {
        return BR.userInfo
    }

}