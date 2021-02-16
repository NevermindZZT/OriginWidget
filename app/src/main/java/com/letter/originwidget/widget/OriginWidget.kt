package com.letter.originwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.dp2px
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.widget.RemoteViews
import com.blankj.utilcode.util.IntentUtils
import com.letter.originwidget.R
import com.letter.originwidget.database.AppDatabase
import com.letter.originwidget.repository.OriginWidgetRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Implementation of App Widget functionality.
 */
class OriginWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val widgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        if (widgetId != -1) {
            updateAppWidget(context!!, AppWidgetManager.getInstance(context), widgetId!!)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        GlobalScope.launch {
            val originWidgetRepo = OriginWidgetRepo()
            appWidgetIds?.forEach {
                originWidgetRepo.deleteWidget(context!!, it)
            }
        }
    }
}

/**
 * 更新 widget
 * @param context Context context
 * @param appWidgetManager AppWidgetManager app widget manager
 * @param appWidgetId Int widget id
 */
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    GlobalScope.launch(Dispatchers.IO) {
        val widget = AppDatabase.instance(context)
            .widgetDao()
            .get(appWidgetId)
            ?: return@launch

        val originWidgetRepo = OriginWidgetRepo()

        withContext(Dispatchers.Main) {
            val views = RemoteViews(context.packageName, R.layout.origin_widget)

            val width = getWidgetWidth(context, appWidgetManager, appWidgetId)
            val height = getWidgetHeight(context, appWidgetManager, appWidgetId)

            views.apply {
                setImageViewBitmap(R.id.background_image, originWidgetRepo.getWidgetBackground(context, widget, width, height))
                setImageViewBitmap(R.id.icon_image, originWidgetRepo.getIcon(context, widget))
                setViewPadding(R.id.icon_image, widget.marginIcon, widget.marginIcon,
                    widget.marginIcon, widget.marginIcon)
                setViewPadding(R.id.background_image, widget.marginHorizontal,
                    widget.marginVertical, widget.marginHorizontal, widget.marginVertical)

                val intent = IntentUtils.getLaunchAppIntent(widget.packageName)
                val pi = PendingIntent.getActivity(context, widget.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                setOnClickPendingIntent(R.id.widget_layout, pi)
            }

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }
}

/**
 * 获取 widget 宽度
 * @param context Context context
 * @param appWidgetManager AppWidgetManager app widget manager
 * @param widgetId Int widget id
 * @return Int widget 宽度
 */
internal fun getWidgetWidth(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int): Int {
    val dp = appWidgetManager.getAppWidgetOptions(widgetId)
            .getInt(if (context.resources.configuration.orientation == ORIENTATION_PORTRAIT)
                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 0)
    return context.dp2px(dp).toInt()
}

/**
 * 获取 widget 高度
 * @param context Context context
 * @param appWidgetManager AppWidgetManager app widget manager
 * @param widgetId Int widget id
 * @return Int widget 高度
 */
internal fun getWidgetHeight(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int): Int {
    val dp = appWidgetManager.getAppWidgetOptions(widgetId)
            .getInt(if (context.resources.configuration.orientation == ORIENTATION_PORTRAIT)
                AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
    return context.dp2px(dp).toInt()
}
