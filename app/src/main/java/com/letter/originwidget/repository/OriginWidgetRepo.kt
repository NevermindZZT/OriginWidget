package com.letter.originwidget.repository

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ImageUtils
import com.letter.originwidget.database.AppDatabase
import com.letter.originwidget.database.entity.WidgetEntity
import com.letter.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * origin widget repository
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class OriginWidgetRepo {

    fun getBlurBitmap(bitmap: Bitmap?, blur: Float) =
        ImageUtils.fastBlur(bitmap, 0.3f, blur)

    fun getBlurBitmap(drawable: Drawable?, blur: Float) =
        getBlurBitmap(ConvertUtils.drawable2Bitmap(drawable), blur)

    fun getPreviewBackground(context: Context, widgetEntity: WidgetEntity): Bitmap? =
        getBlurBitmap(getBackground(context, widgetEntity), 25f)

    fun getBackground(context: Context, widgetEntity: WidgetEntity): Bitmap? =
        when (widgetEntity.backgroundType) {
            WidgetEntity.SOURCE_TYPE_ICON -> {
                ConvertUtils.drawable2Bitmap(
                        AppUtils.getAppInfo(context, widgetEntity.packageName ?: "", PackageManager.GET_ACTIVITIES).icon)
            }
            else -> null
        }

    fun getWidgetBackground(context: Context, widgetEntity: WidgetEntity, width: Int, height: Int): Bitmap? {
        var bitmap = getBackground(context, widgetEntity)
        if (bitmap != null && width > 0 && height > 0) {
            if (widgetEntity.backgroundType == WidgetEntity.SOURCE_TYPE_ICON) {
                bitmap = ImageUtils.clip(bitmap, bitmap.width / 4, bitmap.height / 4,
                        bitmap.width / 2, bitmap.height / 2)!!
            }

            val rateWidget = height.toFloat() / width.toFloat()
            val rateBitmap = bitmap.height.toFloat() / bitmap.width.toFloat()
            bitmap = if (rateBitmap > rateWidget) {
                val scaleHeight = bitmap.height * width / bitmap.width
                ImageUtils.clip(
                        ImageUtils.scale(bitmap, width, scaleHeight),
                        0,
                        (scaleHeight - height) / 2,
                        width,
                        height
                )
            } else {
                val scaleWidth = bitmap.width * height / bitmap.height
                ImageUtils.clip(
                        ImageUtils.scale(bitmap, scaleWidth, height),
                        (scaleWidth - width) / 2,
                        0,
                        width,
                        height
                )
            }

            return ImageUtils.toRoundCorner(getBlurBitmap(bitmap, 25f), widgetEntity.radius.toFloat(), true)
        }
        return null
    }

    fun getIcon(context: Context, widgetEntity: WidgetEntity): Bitmap? =
        when (widgetEntity.iconType) {
            WidgetEntity.SOURCE_TYPE_ICON -> {
                ConvertUtils.drawable2Bitmap(
                    AppUtils.getAppInfo(context, widgetEntity.packageName ?: "", PackageManager.GET_ACTIVITIES).icon)
            }
            else -> null
        }

    suspend fun saveWidget(context: Context, widgetEntity: WidgetEntity, action: (()->Unit)? = null) {
        withContext(Dispatchers.IO) {
            AppDatabase.instance(context).widgetDao().insert(widgetEntity)
        }
        withContext(Dispatchers.Main) {
            action?.invoke()
        }
    }

    suspend fun deleteWidget(context: Context, widgetId: Int, action: (()->Unit)? = null) {
        withContext(Dispatchers.IO) {
            AppDatabase.instance(context).widgetDao().deleteById(widgetId)
        }
        withContext(Dispatchers.Main) {
            action?.invoke()
        }
    }
}