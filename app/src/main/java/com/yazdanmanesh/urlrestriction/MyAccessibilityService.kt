package com.yazdanmanesh.urlrestriction


import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import java.util.ArrayList

class MyAccessibilityService : AccessibilityService() {

    companion object {
        var instance: MyAccessibilityService? = null
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.serviceInfo = AccessibilityServiceInfo().apply {
                feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
                eventTypes =
                    AccessibilityEvent.TYPE_VIEW_CLICKED or

                            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                flags =
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                            AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS or
                            AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
                packageNames = packageNames()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event == null)
            return

            AccessibilityUtils.filterBrowserURL(event, this,getSupportedBrowsers())

    }

    override fun onInterrupt() {
        // ignore
    }
    private fun getSupportedBrowsers(): List<SupportedBrowserConfig> {
        val browsers: MutableList<SupportedBrowserConfig> = ArrayList()
        browsers.add(
            SupportedBrowserConfig(
                "com.android.chrome",
                "com.android.chrome:id/url_bar"
            )
        )
        browsers.add(
            SupportedBrowserConfig(
                "org.mozilla.firefox",
                "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"
            )
        )
        browsers.add(
            SupportedBrowserConfig(
                "com.opera.browser",
                "com.opera.browser:id/url_field"
            )
        )
        browsers.add(
            SupportedBrowserConfig(
                "com.opera.mini.native",
                "com.opera.mini.native:id/url_field"
            )
        )
        browsers.add(
            SupportedBrowserConfig(
                "com.duckduckgo.mobile.android",
                "com.duckduckgo.mobile.android:id/omnibarTextInput"
            )
        )

        browsers.add(
            SupportedBrowserConfig(
                "com.microsoft.emmx",
                "com.microsoft.emmx:id/url_bar"
            )
        )
        browsers.add(
            SupportedBrowserConfig(
                "com.coloros.browser",
                "com.coloros.browser:id/azt"
            )
        )
        browsers.add(
            SupportedBrowserConfig(
                "com.sec.android.app.sbrowser",
                "com.sec.android.app.sbrowser:id/location_bar_edit_text"
            )
        )
        return browsers
    }

    private fun packageNames(): Array<String> {
        val packageNames: MutableList<String> = ArrayList()
        for (config in getSupportedBrowsers()) {
            packageNames.add(config.packageName)
        }
        return packageNames.toTypedArray()
    }

}