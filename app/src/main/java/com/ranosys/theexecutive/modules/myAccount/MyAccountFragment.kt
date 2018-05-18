package com.ranosys.theexecutive.modules.myAccount

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.MyAccountOptionItemBinding
import com.ranosys.theexecutive.modules.addressBook.AddressBookFragment
import com.ranosys.theexecutive.modules.changeLanguage.ChangeLanguageFragment
import com.ranosys.theexecutive.modules.changePassword.ChangePasswordFragment
import com.ranosys.theexecutive.modules.checkout.CheckoutFragment
import com.ranosys.theexecutive.modules.myInformation.MyInformationFragment
import com.ranosys.theexecutive.modules.newsLetter.NewsLetterFragment
import com.ranosys.theexecutive.utils.DialogOkCallback
import com.ranosys.theexecutive.utils.FragmentUtils
import com.ranosys.theexecutive.utils.Utils
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.logout_btn.view.*

/**
 * Created by nikhil on 22/3/18.
 */
class MyAccountFragment : BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity as Context)
        my_account_options_list.layoutManager = linearLayoutManager

        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        my_account_options_list.addItemDecoration(itemDecor)
        my_account_options_list.adapter = MyAccountAdapter(getAccountOptions(), activity as Context)

    }

    private fun getAccountOptions(): List<MyAccountDataClass.MyAccountOption> {
        val optionList = ArrayList<MyAccountDataClass.MyAccountOption>()

        val titleArray = resources.getStringArray(R.array.my_account_option_title_array)
        val iconArray = resources.obtainTypedArray(R.array.my_account_option_icon_array)

        for (i in 0..(titleArray.size - 1)) {
            optionList.add(MyAccountDataClass.MyAccountOption(titleArray[i], iconArray.getResourceId(i, -1)))
        }

        return optionList
    }

    class MyAccountAdapter(val optionArray: List<MyAccountDataClass.MyAccountOption>, val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val VIEW_TYPE_FOOTER = 2
            const val VIEW_TYPE_ITEM = 1
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is MyAccountHolder) {
                holder.bind(optionArray[position], context)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            if (viewType == VIEW_TYPE_FOOTER) {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.logout_btn, parent, false)
                return MyAccountFooterHolder(itemView, context)

            } else {
                val binding: MyAccountOptionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_account_option_item, parent, false)
                return MyAccountAdapter.MyAccountHolder(binding)
            }

        }

        override fun getItemViewType(position: Int): Int {
            return if (position == optionArray.size) VIEW_TYPE_FOOTER else VIEW_TYPE_ITEM
        }

        override fun getItemCount() = optionArray.size + 1


        class MyAccountHolder(val itemBinding: MyAccountOptionItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

            fun bind(option: MyAccountDataClass.MyAccountOption, context: Context) {
                itemBinding.option = option

                itemView.setOnClickListener {
                    when (option.title) {
                        context.getString(R.string.news_letter_option) -> {
                            FragmentUtils.addFragment(context, NewsLetterFragment(), null, NewsLetterFragment::class.java.name, true)
                        }
                        context.getString(R.string.change_password) -> {
                            FragmentUtils.addFragment(context, ChangePasswordFragment(), null, ChangePasswordFragment::class.java.name, true)
                        }

                        context.getString(R.string.my_information_option) ->{
                            FragmentUtils.addFragment(context, MyInformationFragment(),null, MyInformationFragment::class.java.name, true )
                        }

                        context.getString(R.string.select_lang_title) ->{
                            //FragmentUtils.addFragment(context, ChangeLanguageFragment(),null, ChangeLanguageFragment::class.java.name, true )
                            FragmentUtils.addFragment(context, CheckoutFragment(),null, CheckoutFragment::class.java.name, true )
                        }

                        context.getString(R.string.shipping_address) ->{
                            FragmentUtils.addFragment(context, AddressBookFragment(),null, AddressBookFragment::class.java.name, true )
                        }

                        context.getString(R.string.buying_guide) ->{
                            val url = ""
                            openWebPage(context, url, context.getString(R.string.buying_guide))
                        }

                        context.getString(R.string.contact_us) ->{
                            val url = ""
                            openWebPage(context, url, context.getString(R.string.contact_us))
                        }

                    }
                }
            }

            private fun openWebPage(context: Context, url: String, title: String) {
                val fragment = FragmentUtils.getCurrentFragment(context as BaseActivity)
                fragment?.run {
                    (fragment as BaseFragment).prepareWebPageDialog(context, "http://magento.theexecutive.co.id/" , title)
                }

            }
        }

        class MyAccountFooterHolder(val item: View, context: Context) : RecyclerView.ViewHolder(item) {
            init {
                itemView.btn_logout.setOnClickListener({
                    Utils.showDialog(context, context.getString(R.string.logout_text),
                            context.getString(R.string.yes), context.getString(R.string.no), object : DialogOkCallback {
                        override fun setDone(done: Boolean) {

                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(context.getString(R.string.gmail_server_client_id))
                                    .requestEmail()
                                    .requestProfile()
                                    .build()

                            val mGoogleSignInClient = GoogleSignIn.getClient(context as Activity, gso)
                            Utils.logout(item.context, mGoogleSignInClient)

                        }
                    })
                })
            }
        }
    }
}

