package org.gampiot.robok.feature.component.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.gampiot.robok.feature.component.radio.IntRadioController

@Composable
fun RobokChoiceDialog(
    visible: Boolean,
    title: @Composable () -> Unit,
    default: Int,
    options: List<Int>, // Added options parameter
    labelFactory: (Int) -> String,
    excludedOptions: List<Int>,
    onRequestClose: () -> Unit,
    onChoice: (Int) -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = { onRequestClose() },
            title = title,
            text = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    IntRadioController(
                        default = default,
                        options = options, // Passing options here
                        excludedOptions = excludedOptions,
                        labelFactory = labelFactory,
                        onChoiceSelected = { selectedOption ->
                            onChoice(selectedOption)
                            onRequestClose()
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onRequestClose() }) {
                    Text("Close")
                }
            }
        )
    }
}
