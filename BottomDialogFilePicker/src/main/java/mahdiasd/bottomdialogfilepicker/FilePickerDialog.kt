package mahdiasd.bottomdialogfilepicker

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clipScrollableContainer
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.request.CachePolicy
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mahdiasd.bottomdialogfilepicker.PickerUtils.getAudio
import mahdiasd.bottomdialogfilepicker.PickerUtils.getImage
import mahdiasd.bottomdialogfilepicker.PickerUtils.getVideo
import mahdiasd.bottomdialogfilepicker.PickerUtils.permissionState
import mahdiasd.bottomdialogfilepicker.PickerUtils.toDp
import mahdiasd.bottomdialogfilepicker.PickerUtils.toPx
import java.io.File

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilePickerDialog(
    config: PickerConfig,
    modes: List<PickerMode> = PickerUtils.allModes,
    onDismissDialog: () -> Unit,
    selectedFiles: (List<PickerFile>) -> Unit,
) {

    val permissionsState = permissionState(config.enableCamera)
    if (!permissionsState.allPermissionsGranted) {
        LaunchedEffect(key1 = true, block = {
            permissionsState.launchMultiplePermissionRequest()
        })
    } else {

        val c = LocalContext.current
        val images = remember { mutableStateListOf<PickerFile>() }.apply {
            addAll(getImage(c, config.enableCamera))
        }
        val audios = remember { mutableStateListOf<PickerFile>() }.apply {
            addAll(getAudio(c))
        }
        val videos = remember { mutableStateListOf<PickerFile>() }.apply {
            addAll(getVideo(c))
        }

        val currentType = remember { mutableStateOf(config.currentType) }
        var searchText by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        val files by remember {
            derivedStateOf {
                when (currentType.value) {
                    PickerType.Image -> images
                    PickerType.Video -> videos
                    PickerType.File -> listOf()
                    PickerType.Audio -> {
                        if (searchText.isEmpty())
                            audios
                        else
                            audios.filter { it.path.lowercase().contains(searchText.lowercase()) }
                    }
                }.distinctBy { it.path }
            }
        }

        val bottomSheetState = rememberModalBottomSheetState()

        val selectedFilesCount = remember { mutableStateOf(0) }


        BottomSheetDialog(
            pickerModes = modes.toImmutableList(),
            bottomSheetState = bottomSheetState,
            config = config,
            currentType = currentType.value,
            files = files.toImmutableList(),
            selectedCount = selectedFilesCount.value,
            itemTypeClick = { currentType.value = it },
            onDismissDialog = onDismissDialog,
            searchText = searchText,
            onSearchChange = {
                searchText = it
            },
            onChangeSelect = { pickerFile ->
                when (currentType.value) {
                    PickerType.Image -> {
                        val index = images.indexOfFirst { it.path == pickerFile.path }.takeIf { it >= 0 } ?: return@BottomSheetDialog
                        images[index] = images[index].copy(selected = !pickerFile.selected)
                    }

                    PickerType.Video -> {
                        val index = videos.indexOfFirst { it.path == pickerFile.path }.takeIf { it >= 0 } ?: return@BottomSheetDialog
                        videos[index] = videos[index].copy(selected = !pickerFile.selected)
                    }

                    PickerType.File -> {

                    }

                    PickerType.Audio -> {
                        val index = audios.indexOfFirst { it.path == pickerFile.path }.takeIf { it >= 0 } ?: return@BottomSheetDialog
                        audios[index] = audios[index].copy(selected = !pickerFile.selected)
                    }
                }

                val total = (images + audios + videos).filter { it.selected }

                if (total.size > config.maxSelection) {
                    total.firstOrNull()?.let {
                        images.indexOfFirst { it == total.first() }.takeIf { it > 0 }?.apply { images[this] = images[this].copy(selected = false) }
                        videos.indexOfFirst { it == total.first() }.takeIf { it > 0 }?.apply { videos[this] = videos[this].copy(selected = false) }
                        audios.indexOfFirst { it == total.first() }.takeIf { it > 0 }?.apply { audios[this] = audios[this].copy(selected = false) }
                    }
                }

                if (total.size != selectedFilesCount.value)
                    selectedFilesCount.value = total.size
            },
            onDoneClick = {
                val s = (images.flatMap { listOf(it) } + audios.flatMap { listOf(it) } + videos.flatMap { listOf(it) }).filter { it.selected }.distinctBy { it.path }
                selectedFiles.invoke(s)
                onDismissDialog()
                coroutineScope.launch { bottomSheetState.hide() }
            },
            onCameraPhoto = { pickerFile ->
                images.add(1, pickerFile)
                val count = (images.flatMap { listOf(it) } + audios.flatMap { listOf(it) } + videos.flatMap { listOf(it) }).filter { it.selected }.size
                if (count != selectedFilesCount.value)
                    selectedFilesCount.value = count
            },
            onStoragePicker = { list ->
                selectedFiles.invoke(list.distinctBy { it.path })
                onDismissDialog()
                coroutineScope.launch { bottomSheetState.hide() }
            }
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetDialog(
    pickerModes: ImmutableList<PickerMode>,
    bottomSheetState: SheetState,
    config: PickerConfig,
    currentType: PickerType,
    files: ImmutableList<PickerFile>,
    selectedCount: Int,
    itemTypeClick: (PickerType) -> Unit,
    onChangeSelect: (PickerFile) -> Unit,
    searchText: String = "",
    onSearchChange: (String) -> Unit,
    onDismissDialog: () -> Unit,
    onDoneClick: () -> Unit,
    onCameraPhoto: (PickerFile) -> Unit,
    onStoragePicker: (List<PickerFile>) -> Unit,
    isLandscape: Boolean = LocalConfiguration.current.orientation != Configuration.ORIENTATION_PORTRAIT

    ) {
    val density = LocalDensity.current.density
    var modalHeight by remember { mutableStateOf(0) }
    var modalWidth by remember { mutableStateOf(0) }
    var footerHeight by remember { mutableStateOf(0) }

    val bottomPadding = ButtonDefaults.MinHeight.toPx()

    val context = LocalContext.current
    val mode = pickerModes.find { it.pickerType == currentType } ?: PickerMode(currentType)

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(VideoFrameDecoder.Factory()) }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()
    }

    var horizontalArrangement by remember { mutableStateOf(Arrangement.SpaceEvenly) }
    LaunchedEffect(key1 = selectedCount, block = {
        horizontalArrangement = if (selectedCount > 0 && !isLandscape) Arrangement.spacedBy(16.dp) else Arrangement.SpaceEvenly
    })

    val doneAlpha = animateFloatAsState(targetValue = if (selectedCount > 0) 1f else 0f, label = "doneAlphaAnimation")



    ModalBottomSheet(
        modifier = Modifier
            .defaultMinSize(minHeight = modalHeight.toDp())
            .onGloballyPositioned {
                modalHeight = it.size.height
                modalWidth = it.size.width
            },
        sheetState = bottomSheetState,
        containerColor = config.containerColor,
        scrimColor = config.scrimColor ?: Color.Gray,
        onDismissRequest = onDismissDialog
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {

            when (currentType) {
                PickerType.Image, PickerType.Video -> {
                    if (files.isEmpty())
                        Text(modifier = Modifier.fillMaxWidth(), text = config.noItemMessage, style = config.noItemStyle)
                    else
                        ImageAndVideoScreen(config, imageLoader, modalHeight, files, mode, onChangeSelect, onCameraPhoto)
                }

                PickerType.File -> {
                    FileScreen(config, onStoragePicker = onStoragePicker)
                }

                PickerType.Audio -> {
                    if (files.isEmpty())
                        Text(modifier = Modifier.fillMaxWidth(), text = config.noItemMessage, style = config.noItemStyle)
                    else
                        AudioScreen(config, modalHeight, files, onChangeSelect, searchText, onSearchChange)
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(200.dp)
                    .offset {
                        IntOffset(
                            0,
                            (modalHeight - bottomSheetState.requireOffset() - footerHeight).toInt()
                        )
                    }
                    .fillMaxWidth()
                    .padding(top = 12.dp, end = 0.dp, start = 0.dp, bottom = 42.dp)
                    .onGloballyPositioned {
                        footerHeight = (((it.size.height * 1)) + bottomPadding + 0).toInt()
                    }
                    .drawBehind {
                        drawLine(
                            color = Color.DarkGray,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 5f
                        )
                    }
                    .shadow(6.dp, RoundedCornerShape(1.dp), spotColor = Color.Gray)
                    .background(color = config.containerColor, RoundedCornerShape(1.dp)),
                horizontalArrangement = horizontalArrangement,
                contentPadding = PaddingValues(16.dp)
            ) {
                items(pickerModes.size, key = { pickerModes[it].title }) { index ->
                    val item = pickerModes[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = "${item.pickerType.name} icon",
                            Modifier
                                .size(item.iconSize)
                                .padding(4.dp)
                                .shadow(2.dp, item.shape, spotColor = Color.Gray)
                                .background(item.shapeColor, item.shape)
                                .then(
                                    if (currentType == item.pickerType) Modifier
                                        .padding(4.dp)
                                        .border(1.5.dp, color = Color.White.copy(alpha = 0.5f), shape = CircleShape)
                                    else Modifier
                                )
                                .padding(12.dp)
                                .clickable { itemTypeClick(item.pickerType) },
                            tint = item.iconTint,
                        )

                        Text(
                            text = item.title,
                            color = if (currentType == item.pickerType) item.selectedColor else item.itemTextStyle.color,
                            textAlign = TextAlign.Center,
                            style = item.itemTextStyle,
                            modifier = Modifier.clickable { itemTypeClick(item.pickerType) }
                        )
                    }
                }
            }


            Box(
                modifier = Modifier
                    .alpha(doneAlpha.value)
                    .offset {
                        IntOffset(
                            modalWidth - (68.dp * density).value.toInt(),
                            (modalHeight - bottomSheetState.requireOffset() - (1.1 * footerHeight)).toInt()
                        )
                    }
                    .clickable { onDoneClick() }
                    .padding(0.dp)) {

                Icon(
                    modifier =
                    Modifier
                        .size(config.doneIconSize)
                        .shadow(2.dp, CircleShape, spotColor = Color.Gray)
                        .background(config.doneIconBackground, CircleShape),
                    tint = config.doneIconTint,
                    painter = painterResource(id = config.doneIcon),
                    contentDescription = "done icon"
                )

                if (selectedCount > 0)
                    Text(
                        text = "$selectedCount",
                        modifier = Modifier
                            .wrapContentSize(unbounded = true)
                            .border(1.dp, config.containerColor, shape = CircleShape)
                            .sizeIn(20.dp, 20.dp, 30.dp, 30.dp)
                            .shadow(2.dp, CircleShape, spotColor = Color.Gray)
                            .drawBehind {
                                drawCircle(
                                    color = config.doneBadgeBackgroundColor,
                                    radius = this.size.maxDimension
                                )
                            }
                            .scale(1f)
                            .align(Alignment.BottomEnd),
                        style = config.doneBadgeStyle,
                    )
            }
        }
    }

    LaunchedEffect(key1 = isLandscape, block = {
        delay(500)
        bottomSheetState.expand()
    })


}

@Composable
fun AudioScreen(
    config: PickerConfig,
    modalHeight: Int,
    files: ImmutableList<PickerFile>,
    onChangeSelect: (PickerFile) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .defaultMinSize(minHeight = modalHeight.toDp())
            .fillMaxWidth()
    ) {
        TextField(
            value = searchText,
            onValueChange = {
                onSearchChange(it)
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = config.searchTextStyle.color,
                focusedContainerColor = Color.DarkGray,
                unfocusedContainerColor = Color.DarkGray,
                disabledContainerColor = Color.DarkGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
            placeholder = {
                Text(
                    config.searchTextHint,
                    modifier = Modifier.fillMaxWidth(),
                    style = config.searchTextHintStyle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )


        LazyColumn(
            modifier = Modifier.clipScrollableContainer(Orientation.Vertical),
            state = rememberLazyListState(),
            contentPadding = PaddingValues(
                top = 16.dp,
                end = 16.dp,
                start = 16.dp,
                bottom = 150.dp
            ),
        ) {
            items(files.size, key = { files[it].path }) { index ->
                MediaAudioItem(files[index]) {
                    onChangeSelect(files[index])
                }
            }
        }
    }

}

@Composable
fun ImageAndVideoScreen(
    config: PickerConfig,
    imageLoader: ImageLoader,
    modalHeight: Int,
    files: ImmutableList<PickerFile>,
    mode: PickerMode,
    onChangeSelect: (PickerFile) -> Unit,
    onCameraPhoto: (PickerFile) -> Unit,
    isLandscape: Boolean = LocalConfiguration.current.orientation != Configuration.ORIENTATION_PORTRAIT

) {
    val context = LocalContext.current
    val mediaListState = rememberLazyGridState()
    val cameraPicture = remember { mutableStateOf<Bitmap?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        cameraPicture.value = it
        PickerUtils.saveBitmapToStorage(it)?.let { file ->
            onCameraPhoto(PickerFile(file.path, file, selected = true))
        } ?: run {
            Toast.makeText(context, "Some error while write picture in storage, check permissions...", Toast.LENGTH_LONG).show()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(120.dp),
        state = mediaListState,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = modalHeight.toDp())
            .clipScrollableContainer(Orientation.Vertical),
        contentPadding = PaddingValues(
            top = 16.dp,
            end = 16.dp,
            start = 16.dp,
            bottom = 150.dp
        )
    ) {
        items(items = files, key = { it.path }) { pickerFile ->
            if (pickerFile.path == "show camera") {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .background(config.cameraIconBackground, RoundedCornerShape(16.dp))
                        .aspectRatio(1f)
                ) {
                    Icon(
                        painter = painterResource(id = config.cameraIcon),
                        contentDescription = "camera",
                        tint = config.cameraIconTint,
                        modifier = Modifier
                            .padding(24.dp)
                            .clickable {
                                cameraLauncher.launch(null)
                            }
                    )
                }
            } else {
                MediaItem(pickerFile, config, mode, imageLoader = imageLoader) {
                    onChangeSelect(pickerFile)
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun FileScreen(config: PickerConfig, onStoragePicker: (List<PickerFile>) -> Unit) {
    val context = LocalContext.current
    val files = remember { mutableStateOf(listOf<Uri>()) }
    val pathListener =
        object : HandlePathOzListener.MultipleUri {
            override fun onRequestHandlePathOz(listPathOz: List<PathOz>, tr: Throwable?) {
                if (listPathOz.isNotEmpty()) {
                    listPathOz.map { pathOz -> PickerFile(path = pathOz.path, file = File(pathOz.path), selected = false) }
                        .let { filePickers -> onStoragePicker.invoke(filePickers.distinctBy { it.path }) }
                }
            }
        }

    val launcher = if (config.maxSelection < 2) {
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                files.value = listOf(uri)
                HandlePathOz(context, pathListener).getListRealPath(files.value)
            }
        }
    } else {
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            files.value = uris
            HandlePathOz(context, pathListener).getListRealPath(files.value)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(400.dp)
    ) {
        config.apply {
            FileItem(
                icon = storageIcon,
                iconBackground = storageIconBackground,
                iconTint = storageIconTint,
                iconSize = storageIconSize,
                title = storageTitle,
                description = storageDescription,
                titleTextStyle = storageTitleStyle,
                descriptionTextStyle = storageDescriptionStyle,
                supportRtl = true,
                onClicked = {
                    launcher.launch("*/*")
                }
            )

            Spacer(
                modifier = Modifier
                    .height(3.dp)
                    .background(Color.Gray)
            )

            FileItem(
                icon = galleryIcon,
                iconBackground = galleryIconBackground,
                iconTint = galleryIconTint,
                iconSize = galleryIconSize,
                title = galleryTitle,
                description = galleryDescription,
                titleTextStyle = galleryTitleStyle,
                descriptionTextStyle = galleryDescriptionStyle,
                supportRtl = true,
                onClicked = {
                    launcher.launch("image/*")
                }
            )
        }
    }
}
