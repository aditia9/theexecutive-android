package com.ranosys.theexecutive.modules.bankTransfer

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.ranosys.rtp.Helper
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.activities.DashBoardActivity
import com.ranosys.theexecutive.api.ApiResponse
import com.ranosys.theexecutive.base.BaseActivity
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentBankTransferBinding
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @Details fragment shows BankTransferFragment
 * @Author Ranosys Technologies
 * @Date 1, June,2018
 */
class BankTransferFragment : BaseFragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var bankTransferViewModel: BankTransferViewModel
    private lateinit var mBinding: FragmentBankTransferBinding
    private lateinit var mediaPicker: MediaHelperActivity


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank_transfer, container, false)
        bankTransferViewModel = ViewModelProviders.of(this).get(BankTransferViewModel::class.java)
        mBinding.bankTransferVM = bankTransferViewModel

        bankTransferViewModel.getRecipientsList()
        bankTransferViewModel.getTransferMethodList()
        mediaPicker = (activity as DashBoardActivity).initMediaPicker()

        mBinding.imgAttachment.setOnClickListener { view ->

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) run {
                val permissionList = java.util.ArrayList<String>()
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                permissionList.add(Manifest.permission.CAMERA)

                (activity as BaseActivity).getPermission(permissionList, { isAllPermissionGranted ->
                    if (isAllPermissionGranted) {
                        openAttachment()
                    } else {
                        Helper.showAppInfo(activity as BaseActivity)
                    }
                })

            } else {
                openAttachment()
            }
        }

        mBinding.btnSubmit.setOnClickListener { view ->
            showLoading()
            bankTransferViewModel.submitBankTransfer()
        }


        mBinding.etTransferDate.setOnClickListener {
            showDate(Calendar.getInstance().get(Calendar.YEAR) - 1, 0, 1, R.style.DatePickerSpinner)
        }

        observeEvents()

        return mBinding.root
    }


    private fun observeEvents() {
        bankTransferViewModel.transferMethodsLabelObserVer?.observe(this, android.arch.lifecycle.Observer {
            mBinding.spTransferMethod.setSelection(0)

        })



        bankTransferViewModel.bankTransferObservable.observe(this, android.arch.lifecycle.Observer<ApiResponse<String>> { apiResponse ->
            hideLoading()
            if (apiResponse?.error.isNullOrEmpty()) {
                val response = apiResponse?.apiResponse
                if (response is String) {
                    Toast.makeText(activity, response, Toast.LENGTH_LONG).show()

                }
            } else {
                Utils.showDialog(activity, apiResponse?.error, getString(android.R.string.ok), "", null)
            }
        })

    }

    private fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        val dpd = SpinnerDatePickerDialogBuilder()
                .context(activity)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .year(year)
                .monthOfYear(monthOfYear)
                .dayOfMonth(dayOfMonth)
                .defaultStartYear(Calendar.getInstance().get(Calendar.YEAR) - 1)
                .build()

        dpd.show()
        dpd.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(activity as Context, R.color.black))
        dpd.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(activity as Context, R.color.black))
    }

    override fun onDateSet(view: com.tsongkha.spinnerdatepicker.DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calender: Calendar = Calendar.getInstance()
        calender.set(year, monthOfYear, dayOfMonth)
        val transferDate: Date = calender.time
        bankTransferViewModel.transferDate.set(transferDate)
        val dateFormat = SimpleDateFormat(Constants.DD_MM_YY_DATE_FORMAT)
        mBinding.etTransferDate.setText(dateFormat.format(transferDate))
    }


    private fun openAttachment() {
        val items = (activity as Context).resources.getStringArray(R.array.attachment_option_array)
        val builder = AlertDialog.Builder(activity as Context)
        builder.setTitle((activity as Context).getString(R.string.add_attachment))
        builder.setItems((activity as Context).resources.getStringArray(R.array.attachment_option_array), { dialog, item ->

            if (items[item] == (activity as Context).resources.getStringArray(R.array.attachment_option_array)[0]) {
                mediaPicker.chooseFromCamera(false, object : MediaCallbackManager.MediaCallback {
                    override fun onMediaSelected(uri: Uri, path: String) {

                        mBinding.imgAttachment.setImageURI(uri)
                        bankTransferViewModel.attachmentFile = File(getRealPathFromUri(activity as Context, uri))
                        dialog.dismiss()
                    }
                })
            } else if (items[item] == (activity as Context).resources.getStringArray(R.array.attachment_option_array)[1]) {
                mediaPicker.chooseFromGallery(false, false, object : MediaCallbackManager.MediaCallback {
                    override fun onMediaSelected(uri: Uri, path: String) {
                        mBinding.imgAttachment.setImageURI(uri)
                        bankTransferViewModel.attachmentFile = File(getRealPathFromUri(activity as Context, uri))

                        dialog.dismiss()
                    }
                })
            } else if (items[item] == (activity as Context).resources.getStringArray(R.array.attachment_option_array)[2]) {
                dialog.dismiss()
            }
        })
        builder.show()
    }

    fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setToolBarParams(getString(R.string.bank_transfer), 0, "", R.drawable.back, true, 0, false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mediaPicker.onCallbackResult(requestCode, resultCode, data)

    }
}