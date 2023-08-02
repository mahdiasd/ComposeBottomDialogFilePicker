package mahdiasd.bottomdialogfilepicker

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter


@Composable
fun MediaItem(
    pickerFile: PickerFile = PickerFile(""),
    config: PickerConfig = PickerConfig(PickerType.Video),
    pickerMode: PickerMode = PickerMode(PickerType.Video),
    context: Context = LocalContext.current,
    imageLoader: ImageLoader,
    onChecked: () -> Unit = {},
) {

    val painter = rememberAsyncImagePainter(model = pickerFile.path, imageLoader = imageLoader)

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onChecked() }
            .background(Color.Gray, RoundedCornerShape(16.dp))
            .aspectRatio(1f)
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clickable { onChecked() }
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
        )

        CircleCheckbox(
            modifier = Modifier.align(Alignment.TopEnd),
            selected = pickerFile.selected,
            selectedColor = config.checkBoxSelectedColor,
            unSelectedColor = config.checkBoxUnSelectedColor,
            iconSize = config.checkBoxSize,
            onChecked = onChecked
        )

        if (pickerMode.pickerType == PickerType.Video)
            Icon(
                painter = painterResource(id = config.videoPlayIcon),
                contentDescription = null,
                Modifier
                    .size(config.videoPlayIconSize)
                    .shadow(2.dp, CircleShape, spotColor = pickerColorPrimary)
                    .align(Alignment.Center)
                    .background(config.videoPlayIconBackground, CircleShape)
                    .padding(8.dp),
                tint = config.videoPlayIconTint,
            )
    }
}

@Preview(showBackground = true)
@Composable
fun MediaAudioItem(
    pickerFile: PickerFile = PickerFile(""),
    config: PickerConfig = PickerConfig(PickerType.Audio),
    pickerMode: PickerMode = PickerMode(PickerType.Audio),
    onChecked: () -> Unit = {}

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChecked() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = pickerMode.itemIcon),
            contentDescription = null,
            modifier = Modifier
                .clickable { onChecked() }
                .size(pickerMode.itemIconSize)
                .background(pickerMode.itemIconBackground, CircleShape)
                .padding(4.dp),
            tint = pickerMode.itemIconTint
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            modifier = Modifier
                .clickable { onChecked() }
                .weight(1f, true),
            text = pickerFile.file.name,
            maxLines = 1,
            fontSize = 14.sp,
            color = Color.White,
            textAlign = TextAlign.Left
        )

        CircleCheckbox(
            selected = pickerFile.selected,
            selectedColor = config.checkBoxSelectedColor,
            unSelectedColor = config.checkBoxUnSelectedColor,
            iconSize = config.checkBoxSize,
            onChecked = onChecked
        )
    }

}


@Preview(showBackground = true)
@Composable
fun FileItem(
    icon: Int = R.drawable.mahdiasd_ic_storage,
    iconBackground: Color = Color.White,
    iconTint: Color = lightPurple,
    iconSize: Dp = 52.dp,

    title: String = "",
    titleTextStyle: TextStyle = TextStyle(),

    description: String = "",
    descriptionTextStyle: TextStyle = TextStyle(),

    supportRtl: Boolean = false,

    onClicked: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClicked() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (supportRtl) {
            Column(modifier = Modifier.weight(1f, true), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClicked() },
                    text = title,
                    style = titleTextStyle.plus(TextStyle(textDirection = TextDirection.Rtl, textAlign = TextAlign.Right))
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    text = description,
                    style = descriptionTextStyle.plus(TextStyle(textDirection = TextDirection.Rtl, textAlign = TextAlign.Right))
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .background(iconBackground, CircleShape)
                    .padding(8.dp),
                tint = iconTint
            )
        } else {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .background(iconBackground, CircleShape)
                    .padding(8.dp),
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f, true)) {
                Text(
                    text = title,
                    style = titleTextStyle
                )
                Text(
                    text = description,
                    style = descriptionTextStyle
                )
            }
        }


    }

}

@Composable
fun CircleCheckbox(
    modifier: Modifier = Modifier,
    selected: Boolean,
    selectedColor: Color,
    unSelectedColor: Color,
    iconSize: Dp = 24.dp,
    onChecked: () -> Unit = {}
) {
    Icon(
        imageVector = Icons.Filled.Done,
        tint = Color.White,
        modifier = modifier
            .size(iconSize)
            .padding(4.dp)
            .size(24.dp)
            .shadow(2.dp, CircleShape)
            .background(if (selected) selectedColor else unSelectedColor, shape = CircleShape)
            .padding(4.dp)
            .clickable {
                onChecked()
            },
        contentDescription = "checkbox"
    )

}
