package mahdiasd.bottomdialogfilepicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
            searchTextHint = "جستجو",
            searchTextHintStyle = TextStyle(textAlign = TextAlign.Right)
        )

        setContent {
            BottomDialogFilePickerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FilePickerDialog(config = config, selectedFiles = {
                        it.printToLog("selectedFiles")
                    })
                }
            }
        }
    }

}
