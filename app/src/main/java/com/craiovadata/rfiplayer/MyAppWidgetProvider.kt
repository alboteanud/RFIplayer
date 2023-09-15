package com.craiovadata.rfiplayer

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class MyAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (appWidgetId in appWidgetIds) {
            // Create an Intent to launch ExampleActivity
            val intentToMainActivity = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intentToMainActivity, PendingIntent.FLAG_IMMUTABLE)

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            val views = RemoteViews(context.packageName, R.layout.my_widget_layout)
            views.setOnClickPendingIntent(R.id.widgetView, pendingIntent)

            val pendingIntentStartService = MyService.getPendingIntentTogglePlayerState(context)
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntentStartService)
//            views.setTextViewText(R.id.widgetTextViewTemperature, tempTxt)

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

    }


}
