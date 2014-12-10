package com.aware.plugin.questionaire;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;


public class EsmAlarm extends BroadcastReceiver {    
	
	@Override
    public void onReceive(Context context, Intent intent) {
        Plugin.triggerQuestionnaire(context, 0);
    }

	public void SetAlarm(Context context, long time, int id) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, EsmAlarm.class);
        i.putExtra("id", id);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
	}

	public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, EsmAlarm.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}