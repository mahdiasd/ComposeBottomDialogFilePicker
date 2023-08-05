package mahdiasd.bottomdialogfilepicker

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.Serializable

val pickerDarkColor = Color(0xFF121212)
val pickerColorPrimary = Color(0xFF55D6D6)


@Stable
data class PickerConfig(
    val currentType: PickerType,
    val modes: List<PickerMode> = PickerUtils.allModes,

    val containerColor: Color = pickerDarkColor,
    val scrimColor: Color? = null,
    val maxSelection: Int = 100,
    val showPreview: Boolean = false,

    val enableCamera: Boolean = true,
    val cameraIcon: Int = R.drawable.mahdiasd_ic_camera,
    val cameraIconTint: Color = Color.White,
    val cameraIconBackground: Color = Color.Transparent,

    val checkBoxSelectedColor: Color = pickerColorPrimary,
    val checkBoxUnSelectedColor: Color = Color.DarkGray,
    val checkBoxSize: Dp = 36.dp,

    val doneIcon: Int = R.drawable.mahdiasd_ic_tick,
    val doneIconSize: Dp = 52.dp,
    val doneIconTint: Color = Color.White,
    val doneIconBackground: Color = pickerColorPrimary,

    val doneBadgeBackgroundColor: Color = pickerColorPrimary,
    val doneBadgeStyle: TextStyle = TextStyle(Color.White, fontWeight = FontWeight.Normal, fontSize = 14.sp, textAlign = TextAlign.Center),


    val videoPlayIcon: Int = R.drawable.mahdiasd_ic_play,
    val videoPlayIconSize: Dp = 28.dp,
    val videoPlayIconTint: Color = Color.DarkGray,
    val videoPlayIconBackground: Color = Color.White,

    val storageIcon: Int = R.drawable.mahdiasd_ic_storage,
    val storageIconSize: Dp = 48.dp,
    val storageIconTint: Color = Color.White,
    val storageIconBackground: Color = amber,
    val storageTitle: String = "Storage",
    val storageDescription: String = "Brows your file system",
    val storageTitleStyle: TextStyle = TextStyle(Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    val storageDescriptionStyle: TextStyle = TextStyle(Color.Gray, fontWeight = FontWeight.Normal, fontSize = 14.sp),

    val galleryIcon: Int = R.drawable.mahdiasd_ic_gallery,
    val galleryIconSize: Dp = 48.dp,
    val galleryIconTint: Color = Color.White,
    val galleryIconBackground: Color = lightPurple,
    val galleryTitle: String = "Gallery",
    val galleryDescription: String = "To send images directly from gallery",
    val galleryTitleStyle: TextStyle = TextStyle(Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    val galleryDescriptionStyle: TextStyle = TextStyle(Color.Gray, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    val supportRtl: Boolean = false,

    val searchTextHint: String = "Search",
    val searchTextHintStyle: TextStyle = TextStyle(color = Color.Gray, fontSize = 14.sp),
    val searchTextStyle: TextStyle = TextStyle(color = Color.White, fontSize = 14.sp),

    val noItemMessage: String = "No item to show",
    val noItemStyle: TextStyle = TextStyle(Color.Cyan, fontWeight = FontWeight.Normal, fontSize = 14.sp),

    ) {
}

@Stable
data class PickerMode(
    val pickerType: PickerType,

    val title: String = getDefaultTitle(pickerType),

    val selectedColor: Color = pickerColorPrimary,

    val titleStyle: TextStyle = TextStyle(Color.White, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    val itemTextStyle: TextStyle = TextStyle(Color.White, fontWeight = FontWeight.Normal, fontSize = 14.sp),

    val itemIcon: Int = R.drawable.mahdiasd_ic_music,
    val itemIconSize: Dp = 32.dp,
    val itemIconTint: Color = pickerColorPrimary,
    val itemIconBackground: Color = Color.White,


    val icon: Int = getDefaultIcon(pickerType),
    val iconTint: Color = Color.White,
    val iconSize: Dp = 58.dp,

    val shape: Shape = CircleShape,
    val shapeColor: Color = getDefaultColor(pickerType)
) : Serializable

private fun getDefaultColor(pickerType: PickerType): Color {
    return when (pickerType) {
        PickerType.Image -> darkBlue
        PickerType.Video -> lightGreen
        PickerType.File -> deepOrange
        PickerType.Audio -> amber
    }
}

private fun getDefaultTitle(pickerType: PickerType): String {
    return when (pickerType) {
        PickerType.Image -> "Image"
        PickerType.Video -> "Video"
        PickerType.File -> "File"
        PickerType.Audio -> "Music"
    }
}

private fun getDefaultIcon(pickerType: PickerType): Int {
    return when (pickerType) {
        PickerType.Image -> R.drawable.mahdiasd_ic_image
        PickerType.Video -> R.drawable.mahdiasd_ic_video
        PickerType.File -> R.drawable.mahdiasd_ic_file
        PickerType.Audio -> R.drawable.mahdiasd_ic_play
    }
}

val darkBlue = Color(0xFF3F51B5)
val lightBlue = Color(0xFF03A9F4)
val lightGreen = Color(0xFF4CAF50)
val deepOrange = Color(0xFFFF7043)
val amber = Color(0xFFFFC107)
val lightPurple = Color(0xFFBA68C8)

enum class PickerType { Image, Video, File, Audio, }