package com.example.reminderapp.ui.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    var showNotificationRationale by mutableStateOf(false)
        private set
    var showAlarmRationale by mutableStateOf(false)
        private set

    private var onGranted: () -> Unit = {}
    private var launchNotificationPermission: () -> Unit = {}
    private var launchExactAlarmSettings: () -> Unit = {}
    private lateinit var appContext: Context

    internal fun update(
        context: Context,
        onGranted: () -> Unit,
        launchNotificationPermission: () -> Unit,
        launchExactAlarmSettings: () -> Unit
    ) {
        appContext = context
        this.onGranted = onGranted
        this.launchNotificationPermission = launchNotificationPermission
        this.launchExactAlarmSettings = launchExactAlarmSettings
    }

    fun requestOrProceed() {
        if (!NotificationPermissionHelper.hasPermission(appContext)) {
            launchNotificationPermission()
        } else {
            checkExactAlarm()
        }
    }

    internal fun onNotificationPermissionResult(granted: Boolean) {
        if (granted) checkExactAlarm() else showNotificationRationale = true
    }

    private fun checkExactAlarm() {
        if (!NotificationPermissionHelper.canScheduleExactAlarms(appContext)) {
            showAlarmRationale = true
        } else {
            onGranted()
        }
    }

    internal fun onReturnFromAlarmSettings() {
        if (NotificationPermissionHelper.canScheduleExactAlarms(appContext)) {
            onGranted()
        } else {
            showAlarmRationale = true
        }
    }

    fun dismissNotificationRationale() { showNotificationRationale = false }
    fun dismissAlarmRationale() { showAlarmRationale = false }

    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", appContext.packageName, null)
        }
        appContext.startActivity(intent)
    }

    fun openAlarmSettings() { launchExactAlarmSettings() }
}

@Composable
fun rememberNotificationPermissionState(
    onGranted: () -> Unit
): NotificationPermissionState {
    val context = LocalContext.current
    val state = remember { NotificationPermissionState() }

    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        state.onNotificationPermissionResult(isGranted)
    }

    val alarmSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        state.onReturnFromAlarmSettings()
    }

    SideEffect {
        state.update(
            context = context,
            onGranted = onGranted,
            launchNotificationPermission = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            },
            launchExactAlarmSettings = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    alarmSettingsLauncher.launch(intent)
                }
            }
        )
    }

    return state
}

@Composable
fun NotificationPermissionRationaleDialog(state: NotificationPermissionState) {
    if (state.showNotificationRationale) {
        AlertDialog(
            onDismissRequest = state::dismissNotificationRationale,
            title = { Text(stringResource(R.string.permission_notification_title)) },
            text = { Text(stringResource(R.string.permission_notification_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    state.dismissNotificationRationale()
                    state.openNotificationSettings()
                }) { Text(stringResource(R.string.action_open_settings)) }
            },
            dismissButton = {
                TextButton(onClick = state::dismissNotificationRationale) {
                    Text(stringResource(R.string.undo))
                }
            }
        )
    }
    if (state.showAlarmRationale) {
        AlertDialog(
            onDismissRequest = state::dismissAlarmRationale,
            title = { Text(stringResource(R.string.permission_alarm_title)) },
            text = { Text(stringResource(R.string.permission_alarm_rationale)) },
            confirmButton = {
                TextButton(onClick = {
                    state.dismissAlarmRationale()
                    state.openAlarmSettings()
                }) { Text(stringResource(R.string.action_open_settings)) }
            },
            dismissButton = {
                TextButton(onClick = state::dismissAlarmRationale) {
                    Text(stringResource(R.string.undo))
                }
            }
        )
    }
}
