package com.example.reminderapp.ui.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.reminderapp.R
import com.example.reminderapp.notification.NotificationPermissionHelper

class NotificationPermissionState {
    var showRationale by mutableStateOf(false)
        private set

    private var onGranted: () -> Unit = {}
    private var launchPermission: () -> Unit = {}
    private lateinit var appContext: Context

    internal fun update(context: Context, onGranted: () -> Unit, launchPermission: () -> Unit) {
        appContext = context
        this.onGranted = onGranted
        this.launchPermission = launchPermission
    }

    fun requestOrProceed() {
        if (NotificationPermissionHelper.hasPermission(appContext)) {
            onGranted()
        } else {
            launchPermission()
        }
    }

    fun dismissRationale() { showRationale = false }

    fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", appContext.packageName, null)
        }
        appContext.startActivity(intent)
    }

    internal fun onPermissionResult(granted: Boolean) {
        if (granted) onGranted() else showRationale = true
    }
}

@Composable
fun rememberNotificationPermissionState(
    onGranted: () -> Unit
): NotificationPermissionState {
    val context = LocalContext.current
    val state = remember { NotificationPermissionState() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        state.onPermissionResult(isGranted)
    }

    SideEffect {
        state.update(
            context = context,
            onGranted = onGranted,
            launchPermission = { launcher.launch(Manifest.permission.POST_NOTIFICATIONS) }
        )
    }

    return state
}

@Composable
fun NotificationPermissionRationaleDialog(state: NotificationPermissionState) {
    if (state.showRationale) {
        AlertDialog(
            onDismissRequest = state::dismissRationale,
            title = { Text(stringResource(R.string.permission_notification_title)) },
            text = { Text(stringResource(R.string.permission_notification_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    state.dismissRationale()
                    state.openSettings()
                }) { Text(stringResource(R.string.action_open_settings)) }
            },
            dismissButton = {
                TextButton(onClick = state::dismissRationale) {
                    Text(stringResource(R.string.undo))
                }
            }
        )
    }
}
