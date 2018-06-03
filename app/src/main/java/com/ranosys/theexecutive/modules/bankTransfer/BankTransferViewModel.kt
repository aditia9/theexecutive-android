package com.ranosys.theexecutive.modules.bankTransfer

import android.annotation.SuppressLint
import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.databinding.ObservableField
import android.support.design.widget.TextInputEditText
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.api.AppRepository
import com.ranosys.theexecutive.api.interfaces.ApiCallback
import com.ranosys.theexecutive.base.BaseViewModel
import com.ranosys.theexecutive.modules.register.RegisterViewModel
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.Utils
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 * @Details fragment shows BankTransfer View Model
 * @Author Ranosys Technologies
 * @Date 1, June,2018
 */
class BankTransferViewModel(application: Application) : BaseViewModel(application) {
    var firstName: ObservableField<String> = ObservableField()
    var lastName: ObservableField<String> = ObservableField()
    var firstNameError: ObservableField<String> = ObservableField()
    var lastNameError: ObservableField<String> = ObservableField()
    var emailAddress: ObservableField<String> = ObservableField()
    var emailAddressError: ObservableField<String> = ObservableField()
    var orderNumber: ObservableField<String> = ObservableField()
    var orderNumberError: ObservableField<String> = ObservableField()
    var bankNumber: ObservableField<String> = ObservableField()
    var bankNumberError: ObservableField<String> = ObservableField()
    var holderAccountNumber: ObservableField<String> = ObservableField()
    var holderAccountError: ObservableField<String> = ObservableField()
    var transferAmount: ObservableField<String> = ObservableField()
    var transferAmountError: ObservableField<String> = ObservableField()
    var transferDate: ObservableField<Date> = ObservableField()
    var transferDateError: ObservableField<String> = ObservableField()

    var transferMethodsList: MutableList<TransferMethodsDataClass> = mutableListOf()
    var recipientsLabel: MutableList<String> = mutableListOf()
    var transferMethodsLabel: MutableList<String> = mutableListOf()
    var transferMethodsLabelObserVer: MutableLiveData<List<TransferMethodsDataClass>>? = MutableLiveData()
    var recipientsList: MutableList<Recipients> = mutableListOf()
    private lateinit var recipient: String
    private lateinit var transferMethods: String
    var attachmentFile: File? = null

    private val recipients: Recipients = Recipients(value = Constants.BANK_RECIPIENT_LABEL, label = Constants.BANK_RECIPIENT_LABEL)
    private val transferMethodsData: TransferMethodsDataClass = TransferMethodsDataClass(value = Constants.TRANSFER_METHOD_LABEL, label = Constants.TRANSFER_METHOD_LABEL)


    init {
        recipientsList.add(recipients)
        recipientsLabel.add(Constants.BANK_RECIPIENT_LABEL)
        transferMethodsList.add(transferMethodsData)
        transferMethodsLabel.add(Constants.TRANSFER_METHOD_LABEL)
    }

    fun getTransferMethodList() {
        AppRepository.getBankTransferMethod(object : ApiCallback<List<TransferMethodsDataClass>> {
            override fun onException(error: Throwable) {
                Utils.printLog(RegisterViewModel.COUNTRY_API_TAG, error.message!!)
            }

            override fun onError(errorMsg: String) {
                Utils.printLog(RegisterViewModel.COUNTRY_API_TAG, errorMsg)
            }

            override fun onSuccess(transferMethods: List<TransferMethodsDataClass>?) {

                transferMethodsLabelObserVer?.value = transferMethods
                if (null != transferMethods && transferMethods.isNotEmpty()) {
                    transferMethodsList.addAll(transferMethods as ArrayList<TransferMethodsDataClass>)

                    val length = transferMethodsList.size
                    for (i in 1..length - 1) {
                        transferMethodsLabel.add(i, transferMethodsList[i].value)
                    }
                }
            }
        })
    }


    fun getRecipientsList() {
        AppRepository.getRecipient(object : ApiCallback<List<Recipients>> {
            override fun onException(error: Throwable) {
                Utils.printLog(RegisterViewModel.COUNTRY_API_TAG, error.message!!)
            }

            override fun onError(errorMsg: String) {
                Utils.printLog(RegisterViewModel.COUNTRY_API_TAG, errorMsg)
            }

            override fun onSuccess(recipients: List<Recipients>?) {
                if (null != recipients && recipients.isNotEmpty()) {
                    recipientsList.addAll(recipients as ArrayList<Recipients>)

                    val length = recipientsList.size
                    for (i in 1..length - 1) {
                        recipientsLabel.add(i, recipientsList[i].value)
                    }

                }
            }
        })
    }


