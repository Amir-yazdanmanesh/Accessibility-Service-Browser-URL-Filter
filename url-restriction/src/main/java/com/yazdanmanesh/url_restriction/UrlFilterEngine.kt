package com.yazdanmanesh.url_restriction

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

data class UrlFilterConfig(
    val restrictedAddress: String = "",
    val redirectTo: String = ""
) {
    companion object {
        fun filterInputAddress(address: String): String {
            return if (address.startsWith("www.")) {
                address.removePrefix("www.")
            } else {
                address
            }
        }
    }
}

class UrlFilterEngine {

    @Volatile
    var config: UrlFilterConfig = UrlFilterConfig()

    private val previousUrlDetections = HashMap<String, Long>()

    fun filterBrowserURL(
        event: AccessibilityEvent,
        service: MyAccessibilityService,
        supportedBrowsers: List<SupportedBrowserConfig>
    ) {
        try {
            val parentNodeInfo = event.source ?: return
            val packageName = event.packageName?.toString() ?: return

            val browserConfig = supportedBrowsers.firstOrNull { it.packageName == packageName }
                ?: return

            val capturedUrl = captureUrl(parentNodeInfo, browserConfig) ?: return

            val eventTime = event.eventTime
            val detectionId = "$packageName, and url $capturedUrl"
            val lastRecordedTime = previousUrlDetections[detectionId] ?: 0L

            if (eventTime - lastRecordedTime > 2000) {
                previousUrlDetections[detectionId] = eventTime
                analyzeCapturedUrl(service, capturedUrl, browserConfig.packageName)
            }
        } catch (_: Exception) {
            // ignored — accessibility events can be unpredictable
        }
    }

    private fun captureUrl(info: AccessibilityNodeInfo, config: SupportedBrowserConfig): String? {
        val nodes = info.findAccessibilityNodeInfosByViewId(config.addressBarId)
        if (nodes.isNullOrEmpty()) return null
        return nodes[0].text?.toString()
    }

    private fun analyzeCapturedUrl(
        service: MyAccessibilityService,
        capturedUrl: String,
        browserPackage: String
    ) {
        val restricted = config.restrictedAddress
        if (restricted.isNotEmpty() && capturedUrl.lowercase().startsWith(restricted)) {
            performRedirect(service, config.redirectTo, browserPackage)
        }
    }

    private fun performRedirect(
        service: MyAccessibilityService,
        redirectUrl: String,
        browserPackage: String
    ) {
        if (redirectUrl.isEmpty()) return

        val url = if (redirectUrl.startsWith("http://") || redirectUrl.startsWith("https://")) {
            redirectUrl
        } else {
            "https://$redirectUrl"
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                setPackage(browserPackage)
                putExtra(Browser.EXTRA_APPLICATION_ID, browserPackage)
                addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
            service.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            service.startActivity(intent)
        }
    }
}
