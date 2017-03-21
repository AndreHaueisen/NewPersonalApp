package com.andrehaueisen.fitx.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.widget.RemoteViews
import com.andrehaueisen.fitx.Constants
import com.andrehaueisen.fitx.R
import com.andrehaueisen.fitx.client.ClientActivity
import com.andrehaueisen.fitx.personal.PersonalActivity

/**
 * Created by andre on 3/10/2017.
 */
class ClassWidgetProvider : AppWidgetProvider(){

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {

        appWidgetIds?.forEach {

            val views = configureView(context)
            appWidgetManager?.updateAppWidget(it, views)

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE == (intent?.action)) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds (ComponentName(context, javaClass))
            appWidgetManager?.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list)
        }
    }

    private fun configureView(context: Context?) : RemoteViews{

        val views = RemoteViews(context?.packageName, R.layout.widget_main)

        val intent : Intent
        val remoteAdapterIntent = Intent(context, ClassWidgetViewsService::class.java)

        if(isPersonal(context)) {
            intent = Intent(context, PersonalActivity::class.java)
        }else{
            intent = Intent(context, ClientActivity::class.java)
        }

        val clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(intent)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_CLEAR_TASK)

        views.setOnClickPendingIntent(R.id.widget, pendingIntent)
        views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate)

        views.setRemoteAdapter(R.id.widget_list, remoteAdapterIntent)
        views.setEmptyView(R.id.widget_list, R.id.widget_empty)

        return views
    }

    private fun isPersonal(context: Context?) : Boolean{

        val personalKey = getPersonalUniqueKey(context)
        return personalKey != null
    }

    private fun getPersonalUniqueKey(context: Context?) : String?{
        return context?.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)?.getString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, null)
    }

    private fun getClientUniqueKey(context: Context?) : String?{
        return context?.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)?.getString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, null)
    }


}