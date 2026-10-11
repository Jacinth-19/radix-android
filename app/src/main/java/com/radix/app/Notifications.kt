package com.radix.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.media.AudioAttributes
import android.net.Uri
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import java.time.LocalDate
import java.time.LocalTime
import org.json.JSONArray
import org.json.JSONObject

internal data class InboxItem(val id:Long,val title:String,val body:String,val day:String)

/** Offline study reminders. No server, no tracking; all content is computed on-device. */
internal object StudyReminder {
    const val CHANNEL="radix_study_v2";private const val LEGACY_CHANNEL="radix_study";const val DAILY_ID=101;const val MILESTONE_ID=102;const val TEST_ID=103
    val milestones=listOf(3,7,14,30,60,100)
    private val tipsEn=listOf(
        "11111111 means 255 unsigned — and −1 in 8-bit two’s complement.",
        "A′B + AB′ and A XOR B match in every truth-table row.",
        "On a 4-variable K-map, the four corners are one wrapped group.",
        "A D flip-flop captures only on a rising edge — a high level is not an edge.",
        "In A + BC, the AND happens before the OR.",
        "Gray code changes one bit between neighbours; binary can change many.",
        "A full adder with A=B=Cin=1 gives Sum=1, Cout=1. Total 3 needs two bits.")
    private val tipsFr=listOf(
        "11111111 vaut 255 non signé — et −1 en complément à deux sur 8 bits.",
        "A′B + AB′ et A XOR B coïncident sur chaque ligne de la table.",
        "Sur un tableau de Karnaugh à 4 variables, les quatre coins forment un groupe.",
        "Une bascule D capture uniquement sur front montant.",
        "Dans A + BC, le ET passe avant le OU.",
        "Le code Gray change un seul bit entre voisins.",
        "Un additionneur complet avec A=B=Cin=1 donne S=1, Cout=1.")
    private val tipsEs=listOf(
        "11111111 vale 255 sin signo y −1 en complemento a dos de 8 bits.",
        "A′B + AB′ y A XOR B coinciden en todas las filas de verdad.",
        "En un mapa de Karnaugh de 4 variables, las cuatro esquinas forman un grupo.",
        "Un flip-flop D captura solo en el flanco ascendente.",
        "En A + BC, AND se evalúa antes que OR.",
        "El código Gray cambia un bit entre vecinos.",
        "Un sumador completo con A=B=Cin=1 da Sum=1 y Cout=1.")
    fun dayString():String=LocalDate.now().toString()
    fun prefs(ctx:Context)=ctx.getSharedPreferences("radix_local",0)
    fun notificationsAllowed(ctx:Context)=Build.VERSION.SDK_INT<33||ContextCompat.checkSelfPermission(ctx,android.Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED
    fun dailyContent(lang:String,hour:Int,streak:Int,dayOfYear:Int):Pair<String,String>{
        val greeting=when(lang){
            "fr"->when{hour<12->"Bonjour";hour<18->"Bon après-midi";else->"Bonsoir"}
            "es"->when{hour<12->"Buenos días";hour<18->"Buenas tardes";else->"Buenas noches"}
            else->when{hour<12->"Good morning";hour<18->"Good afternoon";else->"Good evening"}
        }
        val tips=when(lang){"fr"->tipsFr;"es"->tipsEs;else->tipsEn};val tip=tips[dayOfYear%tips.size]
        val title=when(lang){
            "fr"->if(streak>=2)"$greeting — série de $streak jours 🔥"else"$greeting — une petite découverte ?"
            "es"->if(streak>=2)"$greeting — racha de $streak días 🔥"else"$greeting — ¿una pequeña idea?"
            else->if(streak>=2)"$greeting — $streak-day streak 🔥"else"$greeting — time for a tiny discovery?"
        }
        val body=when(lang){
            "fr"->if(streak>=2)"Une question garde la série en vie. Astuce : $tip"else"Une carte d’entraînement par jour suffit. Astuce : $tip"
            "es"->if(streak>=2)"Una pregunta mantiene la racha. Consejo: $tip"else"Una tarjeta al día es suficiente. Consejo: $tip"
            else->if(streak>=2)"One question keeps it alive. Tip: $tip"else"One practice card a day is enough. Tip: $tip"
        }
        return title to body
    }
    fun milestoneContent(lang:String,streak:Int):Pair<String,String> = when(lang){
        "fr"->"🔥 Série de $streak jours !" to "Tu progresses vraiment. Continue à ton rythme — sans pression."
        "es"->"🔥 ¡Racha de $streak días!" to "Estás avanzando. Sigue a tu ritmo, sin presión."
        else->"🔥 $streak-day streak!" to "You’re really progressing. Keep your own pace — no pressure."
    }
    fun loadInbox(ctx:Context):List<InboxItem> = runCatching{
        val a=JSONArray(prefs(ctx).getString("inbox_json","[]"));(0 until a.length()).map{val o=a.getJSONObject(it);InboxItem(o.getLong("id"),o.getString("title"),o.getString("body"),o.getString("day"))}
    }.getOrDefault(emptyList())
    fun pushInbox(ctx:Context,title:String,body:String){
        val items=(listOf(InboxItem(System.currentTimeMillis(),title,body,dayString()))+loadInbox(ctx)).take(20)
        val a=JSONArray();items.forEach{a.put(JSONObject().put("id",it.id).put("title",it.title).put("body",it.body).put("day",it.day))}
        prefs(ctx).edit{putString("inbox_json",a.toString())}
    }
    fun clearInbox(ctx:Context){prefs(ctx).edit{putString("inbox_json","[]")}}
    private fun channel(ctx:Context){
        val m=ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val lang=prefs(ctx).getString("app_language","en")?:"en"
        val name=when(lang){"fr"->"Rappels d’étude";"es"->"Recordatorios de estudio";else->"Study reminders"}
        val sound=Uri.parse("android.resource://${ctx.packageName}/${R.raw.radix_reminder}")
        val audio=AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        val fresh=NotificationChannel(CHANNEL,name,NotificationManager.IMPORTANCE_DEFAULT).apply{
            description=when(lang){"fr"->"Rappels hors ligne avec sonnerie facultative, réglable par vous.";"es"->"Recordatorios sin conexión con tono opcional que puedes controlar.";else->"Offline reminders with an optional chime you control."}
            setSound(sound,audio)
        }
        m.getNotificationChannel(LEGACY_CHANNEL)?.let{old->
            // A new ID is needed to add the bundled sound, but do not undo an old mute/custom sound.
            if(old.importance!=NotificationManager.IMPORTANCE_DEFAULT)fresh.setImportance(old.importance)
            when(val oldSound=old.sound){null->fresh.setSound(null,null);Settings.System.DEFAULT_NOTIFICATION_URI->Unit;else->fresh.setSound(oldSound,audio)}
        }
        m.createNotificationChannel(fresh)
    }
    fun prepareChannel(ctx:Context)=channel(ctx)
    fun show(ctx:Context,id:Int,title:String,body:String){
        channel(ctx);if(!notificationsAllowed(ctx))return
        val open=PendingIntent.getActivity(ctx,0,Intent(ctx,MainActivity::class.java).apply{flags=Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP},PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val n=NotificationCompat.Builder(ctx,CHANNEL).setSmallIcon(R.drawable.ic_radix).setContentTitle(title).setContentText(body).setStyle(NotificationCompat.BigTextStyle().bigText(body)).setContentIntent(open).setAutoCancel(true).build()
        (ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(id,n)
    }
    fun schedule(ctx:Context){
        val p=prefs(ctx);val am=ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi=PendingIntent.getBroadcast(ctx,0,Intent(ctx,ReminderReceiver::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.cancel(pi)
        if(!p.getBoolean("notif_enabled",false))return
        channel(ctx)
        val at=java.util.Calendar.getInstance().apply{set(java.util.Calendar.HOUR_OF_DAY,p.getInt("notif_hour",19).coerceIn(0,23));set(java.util.Calendar.MINUTE,p.getInt("notif_minute",0).coerceIn(0,59));set(java.util.Calendar.SECOND,0);set(java.util.Calendar.MILLISECOND,0);if(timeInMillis<=System.currentTimeMillis())add(java.util.Calendar.DAY_OF_YEAR,1)}
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,at.timeInMillis,AlarmManager.INTERVAL_DAY,pi)
    }
    fun fireDaily(ctx:Context){
        val p=prefs(ctx);if(!p.getBoolean("notif_enabled",false))return
        val lang=p.getString("app_language","en")?:"en";val streak=p.getInt("streak_days",0)
        val now=LocalTime.now();val (title,body)=dailyContent(lang,now.hour,if(p.getBoolean("notif_streak",true))streak else 0,LocalDate.now().dayOfYear)
        val finalBody=if(p.getBoolean("notif_tips",true))body else if(lang=="fr")"Une carte d’entraînement par jour suffit."else"One practice card a day is enough."
        pushInbox(ctx,title,finalBody)
        if(notificationsAllowed(ctx))show(ctx,DAILY_ID,title,finalBody)
    }
    fun milestone(ctx:Context,streak:Int){
        if(streak !in milestones)return
        val lang=prefs(ctx).getString("app_language","en")?:"en";val (title,body)=milestoneContent(lang,streak)
        pushInbox(ctx,title,body)
        if(prefs(ctx).getBoolean("notif_enabled",false)&&prefs(ctx).getBoolean("notif_streak",true))show(ctx,MILESTONE_ID,title,body)
    }
    fun preview(ctx:Context){
        val lang=prefs(ctx).getString("app_language","en")?:"en"
        val time="%02d:%02d".format(prefs(ctx).getInt("notif_hour",19),prefs(ctx).getInt("notif_minute",0))
        val title=when(lang){"fr"->"Aperçu des rappels Radix";"es"->"Vista previa de recordatorios Radix";else->"Radix reminder preview"}
        val body=when(lang){"fr"->"Une douce notification hors ligne, chaque jour à $time.";"es"->"Una notificación suave sin conexión cada día a las $time.";else->"One gentle offline nudge each day at $time."}
        show(ctx,TEST_ID,title,body)
    }
}
internal class ReminderReceiver:BroadcastReceiver(){override fun onReceive(ctx:Context,intent:Intent){StudyReminder.fireDaily(ctx)}}
internal class BootReceiver:BroadcastReceiver(){override fun onReceive(ctx:Context,intent:Intent){if(intent.action in listOf(Intent.ACTION_BOOT_COMPLETED,Intent.ACTION_MY_PACKAGE_REPLACED))StudyReminder.schedule(ctx)}}
