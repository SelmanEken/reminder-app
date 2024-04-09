package com.example.hatirlatici3.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.hatirlatici3.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminder_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "reminders";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE_TIME_MILLIS = "date_time_millis";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DATE_TIME_MILLIS + " INTEGER)";

    public DatabaseHelper(Context context) {
        super((Context) context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Veritabanı tablosunu oluştur
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eski tabloyu sil ve yeni bir tablo oluştur
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Hatırlatıcı eklemek için
    public long addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_DESCRIPTION, reminder.getDescription());
        values.put(COLUMN_DATE_TIME_MILLIS, reminder.getDateTimeMillis());
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Tüm hatırlatıcıları getirmek için
    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                long dateTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME_MILLIS));
                Reminder reminder = new Reminder((int) id, title, description, dateTimeMillis);
                reminder.setId((int) id);
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return reminderList;
    }

    // Hatırlatıcıyı güncellemek için
    public void updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, reminder.getTitle());
        values.put(COLUMN_DESCRIPTION, reminder.getDescription());
        values.put(COLUMN_DATE_TIME_MILLIS, reminder.getDateTimeMillis());
        int rowsAffected = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(reminder.getId())});

        if (rowsAffected > 0) {
            Log.d("UpdateReminder", "Güncellenen satır sayısı: " + rowsAffected);
        } else {
            Log.d("UpdateReminder", "Hiç satır güncellenmedi");
        }

        db.close();
    }

    // Hatırlatıcıyı silmek için
    public void deleteReminder(long reminderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(reminderId)});
        db.close();
    }

    // Hatırlatıcıyı id'ye göre getiren metod
    public Reminder getReminder(long reminderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(reminderId)});
        Reminder reminder = null;
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            long dateTimeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME_MILLIS));
            reminder = new Reminder((int) reminderId, title, description, dateTimeMillis);
            reminder.setId((int) reminderId);
        }
        cursor.close();
        db.close();
        return reminder;
    }
}
