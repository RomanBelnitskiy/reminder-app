package com.example.reminderapp.ui.screen.edit

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.reminderapp.R
import com.example.reminderapp.domain.model.RecurrenceType
import com.example.reminderapp.domain.model.ReminderSound
import com.example.reminderapp.domain.model.ReminderType
import com.example.reminderapp.ui.permission.NotificationPermissionRationaleDialog
import com.example.reminderapp.ui.permission.rememberNotificationPermissionState
import kotlinx.coroutines.flow.Flow
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreenRoute(
    state: EditUiState,
    processUiEvent: (EditUiEvent) -> Unit,
    effects: Flow<EditUiEffect>,
    controller: NavHostController
) {
    EditEffectHandler(effects, controller)

    EditScreen(state, processUiEvent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    state: EditUiState,
    processUiEvent: (EditUiEvent) -> Unit
) {

    val permState = rememberNotificationPermissionState {
        processUiEvent(EditUiEvent.OnSave)
    }
    NotificationPermissionRationaleDialog(permState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isEditMode) R.string.screen_title_edit_reminder
                            else R.string.screen_title_create_reminder
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            processUiEvent(EditUiEvent.OnNavigateBack)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EditForm(
                uiState = state,
                onTitleChange = {
                    processUiEvent(EditUiEvent.OnTitleChange(it))
                },
                onDescriptionChange = {
                    processUiEvent(EditUiEvent.OnDescriptionChange(it))
                },
                onTypeChange = {
                    processUiEvent(EditUiEvent.OnTypeChange(it))
                },
                onSoundChange = {
                    processUiEvent(EditUiEvent.OnSoundChange(it))
                },
                onDateChange = {
                    processUiEvent(EditUiEvent.OnDateChange(it))
                },
                onTimeChange = { hour, minute ->
                    processUiEvent(EditUiEvent.OnTimeChange(hour, minute))
                },
                onRecurrenceTypeChange = {
                    processUiEvent(EditUiEvent.OnRecurrenceTypeChange(it))
                },
                onRecurrenceIntervalChange = {
                    processUiEvent(EditUiEvent.OnRecurrenceIntervalChange(it))
                },
                onSave = permState::requestOrProceed,
                onCancel = {
                    processUiEvent(EditUiEvent.OnCancelClicked)
                },
                modifier = Modifier.padding(paddingValues)
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditForm(
    uiState: EditUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTypeChange: (ReminderType) -> Unit,
    onSoundChange: (ReminderSound) -> Unit,
    onDateChange: (Long) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onRecurrenceIntervalChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var soundExpanded by remember { mutableStateOf(false) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        OutlinedTextField(
            value = uiState.title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.label_title)) },
            isError = uiState.titleError,
            supportingText = {
                if (uiState.titleError) Text(stringResource(R.string.error_title_empty))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Description
        OutlinedTextField(
            value = uiState.description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.label_description)) },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        // Type dropdown
        ExposedDropdownMenuBox(
            expanded = typeExpanded,
            onExpandedChange = { typeExpanded = it }
        ) {
            OutlinedTextField(
                value = stringResource(uiState.type.labelRes),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false }
            ) {
                ReminderType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(stringResource(type.labelRes)) },
                        onClick = {
                            onTypeChange(type)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        // Sound dropdown
        ExposedDropdownMenuBox(
            expanded = soundExpanded,
            onExpandedChange = { soundExpanded = it }
        ) {
            OutlinedTextField(
                value = stringResource(uiState.sound.labelRes),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_sound)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = soundExpanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = soundExpanded,
                onDismissRequest = { soundExpanded = false }
            ) {
                ReminderSound.entries.forEach { sound ->
                    DropdownMenuItem(
                        text = { Text(stringResource(sound.labelRes)) },
                        onClick = {
                            onSoundChange(sound)
                            soundExpanded = false
                        }
                    )
                }
            }
        }

        // Date picker
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                "${stringResource(R.string.label_date)}: ${uiState.date.format(dateFormatter)}"
            )
        }

        // Time picker
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "${stringResource(R.string.label_time)}: ${uiState.time.format(timeFormatter)}"
            )
        }

        if (uiState.dateError) {
            Text(
                text = stringResource(R.string.error_date_past),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // Recurrence type dropdown
        ExposedDropdownMenuBox(
            expanded = recurrenceExpanded,
            onExpandedChange = { recurrenceExpanded = it }
        ) {
            OutlinedTextField(
                value = stringResource(uiState.recurrenceType.labelRes),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.label_recurrence)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = recurrenceExpanded,
                onDismissRequest = { recurrenceExpanded = false }
            ) {
                RecurrenceType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(stringResource(type.labelRes)) },
                        onClick = {
                            onRecurrenceTypeChange(type)
                            recurrenceExpanded = false
                        }
                    )
                }
            }
        }

        // Interval (only for CUSTOM)
        if (uiState.recurrenceType == RecurrenceType.CUSTOM) {
            OutlinedTextField(
                value = uiState.recurrenceInterval?.toString() ?: "",
                onValueChange = onRecurrenceIntervalChange,
                label = { Text(stringResource(R.string.label_interval_days)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.undo))
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val initialMillis = uiState.date
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.undo))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.time.hour,
            initialMinute = uiState.time.minute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.undo))
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}