    fun onTextChanged(et: TextInputEditText){
        when(et.id){
            R.id.et_first_name -> firstNameError.set("")
            R.id.et_last_name -> lastNameError.set("")
            R.id.et_email -> emailAddressError.set("")
            R.id.et_order_number -> orderNumberError.set("")
            R.id.et_bank_number -> bankNumberError.set("")
            R.id.et_holder_acc_number -> holderAccountError.set("")
            R.id.et_transfer_amount -> transferAmountError.set("")
        }
    }

    /*  date = SimpleDateFormat(Constants.YY_MM__DD_DATE_FORMAT).format(transferDate.get()),*/
    @SuppressLint("SimpleDateFormat")
    fun submitBankTransfer(requestFile: RequestBody?) {
        if (isValidData(getApplication())) {


            val bankTransferRequest = BankTransferRequest(
                    name = firstName.get() + " " + lastName.get(),
                    email_submitter = emailAddress.get(),
                    orderid = orderNumber.get(),
                    bank_name = bankNumber.get(),
                    holder_account = holderAccountNumber.get(),
                    amount = transferAmount.get(),
                    recipient = recipient,
                    method = transferMethods,
                    date = "2018-05-29 21:05:56"
            )

            AppRepository.submitBankTransfer(requestFile, attachmentFile, bankTransferRequest, object : ApiCallback<String> {
                override fun onException(error: Throwable) {
                    Utils.printLog(RegisterViewModel.COUNTRY_API_TAG, error.message!!)
                }

                override fun onError(errorMsg: String) {
                    Utils.printLog(RegisterViewModel.COUNTRY_API_TAG, errorMsg)
                }

                override fun onSuccess(msg: String?) {
                    Log.d("msg", msg)
                }
            })
        }
    }

    fun onRecipientSelection(position: Int) {
        recipient = recipientsList[position].value
    }

    fun onTransferMethodsSelection(position: Int) {
        transferMethods = transferMethodsList[position].value
    }


    private fun isValidData(context: Context): Boolean {
        var isValid = true

        if (TextUtils.isEmpty(firstName.get())) {
            firstNameError.set(context.getString(R.string.first_name_error))
            isValid = false
        }

        if (TextUtils.isEmpty(lastName.get())) {
            lastNameError.set(context.getString(R.string.last_name_error))
            isValid = false
        }

        if (TextUtils.isEmpty(emailAddress.get())) {
            emailAddressError.set(context.getString(R.string.empty_email))
            isValid = false
        } else if (!Utils.isValidEmail(emailAddress.get())) {
            emailAddressError.set(context.getString(R.string.provide_valid_email))
            isValid = false
        }

        if (TextUtils.isEmpty(orderNumber.get())) {
            orderNumberError.set(context.getString(R.string.order_number_error))
            isValid = false
        }

        if (TextUtils.isEmpty(bankNumber.get())) {
            bankNumberError.set(context.getString(R.string.bank_number_error))
            isValid = false
        }

        if (TextUtils.isEmpty(holderAccountNumber.get())) {
            holderAccountError.set(context.getString(R.string.holder_account_number_error))
            isValid = false
        }

        if (TextUtils.isEmpty(transferAmount.get())) {
            transferAmountError.set(context.getString(R.string.transfer_amount_error))
            isValid = false
        }


        if (TextUtils.isEmpty(transferDate.get()?.toString())) {
            transferDateError.set(context.getString(R.string.transfer_date_error))
            isValid = false
        }


        if(recipient == Constants.BANK_RECIPIENT_LABEL){
            Toast.makeText(context, context.getString(R.string.empty_bank_recipient), Toast.LENGTH_SHORT ).show()
            isValid = false
        }else if(transferMethods.equals(Constants.TRANSFER_METHOD_LABEL)){
            Toast.makeText(context, context.getString(R.string.empty_transfer_method), Toast.LENGTH_SHORT ).show()
            isValid = false
        }

        if(attachmentFile == null){
            Toast.makeText(context, context.getString(R.string.empty_attachment_file), Toast.LENGTH_SHORT ).show()
            isValid = false
        }
        return isValid
    }
}