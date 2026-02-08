package com.yazdanmanesh.urlrestriction

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.os.Build
import android.view.accessibility.AccessibilityManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yazdanmanesh.url_restriction.MyAccessibilityService
import com.yazdanmanesh.url_restriction.UrlFilterConfig

class MainViewModel : ViewModel() {

    var restrictedUrl by mutableStateOf("")
        private set

    var statusText by mutableStateOf("")
        private set

    var isServiceEnabled by mutableStateOf(false)
        private set

    val isMiUi: Boolean = Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)

    fun onUrlChanged(url: String) {
        restrictedUrl = url
    }

    fun refreshServiceStatus(context: Context) {
        isServiceEnabled = isAccessibilityServiceEnabled(context, MyAccessibilityService::class.java)
    }

    fun applyRestriction() {
        val service = MyAccessibilityService.instance ?: return
        val filtered = UrlFilterConfig.filterInputAddress(restrictedUrl)
        service.urlFilterEngine.config = UrlFilterConfig(
            restrictedAddress = filtered,
            redirectTo = "https://www.404.net"
        )
        statusText = "Your browsers restrict '$restrictedUrl' address"
    }

    private fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService>
    ): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any {
                val info = it.resolveInfo.serviceInfo
                info.packageName == context.packageName && info.name == service.name
            }
    }
}
