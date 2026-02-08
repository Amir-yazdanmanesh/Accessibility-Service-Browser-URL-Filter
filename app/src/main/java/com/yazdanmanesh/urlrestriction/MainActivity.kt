package com.yazdanmanesh.urlrestriction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yazdanmanesh.urlrestriction.ui.theme.UrlRestrictionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrlRestrictionTheme {
                UrlRestrictionApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlRestrictionApp(viewModel: MainViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshServiceStatus(context)
        }
    }

    var showMiUiDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (viewModel.isMiUi) {
            val prefs = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
            if (prefs.getInt("check_key", 0) == 0) {
                showMiUiDialog = true
            }
        }
    }

    if (showMiUiDialog) {
        MiUiPermissionDialog(
            onConfirm = {
                showMiUiDialog = false
                val prefs = context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                prefs.edit().putInt("check_key", 1).apply()
                openBackgroundPermissionInXiaomi(context)
            },
            onDismiss = { showMiUiDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Button(
                onClick = {
                    if (!viewModel.isServiceEnabled) {
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    } else {
                        Toast.makeText(context, "Service is active!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.active_text))
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.enter_the_address_you_want_to_restrict),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(15.dp))

            OutlinedTextField(
                value = viewModel.restrictedUrl,
                onValueChange = { viewModel.onUrlChanged(it) },
                placeholder = { Text(stringResource(R.string.www)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.for_example_www_facebook_com),
                style = MaterialTheme.typography.bodySmall
            )

            if (viewModel.statusText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = viewModel.statusText,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    viewModel.refreshServiceStatus(context)
                    if (viewModel.isServiceEnabled) {
                        viewModel.applyRestriction()
                        Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Service is not active!", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.apply))
            }
        }
    }
}

@Composable
fun MiUiPermissionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text("You must give background permission to activate the application")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun openBackgroundPermissionInXiaomi(context: Context) {
    try {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR").apply {
            setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            putExtra("extra_pkgname", context.packageName)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // MIUI permission editor not available
    }
}
