package mahdiasd.bottomdialogfilepicker

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import mahdiasd.bottomdialogfilepicker.PickerUtils.printToLog
import mahdiasd.bottomdialogfilepicker.ui.theme.BottomDialogFilePickerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = PickerConfig(
            currentType = PickerType.Image,
            storageTitle = "حافظه دستگاه",
            storageDescription = "برای انتخاب فایل از فایل منیجر دستگاه",
            galleryTitle = "گالری",
            galleryDescription = "برای انتخاب فایل از گالری دستگاه",
            supportRtl = true,
            maxSelection = 12,
            searchTextHint = "جستجو",
            searchTextHintStyle = TextStyle(textAlign = TextAlign.Right)
        )

        setContent {
            BottomDialogFilePickerTheme {
//                LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                val isShowing = remember {
                    mutableStateOf(true)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isShowing.value) {
                        FilePickerDialog(
                            config = config,
                            onDismissDialog = {
                                isShowing.value = false
                            },
                            selectedFiles = {
                                it.printToLog("selectedFiles")
                            }
                        )
                    } else {
                        Button(onClick = { isShowing.value = true }) {
                            Text(text = "Open Dialog")
                        }
                    }
                }
            }
        }
    }

}
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
