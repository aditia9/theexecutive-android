package com.ranosys.theexecutive.modules.bankTransfer

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.dochelper.MediaCallbackManager
import com.ranosys.dochelper.MediaHelperActivity
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.base.BaseFragment
import com.ranosys.theexecutive.databinding.FragmentBankTransferBinding
import com.ranosys.theexecutive.utils.Constants
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.MediaType
import okhttp3.RequestBody
import android.provider.MediaStore
import com.ranosys.rtp.Helper
import com.ranosys.theexecutive.base.BaseActivity


class BankTransferFragment : BaseFragment(), DatePickerDialog.OnDateSetListener{
    private lateinit var bankTransferViewModel: BankTransferViewModel
    private lateinit var mBinding : FragmentBankTransferBinding
    private lateinit var mediaPicker: MediaHelperActivity
    lateinit var requestFile : RequestBody


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank_transfer, container, false)
        bankTransferViewModel = ViewModelProviders.of(this).get(BankTransferViewModel::class.java)
        mBinding.bankTransferVM = bankTransferViewModel

        bankTransferViewModel.getRecipientsList()
        bankTransferViewModel.getTransferMethodList()
        mediaPicker = initMediaPicker()

        mBinding.imgAttachment.setOnClickListener{view ->

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
            bankTransferViewModel.submitBankTransfer(requestFile)
        }
        val calender: Calendar = Calendar.getInstance()
        calender.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE))
        val transferDate: Date = calender.time
        bankTransferViewModel.transferDate.set(transferDate)
        val dateFormat = SimpleDateFormat(Constants.DD_MM_YY_DATE_FORMAT)
        mBinding.etTransferDate.setText(dateFormat.format(transferDate))

        mBinding.etTransferDate.setOnClickListener {
            showDate(Calendar.getInstance().get(Calendar.YEAR) - Constants.MINIMUM_AGE, 0, 1, R.style.DatePickerSpinner)
        }

        observeApiFailure()
        return mBinding.root
    }



    private fun observeApiFailure() {
        bankTransferViewModel.transferMethodsLabelObserVer?.observe(this, android.arch.lifecycle.Observer {
          mBinding.spTransferMethod.setSelection(0)

        })
    }
    private fun showDate(year: Int, monthOfYear: Int, dayOfMonth: Int, spinnerTheme: Int) {
        val dpd = SpinnerDatePickerDialogBuilder()
                .context(activity)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .year(year)
                .finalYear(Calendar.getInstance().get(Calendar.YEAR) - 0)
                .monthOfYear(monthOfYear)
                .dayOfMonth(dayOfMonth)
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
        mBinding.etTransferDate.setText(dateFormat.format(transferDate))    }


    private fun openAttachment() {
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = AlertDialog.Builder(activity as Context)
        builder.setTitle("Add Photo!")
        builder.setItems(items, { dialog, item ->

            if (items[item] == "Take Photo") {
                mediaPicker.chooseFromCamera(false, object : MediaCallbackManager.MediaCallback {
                    override fun onMediaSelected(uri: Uri, path: String) {

                        mBinding.imgAttachment.setImageURI(uri)
                        bankTransferViewModel.attachmentFile = File(getRealPathFromUri(activity as Context, uri))

                         requestFile = RequestBody.create(
                                MediaType.parse(getRealPathFromUri(activity as Context, uri)),
                                File(getRealPathFromUri(activity as Context, uri))
                        )
                        dialog.dismiss()
                    }
                })
            } else if (items[item] == "Choose from Library") {
                mediaPicker.chooseFromGallery(true, false, object : MediaCallbackManager.MediaCallback {
                    override fun onMediaSelected(uri: Uri, path: String) {
                        mBinding.imgAttachment.setImageURI(uri)
                        bankTransferViewModel.attachmentFile = File(uri.toString())

                        dialog.dismiss()
                    }
                })
            } else if (items[item] == "Cancel") {
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
}