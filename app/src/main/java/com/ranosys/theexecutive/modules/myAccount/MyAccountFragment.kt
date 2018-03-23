package com.ranosys.theexecutive.modules.myAccount

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.MyAccountOptionItemBinding
import com.ranosys.theexecutive.utils.FragmentUtils
import kotlinx.android.synthetic.main.fragment_my_account.*

/**
 * Created by nikhil on 22/3/18.
 */
class MyAccountFragment: BaseFragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity)
        my_account_options_list.layoutManager = linearLayoutManager

        val itemDecor = DividerDecoration(resources.getDrawable(R.drawable.horizontal_divider, null))
        my_account_options_list.addItemDecoration(itemDecor)

        my_account_options_list.adapter = MyAccountAdapter(getAccountOptions(), activity as Context)

    }

    override fun onResume() {
        super.onResume()
        setToolBarParams(getString(R.string.my_account_title), 0 , false, 0 ,false)
    }

    private fun getAccountOptions(): List<MyAccountDataClass.MyAccountOption> {
        var optionList = ArrayList<MyAccountDataClass.MyAccountOption>()

        val titleArray = resources.getStringArray(R.array.my_account_option_title_array)
        val iconArray = resources.getIntArray(R.array.my_account_option_icon_array)

        (0..(titleArray.size -1)).mapTo(optionList) { MyAccountDataClass.MyAccountOption(titleArray[it], iconArray[it]) }

        return optionList
    }

    class MyAccountAdapter(val optionArray: List<MyAccountDataClass.MyAccountOption>, val context: Context): RecyclerView.Adapter<MyAccountAdapter.MyAccountHolder>(){
        override fun onBindViewHolder(holder: MyAccountHolder?, position: Int) {
            holder?.bind(optionArray[position], context)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountHolder {
            val binding: MyAccountOptionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.my_account_option_item, parent,false)
            return MyAccountAdapter.MyAccountHolder(binding)
        }

        override fun getItemCount() = optionArray.size


        class MyAccountHolder(val itemBinding: MyAccountOptionItemBinding): RecyclerView.ViewHolder(itemBinding.root) {


            fun bind(option: MyAccountDataClass.MyAccountOption, context: Context){
                itemBinding.option = option

                itemView.setOnClickListener {
                    //TODO - move to respective screen using option title
                    when(option.title){
                        context.getString(R.string.news_letter_option) -> {
                            FragmentUtils.addFragment(context, NewsLetterFragment(),NewsLetterFragment::class.java.name )
                        }
                    }
                }
            }
        }

    }
}

