package com.letter.originwidget.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Widget 数据 Entity
 * @property id Int id
 * @property sourcePath String 资源文件路径
 * @constructor 构造器
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
@Entity(tableName = "widget")
data class WidgetEntity (@PrimaryKey var id: Int = 0,
                         @ColumnInfo(name = "packageName") var packageName: String? = null,
                         @ColumnInfo(name = "backgroundPath") var backgroundPath: String? = null,
                         @ColumnInfo(name = "iconPath") var iconPath: String? = null,
                         @ColumnInfo(name = "backgroundType") var backgroundType: Int = SOURCE_TYPE_ICON,
                         @ColumnInfo(name = "iconType") var iconType: Int = SOURCE_TYPE_ICON,
                         @ColumnInfo(name = "radius") var radius: Int = 0,
                         @ColumnInfo(name = "marginHorizontal") var marginHorizontal: Int = 0,
                         @ColumnInfo(name = "marginVertical") var marginVertical: Int = 0,
                         @ColumnInfo(name = "marginIcon") var marginIcon: Int = 0,
                         @ColumnInfo(name = "sourcePath") var sourcePath: String? = null) {
    companion object {
        const val SOURCE_TYPE_ICON = 0
        const val SOURCE_TYPE_COLOR = 1
        const val SOURCE_TYPE_PICTURE = 2
    }
}