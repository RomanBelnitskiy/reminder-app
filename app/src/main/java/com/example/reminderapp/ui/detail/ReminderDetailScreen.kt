package com.example.reminderapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.reminderapp.R
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.Reminder
import com.example.reminderapp.ui.permission.NotificationPermissionRationaleDialog
import com.example.reminderapp.ui.permission.rememberNotificationPermissionState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: ReminderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permState = rememberNotificationPermissionState { viewModel.toggleActive() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                DetailUiEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.reminder?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    uiState.reminder?.let { reminder ->
                        IconButton(onClick = { onNavigateToEdit(reminder.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.action_edit)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.reminder == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.reminder_not_found))
                }
            }
            else -> {
                ReminderDetailContent(
                    reminder = uiState.reminder!!,
                    onToggleActive = {
                        if (uiState.reminder?.isActive == false) {
                            permState.requestOrProceed()
                        } else {
                            viewModel.toggleActive()
                        }
                    },
                    onDeleteClick = viewModel::onDeleteClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    NotificationPermissionRationaleDialog(permState)

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDialogDismiss,
            title = { Text(stringResource(R.string.dialog_delete_title)) },
            text = { Text(stringResource(R.string.dialog_delete_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirm) {
                    Text(
                        text = stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteDialogDismiss) {
                    Text(stringResource(R.string.undo))
                }
            }
        )
    }
}

@Composable
private fun ReminderDetailContent(
    reminder: Reminder,
    onToggleActive: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (reminder.description.isNotEmpty()) {
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                }

                DetailRow(
                    icon = Icons.Default.DateRange,
                    text = reminder.reminderDateTime.toFormattedDateTime()
                )
                DetailRow(
                    icon = Icons.AutoMirrored.Filled.Label,
                    text = stringResource(reminder.type.labelRes)
                )
                DetailRow(
                    icon = Icons.Default.Replay,
                    text = reminder.recurrenceLabel()
                )
                DetailRow(
                    icon = Icons.Default.Notifications,
                    text = stringResource(
                        if (reminder.isActive) R.string.status_active else R.string.status_inactive
                    )
                )
            }
        }

        OutlinedButton(
            onClick = onToggleActive,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(
                    if (reminder.isActive) R.string.action_deactivate else R.string.action_activate
                )
            )
        }

        Button(
            onClick = onDeleteClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.action_delete))
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Reminder.recurrenceLabel(): String {
    if (recurrenceType == RecurrenceType.CUSTOM && recurrenceInterval != null) {
        return stringResource(R.string.detail_interval_format, recurrenceInterval)
    }
    return stringResource(recurrenceType.labelRes)
}

private fun Long.toFormattedDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}
