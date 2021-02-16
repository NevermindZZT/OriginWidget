package com.letter.originwidget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.sendBroadcast

/**
 * Application
 *
 * @author Letter(nevermindzt@gmail.com)
 * @since 1.0.0
 */
class LetterApplication : Application() {

    companion object {
        /**
         * Application 单例
         */
        private var instance: LetterApplication ?= null

        /**
         * 获取Application实例
         * @return ScheduleApplication Application实例
         */
        @JvmStatic
        fun instance() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        sendBroadcast("android.appwidget.action.APPWIDGET_UPDATE") {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        }
    }
}