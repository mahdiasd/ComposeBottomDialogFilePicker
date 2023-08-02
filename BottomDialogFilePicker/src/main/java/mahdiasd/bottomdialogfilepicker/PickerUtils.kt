package mahdiasd.bottomdialogfilepicker

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.io.FileOutputStream

object PickerUtils {

    val allModes = listOf(
        PickerMode(PickerType.Image, title = "عکس"),
        PickerMode(PickerType.Video, "ویدیو"),
        PickerMode(PickerType.File, "فایل"),
        PickerMode(PickerType.Audio, "موزیک"),
    )

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun permissionState(enableCamera: Boolean): MultiplePermissionsState {
        val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            mutableListOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            )
        }

        if (enableCamera) list.add(android.Manifest.permission.CAMERA)

        return rememberMultiplePermissionsState(list)
    }

    fun getImage(context: Context, enableCamera: Boolean): List<PickerFile> {
        val list = mutableListOf<PickerFile>()
        if (enableCamera) {
            list.add(PickerFile("show camera"))
        }
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)

        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"

        val cursor: Cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns,
            null,
            null,
            orderBy
        ) ?: return emptyList()

        while (cursor.moveToNext()) {
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            list.add(PickerFile(cursor.getString(dataColumnIndex)))
        }

        cursor.close()
        return list
    }

    fun getVideo(context: Context): List<PickerFile> {
        val list = mutableListOf<PickerFile>()
        val columns = arrayOf(
            MediaStore.Video.VideoColumns.DATA,
            MediaStore.Video.VideoColumns._ID
        )

        val orderBy = MediaStore.Video.VideoColumns.DATE_ADDED + " DESC"

        val cursor: Cursor = context.contentResolver.query(
            /* uri = */ MediaStore.Video.Media.EXTERNAL_CONTENT_URI, /* projection = */ columns, /* selection = */ null,
            /* selectionArgs = */ null, /* sortOrder = */ orderBy
        ) ?: return emptyList()

        while (cursor.moveToNext()) {
            val dataColumnIndex: Int =
                cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
            list.add(PickerFile(cursor.getString(dataColumnIndex)))
        }
        cursor.close()
        return list
    }

    fun getAudio(context: Context): List<PickerFile> {
        val list = mutableListOf<PickerFile>()
        val columns = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.DATA,
        )
        val orderBy = MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            columns,
            null,
            null,
            orderBy
        ) ?: return emptyList()

        while (cursor.moveToNext()) {
            val dataColumnIndex: Int = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            list.add(PickerFile(cursor.getString(dataColumnIndex)))
        }
        cursor.close()
        return list
    }


    fun saveBitmapToStorage(bitmap: Bitmap?): File? {
        if (bitmap == null) return null

        // Generate a unique file name with date
        val fileName = "image_${System.currentTimeMillis()}.png"

        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
        val file = File(imagesDir, fileName)

        return try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos)
            fos.close()

            if (file.exists()) file
            else null
        } catch (e: Exception) {
            null
        }
    }

    @Composable
    fun Dp.toPx(): Float {
        val density = LocalDensity.current.density
        return density * value
    }

    @Composable
    fun Int.toDp(): Dp {
        val density = LocalDensity.current.density
        return (this / density).dp
    }

    fun Any?.printToLog(plusTag: String = "", tag: String = "MyLog") {
        Log.d(tag, plusTag + " " + toString())
    }

}