package com.letter.originwidget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.letter.originwidget.database.dao.WidgetDao
import com.letter.originwidget.database.entity.WidgetEntity

/**
 * 数据库
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
@Database(entities = [WidgetEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    /**
     * 获取WidgetDao
     * @return WidgetDao widget dao
     */
    abstract fun widgetDao(): WidgetDao

    companion object {

        /**
         * AppDatabase 单例
         */
        private var instance: AppDatabase? = null

        /**
         * 获取数据库实例
         * @param context Context context
         * @return AppDatabase 数据库实例
         */
        @Synchronized
        fun instance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room
                    .databaseBuilder(context.applicationContext, AppDatabase::class.java, "user.db")
                    .build()
            }
            return instance!!
        }
    }
}