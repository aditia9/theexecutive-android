package com.ranosys.theexecutive.modules.splash

/**
 * Created by nikhil on 6/3/18.
 */

data class ConfigurationResponse(
		val version: String,
		val maintenance: String,
		val message_maintenance: String,
		val appstore_url: String,
		val product_media_url: String,
		val category_media_url: String
)