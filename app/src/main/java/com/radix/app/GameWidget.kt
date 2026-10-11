package com.radix.app

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalDate

/** Daily-challenge home-screen widget. Reads the same offline prefs as the app. */
class GameWidget:AppWidgetProvider(){
    override fun onUpdate(context:Context,manager:AppWidgetManager,ids:IntArray){ids.forEach{updateOne(context,manager,it)}}
    companion object{
        fun refreshAll(context:Context){runCatching{val manager=AppWidgetManager.getInstance(context);manager.getAppWidgetIds(ComponentName(context,GameWidget::class.java)).forEach{updateOne(context,manager,it)}}}
        private fun updateOne(context:Context,manager:AppWidgetManager,id:Int){
            val prefs=context.getSharedPreferences("radix_local",0)
            val mastered=runCatching{StudyData.progress(prefs.getString("study_progress","[]")?:"[]").size}.getOrDefault(0)
            val streak=prefs.getInt("streak_days",0)
            val questions=PracticeCatalog.questions
            val q=questions[GameEngine.dailyIndex(LocalDate.now().toEpochDay(),questions.size)]
            val language=AppStrings.systemLanguage(context.resources.configuration.locales)
            val views=RemoteViews(context.packageName,R.layout.widget_daily)
            views.setTextViewText(R.id.widget_title,"${context.getString(R.string.widget_title_default)} · ${AppStrings.translate(q.title,language)}")
            views.setTextViewText(R.id.widget_prompt,AppStrings.translate(q.prompt,language).take(140))
            val tier=AppStrings.translate(listOf("Easy","Intermediate","Advanced")[q.tier],language)
            val progress=AppStrings.translate("$mastered/${questions.size} mastered",language)
            val streakLabel=AppStrings.translate(if(streak>0)"🔥 $streak-day streak"else"start your streak",language)
            views.setTextViewText(R.id.widget_meta,"$tier · $progress · $streakLabel")
            val open=PendingIntent.getActivity(context,0,Intent(context,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_root,open)
            manager.updateAppWidget(id,views)
        }
    }
}
