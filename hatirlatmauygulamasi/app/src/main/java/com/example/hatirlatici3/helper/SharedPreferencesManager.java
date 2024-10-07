package com.example.hatirlatici3.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.hatirlatici3.model.Reminder;
import com.example.hatirlatici3.model.ReminderUiModel;
import com.google.gson.Gson;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_REMINDER_UI_MODEL = "reminder_ui_model";
    private static final String KEY_REMINDER_MODEL = "reminder_model";
    private static final String KEY_REMINDER_ICON = "reminder_icon";

    // Hatırlatıcı kullanıcı arayüzü modelini kaydet
    public static void saveReminderUiModel(Context context, ReminderUiModel reminderUiModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(reminderUiModel);
        editor.putString(KEY_REMINDER_UI_MODEL, json);
        editor.apply();
    }

    // Hatırlatıcı kullanıcı arayüzü modelini al
    public static ReminderUiModel getReminderUiModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_REMINDER_UI_MODEL, null);

        return gson.fromJson(json, ReminderUiModel.class);
    }

    // Hatırlatıcı modelini kaydet
    public static void saveReminderModel(Context context, Reminder reminderModel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(reminderModel);
        editor.putString(KEY_REMINDER_MODEL, json);
        editor.apply();
    }

    // Hatırlatıcı modelini al
    public static Reminder getReminderModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_REMINDER_MODEL, null);

        return gson.fromJson(json, Reminder.class);
    }

    // İkon durumunu kaydet
    public static void saveIconState(Context context, boolean isDeleteModeOn) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(KEY_REMINDER_ICON, isDeleteModeOn);
        editor.apply();
    }

    // İkon durumunu al
    public static boolean getIconState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_REMINDER_ICON, false);
    }
}
