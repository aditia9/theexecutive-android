package com.ranosys.theexecutive.modules.bankTransfer

/**
 * @Details fragment shows BankTransfer Data Class
 * @Author Ranosys Technologies
 * @Date 1, June,2018
 */
data class TransferMethodsDataClass(
        val label: String,
        val value: String
)


data class Recipients(
        val label: String,
        val value: String
)


data class BankTransferRequest(
        val name: String,
        val email_submitter: String,
        val orderid: String,
        val bank_name: String,
        val holder_account: String,
        val amount: String,
        val recipient: String,
        val method: String,
        val date: String
)