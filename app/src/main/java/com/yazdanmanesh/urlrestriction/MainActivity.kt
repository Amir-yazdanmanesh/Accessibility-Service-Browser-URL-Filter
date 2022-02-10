package com.yazdanmanesh.urlrestriction

import android.accessibilityservice.AccessibilityService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import android.widget.EditText
import android.view.accessibility.AccessibilityManager
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.text.TextUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yazdanmanesh.url_resteriction.AccessibilityUtils
import com.yazdanmanesh.url_resteriction.MyAccessibilityService
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (isMiUi()) {
            showMiUiAlert()
        }
        val myService = AccessibilityUtils.Builder()
        myService.setRedirectTo("http://www.404.net")

        findViewById<View>(R.id.btn_accessibility).setOnClickListener {
            if (!isAccessibilityServiceEnabled(
                    this@MainActivity,
                    MyAccessibilityService::class.java
                )
            ) startActivity(
                Intent(
                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                )
            ) else Toast.makeText(this@MainActivity, "Service is active!", Toast.LENGTH_SHORT)
                .show()
        }
        val edtRestrictedAddress = findViewById<View>(R.id.edt_input_url) as EditText
        findViewById<View>(R.id.btn_apply).setOnClickListener {
            if (isAccessibilityServiceEnabled(
                    this@MainActivity,
                    MyAccessibilityService::class.java
                )
            ) {
                myService.setMyRestrictedAddress(edtRestrictedAddress.text.toString())
                myService.build()

                Toast.makeText(this@MainActivity, "Successfully!", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.tv_help2).setText("Your browsers restrict '${edtRestrictedAddress.text}' address ")
            } else Toast.makeText(this@MainActivity, "Service not is active!", Toast.LENGTH_SHORT)
                .show()
        }
    }


    fun isAccessibilityServiceEnabled(
        context: Context,
        service: Class<out AccessibilityService?>
    ): Boolean {
        val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName == context.packageName && enabledServiceInfo.name == service.name) return true
        }
        return false
    }


    fun isMiUi(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
    }

    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }

    fun showMiUiAlert() {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val sharedCheckPermissionKeyValue = sharedPreferences.getInt("check_key", 0)
        if (sharedCheckPermissionKeyValue == 0)
            MaterialAlertDialogBuilder(this)
                .setMessage("You must have give background permission to activate the application")
                .setPositiveButton("Yes") { dialog, which ->
                    openBackgroundPermissionInXiaomi()
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putInt("check_key", 1)
                    editor.apply()
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
    }

    private fun openBackgroundPermissionInXiaomi() {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName(
            "com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity"
        )
        intent.putExtra("extra_pkgname", "com.yazdanmanesh.urlrestriction")
        startActivity(intent)
    }

}