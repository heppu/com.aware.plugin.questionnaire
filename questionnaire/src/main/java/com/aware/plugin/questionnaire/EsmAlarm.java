package com.aware.plugin.questionnaire;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class EsmAlarm extends BroadcastReceiver {    
	
	@Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getExtras().getInt("id");
        Log.d("AWARE::Questionnaire", "Alarm onReceive id: "+id);
        Plugin.triggerQuestionnaire(context, id);
    }

	public void SetAlarm(Context context, long time, int id) {
        Log.d("AWARE::Questionnaire", "Alarm SetAlarm");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, EsmAlarm.class);
        i.putExtra("id", id);
		PendingIntent pi = PendingIntent.getBroadcast(context, (int) time, i, 0);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
	}

	public void CancelAlarm(Context context, long time) {
        Log.d("AWARE::Questionnaire", "Alarm CancelAlarm");
        Intent intent = new Intent(context, EsmAlarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, (int) time, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}