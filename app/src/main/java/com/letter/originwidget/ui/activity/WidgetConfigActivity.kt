package com.letter.originwidget.ui.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.sendBroadcast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.letter.originwidget.R
import com.letter.originwidget.databinding.ActivityWidgetConfigBinding
import com.letter.originwidget.viewmodel.WidgetConfigViewModel
import com.letter.presenter.ViewPresenter
import kotlin.properties.Delegates

/**
 * widget config activity
 * @property binding ActivityWidgetConfigBinding binding
 * @property model WidgetConfigViewModel view model
 * @property widgetId Int widget id
 *
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class WidgetConfigActivity : AppCompatActivity(), ViewPresenter {

    private lateinit var binding: ActivityWidgetConfigBinding
    private val model by lazy {
        ViewModelProvider
            .AndroidViewModelFactory(application)
            .create(WidgetConfigViewModel::class.java)
    }
    private var widgetId by Delegates.notNull<Int>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWidgetConfigBinding.inflate(layoutInflater)
        initBinding()
        initModel()

        setResult(RESULT_CANCELED)
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContentView(binding.root)
    }

    private fun initBinding() {
        binding.let {
            it.lifecycleOwner = this
            it.model = model
            it.presenter = this
        }
    }

    private fun initModel() {
        model.apply {
            selectedAppIndex.observe(this@WidgetConfigActivity) {
                if (it > 0) {
                    model.widgetEntity.value?.packageName = model.appList[it].packageName
                    binding.previewIconImage.setImageBitmap(model.getIcon())
                    binding.previewBackgroundImage.setImageBitmap(model.getPreviewBackground())
                    binding.iconImage.setImageBitmap(model.getIcon())
                    binding.backgroundImage.setImageBitmap(model.getBackground())
                }
            }
        }
    }

    private fun onAppLayoutClick() {
        MaterialDialog(this).show {
            listItems(items = model.appList.map { it.name ?: "" }) { dialog, index, _ ->
                model.selectedAppIndex.value = index
                dialog.dismiss()
            }
        }
    }

    private fun onSaveButtonClick() {
        model.saveWidget(widgetId) {
            sendBroadcast("android.appwidget.action.APPWIDGET_UPDATE") {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }

            val result = Intent()
            result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            setResult(RESULT_OK, result)
            finish()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.app_layout -> onAppLayoutClick()
            R.id.save_button -> onSaveButtonClick()
        }
    }


}