package com.yazdanmanesh.url_restriction

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MyAccessibilityService? = null
            private set

        val supportedBrowsers = listOf(
            SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"),
            SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"),
            SupportedBrowserConfig("com.opera.browser", "com.opera.browser:id/url_field"),
            SupportedBrowserConfig("com.opera.mini.native", "com.opera.mini.native:id/url_field"),
            SupportedBrowserConfig("com.duckduckgo.mobile.android", "com.duckduckgo.mobile.android:id/omnibarTextInput"),
            SupportedBrowserConfig("com.microsoft.emmx", "com.microsoft.emmx:id/url_bar"),
            SupportedBrowserConfig("com.coloros.browser", "com.coloros.browser:id/azt"),
            SupportedBrowserConfig("com.sec.android.app.sbrowser", "com.sec.android.app.sbrowser:id/location_bar_edit_text"),
        )

        private val browserPackageNames: Array<String> by lazy {
            supportedBrowsers.map { it.packageName }.toTypedArray()
        }
    }

    val urlFilterEngine = UrlFilterEngine()

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            serviceInfo = AccessibilityServiceInfo().apply {
                eventTypes = AccessibilityEvent.TYPES_ALL_MASK
                packageNames = browserPackageNames
                feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL
                notificationTimeout = 300
                flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                        AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        urlFilterEngine.filterBrowserURL(event, this, supportedBrowsers)
    }

    override fun onInterrupt() {
        // no-op
    }
}
