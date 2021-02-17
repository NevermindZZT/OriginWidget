package com.letter.originwidget.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.letter.originwidget.database.entity.WidgetEntity
import com.letter.originwidget.repository.OriginWidgetRepo
import com.letter.utils.AppUtils
import kotlinx.coroutines.launch

/**
 * widget config view model
 * @property widgetEntity MutableLiveData<(com.letter.originwidget.database.entity.WidgetEntity..com.letter.originwidget.database.entity.WidgetEntity?)>
 * @property appList List<AppInfo>
 * @property selectedAppIndex MutableLiveData<(kotlin.Int..kotlin.Int?)>
 * @property marginHorizontal MutableLiveData<(kotlin.Int..kotlin.Int?)>
 * @property marginVertical MutableLiveData<(kotlin.Int..kotlin.Int?)>
 * @property marginIcon MutableLiveData<(kotlin.Int..kotlin.Int?)>
 * @property radius MutableLiveData<(kotlin.Int..kotlin.Int?)>
 * @property originWidgetRepo OriginWidgetRepo
 * @constructor 构造器
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class WidgetConfigViewModel(application: Application): AndroidViewModel(application) {

    val widgetEntity by lazy {
        MutableLiveData(WidgetEntity())
    }
    val appList by lazy {
        AppUtils.getAppInfoList(application, PackageManager.GET_ACTIVITIES).filter {
            it.hasMainActivity
            true
        }.sortedBy {
            it.name
        }
    }
    val selectedAppIndex = MutableLiveData(-1)
    val marginHorizontal = MutableLiveData(0)
    val marginVertical = MutableLiveData(0)
    val marginIcon = MutableLiveData(0)
    val radius = MutableLiveData(0)

    private val originWidgetRepo by lazy {
        OriginWidgetRepo()
    }

    init {
        marginHorizontal.value = originWidgetRepo.getDefaultParam(application, OriginWidgetRepo.KEY_MARGIN_HORIZONTAL)
        marginVertical.value = originWidgetRepo.getDefaultParam(application, OriginWidgetRepo.KEY_MARGIN_VERTICAL)
        marginIcon.value = originWidgetRepo.getDefaultParam(application, OriginWidgetRepo.KEY_MARGIN_ICON)
        radius.value = originWidgetRepo.getDefaultParam(application, OriginWidgetRepo.KEY_RADIUS)
    }

    fun getPreviewBackground(): Bitmap? =
        if (widgetEntity.value != null) {
            originWidgetRepo.getPreviewBackground(getApplication(), widgetEntity.value!!)
        } else {
            null
        }

    fun getBackground(): Bitmap? =
        if (widgetEntity.value != null) {
            originWidgetRepo.getBackground(getApplication(), widgetEntity.value!!)
        } else {
            null
        }

    fun getIcon(): Bitmap? =
        if (widgetEntity.value != null) {
            originWidgetRepo.getIcon(getApplication(), widgetEntity.value!!)
        } else {
            null
        }

    fun saveWidget(widgetId: Int, action: (()->Unit)? = null) {
        widgetEntity.value?.id = widgetId
        widgetEntity.value?.marginHorizontal = marginHorizontal.value ?: 0
        widgetEntity.value?.marginVertical = marginVertical.value ?: 0
        widgetEntity.value?.marginIcon = marginIcon.value ?: 0
        widgetEntity.value?.radius = radius.value ?: 0
        viewModelScope.launch {
            originWidgetRepo.setDefaultParam(getApplication()) {
                putInt(OriginWidgetRepo.KEY_MARGIN_HORIZONTAL, marginHorizontal.value ?: 0)
                putInt(OriginWidgetRepo.KEY_MARGIN_VERTICAL, marginVertical.value ?: 0)
                putInt(OriginWidgetRepo.KEY_MARGIN_ICON, marginIcon.value ?: 0)
                putInt(OriginWidgetRepo.KEY_RADIUS, radius.value ?: 0)
            }
            originWidgetRepo.saveWidget(getApplication(), widgetEntity.value!!, action)
        }
    }

}