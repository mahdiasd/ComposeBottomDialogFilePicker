package mahdiasd.bottomdialogfilepicker

import androidx.compose.runtime.Stable
import java.io.File
import java.io.Serializable

@Stable
data class PickerFile(
    val path: String,
    val file: File = File(path),
    val selected: Boolean = false
) : Serializable
