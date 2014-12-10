package com.aware.plugin.questionaire;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;

import org.json.*;

import com.aware.Aware;
import com.aware.ESM;
import com.aware.Light;
import com.aware.Aware_Preferences;
import com.aware.plugin.questionaire.Provider.Questionaire_Data;
import com.aware.utils.Aware_Plugin;

import java.lang.reflect.Type;
import java.util.List;

public class Plugin extends Aware_Plugin {

    public static final String ACTION_AWARE_PLUGIN_QUESTIONNAIRE = "ACTION_AWARE_PLUGIN_QUESTIONNAIRE";

    public EsmAlarm alarm = new EsmAlarm();

    public static ContextProducer context_producer;

    private EsmReceiver esmReceiver = new EsmReceiver();

    private String json = "[{\"id\":\"1\",\"general\":{\"startDate\":1416700800,\"endDate\":1417219200,\"startTime\":43200,\"endTime\":64800,\"weekDays\":[false,true,true,true,false,false,false],\"numberOfESM\":\"5\",\"interval\":\"spaced\"},\"trigger\":{\"operand\":\"AND\",\"triggers\":[{\"type\":\"alarm\",\"data\":{\"timestamp\":1416826800000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416831120000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416835440000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416839760000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416844080000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416913200000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416917520000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416921840000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416926160000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416930480000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416999600000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417003920000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417008240000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417012560000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417016880000}}]},\"questions\":[{\"id\":1,\"type\":\"esm\",\"data\":{\"esm\":{\"esm\":[{\"esm_type\":\"2\",\"esm_title\":\"First\",\"esm_instructions\":\"\",\"esm_radios\":[\"Yes\",\"No\",\"Maybe\"],\"esm_submit\":\"Submit\",\"esm_expiration_threashold\":\"0\",\"esm_trigger\":\"esm-questionnaire\"}]},\"conditions\":{\"Yes\":\"2\",\"No\":\"3\",\"Maybe\":\"3\"}}},{\"id\":2,\"type\":\"esm\",\"data\":{\"esm\":{\"esm\":[{\"esm_type\":\"5\",\"esm_title\":\"Seconds\",\"esm_instructions\":\"\",\"esm_quick_answers\":[\"What\",\"ever\"],\"esm_expiration_threashold\":\"0\",\"esm_trigger\":\"esm-questionnaire\"}]},\"conditions\":{\"What\":\"3\",\"ever\":\"false\"}}},{\"id\":3,\"type\":\"esm\",\"data\":{\"esm\":{\"esm\":[{\"esm_type\":\"1\",\"esm_title\":\"Third\",\"esm_instructions\":\"\",\"esm_submit\":\"Submit\",\"esm_expiration_threashold\":\"0\",\"esm_trigger\":\"esm-questionnaire\"}]},\"conditions\":{}}}]}]";


    public class EsmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

    @Override
    public void onCreate() {
        Log.d("asd", "onCreate");
        super.onCreate();
        TAG = "AWARE::Lux Meter";

        Intent aware = new Intent(this, Aware.class);
        startService(aware);

        Aware.setSetting(getApplicationContext(), Settings.STATUS_PLUGIN_QUESTIONNAIRE, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, true);

        Intent apply = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(apply);

        context_producer = new ContextProducer() {
            @Override
            public void onContext() {
                Intent context_questionaire = new Intent();
                context_questionaire.setAction(ACTION_AWARE_PLUGIN_QUESTIONNAIRE);
                sendBroadcast(context_questionaire);
            }
        };

        DATABASE_TABLES = Provider.DATABASE_TABLES;
        TABLES_FIELDS = Provider.TABLES_FIELDS;
        CONTEXT_URIS = new Uri[]{ Questionaire_Data.CONTENT_URI };

        IntentFilter lightFilter = new IntentFilter();
        lightFilter.addAction(Light.ACTION_AWARE_LIGHT);
        registerReceiver(esmReceiver, lightFilter);

        jsonParser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("asd", "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("asd", "onDestroy");
        super.onDestroy();
        alarm.CancelAlarm(Plugin.this);

        Aware.setSetting(getApplicationContext(), Settings.STATUS_PLUGIN_QUESTIONNAIRE, false);

        unregisterReceiver(esmReceiver);

        Intent apply = new Intent(Aware.ACTION_AWARE_REFRESH);
        sendBroadcast(apply);
    }

    protected static void triggerQuestionnaire(Context context, int id) {
        String json = "[{\"id\":\"1\",\"general\":{\"startDate\":1416700800,\"endDate\":1417219200,\"startTime\":43200,\"endTime\":64800,\"weekDays\":[false,true,true,true,false,false,false],\"numberOfESM\":\"5\",\"interval\":\"spaced\"},\"trigger\":{\"operand\":\"AND\",\"triggers\":[{\"type\":\"alarm\",\"data\":{\"timestamp\":1416826800000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416831120000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416835440000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416839760000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416844080000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416913200000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416917520000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416921840000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416926160000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416930480000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1416999600000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417003920000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417008240000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417012560000}},{\"type\":\"alarm\",\"data\":{\"timestamp\":1417016880000}}]},\"questions\":[{\"id\":1,\"type\":\"esm\",\"data\":{\"esm\":{\"esm\":[{\"esm_type\":\"2\",\"esm_title\":\"First\",\"esm_instructions\":\"\",\"esm_radios\":[\"Yes\",\"No\",\"Maybe\"],\"esm_submit\":\"Submit\",\"esm_expiration_threashold\":\"0\",\"esm_trigger\":\"esm-questionnaire\"}]},\"conditions\":{\"Yes\":\"2\",\"No\":\"3\",\"Maybe\":\"3\"}}},{\"id\":2,\"type\":\"esm\",\"data\":{\"esm\":{\"esm\":[{\"esm_type\":\"5\",\"esm_title\":\"Seconds\",\"esm_instructions\":\"\",\"esm_quick_answers\":[\"What\",\"ever\"],\"esm_expiration_threashold\":\"0\",\"esm_trigger\":\"esm-questionnaire\"}]},\"conditions\":{\"What\":\"3\",\"ever\":\"false\"}}},{\"id\":3,\"type\":\"esm\",\"data\":{\"esm\":{\"esm\":[{\"esm_type\":\"1\",\"esm_title\":\"Third\",\"esm_instructions\":\"\",\"esm_submit\":\"Submit\",\"esm_expiration_threashold\":\"0\",\"esm_trigger\":\"esm-questionnaire\"}]},\"conditions\":{}}}]}]";
        JSONArray arr = null;

        try {
            arr = new JSONArray(json);
            String esm = "[{\"esm\": " + arr.getJSONObject(0).getJSONArray("questions").getJSONObject(0).getJSONObject("data").getJSONObject("esm").getJSONArray("esm").getJSONObject(0).toString() + "}]";
            Intent queue_esm = new Intent(ESM.ACTION_AWARE_QUEUE_ESM);
            queue_esm.putExtra(ESM.EXTRA_ESM, esm);
            context.sendBroadcast(queue_esm);
            Log.d("asd data: ", esm);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean jsonParser() {
        JSONArray arr = null;
        long timeNow = System.currentTimeMillis();
        Log.d("asd now: ", String.valueOf(timeNow));

        try {
            arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++){
                JSONArray triggers = arr.getJSONObject(i).getJSONObject("trigger").getJSONArray("triggers");
                //Debug alarm
                alarm.SetAlarm(Plugin.this, timeNow+1000, i);

                for (int j = 0; j < triggers.length(); j++) {
                    long time = triggers.getJSONObject(j).getJSONObject("data").getLong("timestamp");
                    if(time>timeNow) {

                        //alarm.SetAlarm(Plugin.this, time, i);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
