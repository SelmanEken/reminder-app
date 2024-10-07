package com.example.hatirlatici3;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupMenu;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hatirlatici3.databinding.ActivityReminderAddBinding;
import com.example.hatirlatici3.helper.DatabaseHelper;
import com.example.hatirlatici3.helper.SharedPreferencesManager;
import com.example.hatirlatici3.model.Reminder;
import com.example.hatirlatici3.model.ReminderUiModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;

public class AddReminderActivity extends AppCompatActivity {
    ActivityReminderAddBinding binding; // Ekran bileşenlerini içeren bağlam
    String selectedReminderTime; // Seçilen hatırlatıcı zamanı
    InputStream alarmSound; // Alarm için seçilen ses
    SharedPreferences preferences; // Uygulama tercihleri
    int year, month, dayOfMonth, hourOfDay, minute; // Tarih ve saat değerleri
    int myear, mmonth, mdayOfMonth, mhourOfDay, mminute; // Seçilen hatırlatıcı tarih ve saat değerleri
    String title, description; // Hatırlatıcı başlığı ve açıklaması
    boolean isDateSelected = false;
    boolean isTimeSelected = false;
    boolean isSoundSelected = false;
    boolean isReminderSelected = false;
    int position;
    List<Reminder> reminderList; // Hatırlatıcı listesi
    ReminderAdapter adapter; // Hatırlatıcı listesi adaptörü
    boolean isFromList; // Hatırlatıcı listesinden mi geliyor kontrolü
    Reminder reminderAtIndex; // Listeden seçilen hatırlatıcı
    Calendar calendar; // Takvim nesnesi
    TimePicker timePicker; // Saat seçim bileşeni
    int alarmReminderTime; // Hatırlatıcı süresi
    String alarmSoundSource; // Seçilen sesin kaynağı
    DatabaseHelper dbHelper; // Veritabanı yardımcı sınıfı
    AlarmManager alarmManager; // Alarm yöneticisi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderAddBinding.inflate(getLayoutInflater()); // Bağlamı oluştur
        setContentView(binding.getRoot()); // Ekranı bağlamla ayarla
        preferences = getSharedPreferences("app_shared_prefs", MODE_PRIVATE); // Tercihleri al
        binding.toolbarApp.toolbarTitle.setText(getString(R.string.yeni_gorev)); // Araç çubuğuna başlık ekle
        DatabaseHelper dbHelper = new DatabaseHelper(this); // Veritabanı yardımcı sınıfını oluştur
        reminderList = dbHelper.getAllReminders(); // Tüm hatırlatıcıları al
        ReminderUiModel reminderUiModel = new ReminderUiModel(reminderList, R.drawable.icon_heart); // Hatırlatıcı kullanıcı arayüz modelini oluştur
        SharedPreferencesManager.saveReminderUiModel(getApplicationContext(), reminderUiModel); // Hatırlatıcı kullanıcı arayüz modelini kaydet
        adapter = new ReminderAdapter(null, reminderUiModel, null); // Hatırlatıcı adaptörünü oluştur
        getIntentFromActivity(); // Aktiviteden gelen verileri işle
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); // Alarm yöneticisini al
        if (isFromList) {
            setExistingValues(reminderAtIndex); // Varolan değerleri ayarla
        }
        setTextWatchers(); // Metin değişiklik takipçilerini ayarla
        setOnClickListeners(); // Tıklama olaylarını ayarla
    }



    private void getIntentFromActivity() {
        Intent intent = getIntent(); // Intent'i al
        isFromList = intent.getBooleanExtra("isFromList", false); // Listeden mi geliyor kontrol et
        if (isFromList) {
            position = intent.getIntExtra("position", 0); // Pozisyonu al
            reminderAtIndex = reminderList.get(position); // Listeden seçilen hatırlatıcıyı al
        }
    }

    private void setExistingValues(Reminder reminder) {
        binding.editTextReminderTitle.etReminderTitle.setText(reminder.getTitle()); // Başlığı ayarla
        binding.editTextReminderDescription.etReminderDescription.setText(reminder.getDescription()); // Açıklamayı ayarla
        getYearAndMonthFromMillis(reminder.getDateTimeMillis()); // Zaman bilgilerini al
        setDateTextField(myear, mmonth, mdayOfMonth); // Tarih alanını ayarla
        setTimeTextField(mhourOfDay, mminute); // Saat alanını ayarla
    }

    public void setTextWatchers() {
        binding.editTextReminderTitle.etReminderTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Önceki metin değişiklikleri
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Metin değişikliği
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkFieldsNotEmpty(); // boşlukları kontrol et
            }
        });
    }

    private void setOnClickListeners() {
        binding.layoutReminder.layoutReminderField.setOnClickListener(this::openTimeOptionsMenu); // Hatırlatıcı zaman menüsünü açan tıklama olayı
        binding.layoutDatePicker.layoutDate.setOnClickListener(v -> displayDatePicker()); // Tarih seçim ekranını açan tıklama olayı
        binding.layoutTimePicker.layoutTime.setOnClickListener(v -> displayTimePicker()); // Saat seçim ekranını açan tıklama olayı
        binding.layoutSoundMenu.layoutSound.setOnClickListener(this::displaySoundMenu); // Ses menüsünü açan tıklama olayı
        binding.btnSave.setOnClickListener(v -> saveCalendarValues()); // Takvime kaydetme işlemini başlatan tıklama olayı
    }



    private void checkFieldsNotEmpty() {
        title = binding.editTextReminderTitle.etReminderTitle.getText().toString(); // Başlığı al
        binding.btnSave.setEnabled(!title.isEmpty() && isDateSelected && isTimeSelected && isReminderSelected && isSoundSelected); // Boş olmayan alanları kontrol et ve Kaydet düğmesini etkinleştir veya devre dışı bırak
        if (binding.btnSave.isEnabled()) {
            binding.btnSave.setBackgroundColor(ContextCompat.getColor(this, R.color.black)); // Eğer etkinse düğmenin arkaplan rengini siyah olarak ayarla
        } else {
            binding.btnSave.setBackgroundColor(ContextCompat.getColor(this, R.color.inactive)); // Eğer devre dışı bırakılmışsa düğmenin arkaplan rengini gri olarak ayarla
        }
    }


    private void saveCalendarValues() {
        // Hatırlatıcıyı ayarlamak için gerekli işlemleri yap
        description = binding.editTextReminderDescription.etReminderDescription.getText().toString();

        // Seçilen tarih ve saat bilgisini millisaniye cinsine çevir
        long dateTimeMillis = getDateTimeInMillis(year, month, dayOfMonth, hourOfDay, minute);

        Log.e("Alarm Time", "Milliseconds: " + dateTimeMillis);

        // Hatırlatıcıyı veritabanına ekleme ve bildirim ayarlama işlemleri
        dbHelper = new DatabaseHelper(AddReminderActivity.this);
        if (isFromList) {
            Reminder reminder = new Reminder(reminderAtIndex.getId(), title, description, dateTimeMillis
            );
            dbHelper.updateReminder(reminder);
            long reminderId = dbHelper.addReminder(reminder);
            // Alarmı kur
            setAlarm(reminderId, dateTimeMillis, alarmReminderTime);
        } else {
            addReminderDatabase(title, description, dateTimeMillis);
        }
        navigateToMainActivity();
    }

    private void addReminderDatabase(String title, String description, long dateTimeMillis) {
        Reminder reminder = new Reminder(0, title, description, dateTimeMillis
        );
        long reminderId = dbHelper.addReminder(reminder);
        // Alarmı kur
        setAlarm(reminderId, dateTimeMillis, alarmReminderTime);
    }

    private void navigateToMainActivity() {
        // MainActivity'ye geri dön
        Intent intent = new Intent(AddReminderActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void displayDatePicker() {
        // Tarih seçiciyi açan metod
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_date_picker);

        // Tarih seçici ve onay butonu tanımlama
        DatePicker datePicker = dialog.findViewById(R.id.datePicker);
        Button okButton = dialog.findViewById(R.id.ok_button);

        // Onay butonu tıklanınca
        okButton.setOnClickListener(v -> {
            year = datePicker.getYear();
            month = datePicker.getMonth();
            dayOfMonth = datePicker.getDayOfMonth();
            setDateTextField(year, month, dayOfMonth); // Seçilen tarihi görüntüleme alanına ayarla
            checkFieldsNotEmpty();
            dialog.dismiss();
        });

        dialog.show(); // Dialog penceresini göster
    }

    private void setDateTextField(int year, int month, int dayOfMonth) {
        // Görüntüleme alanına seçilen tarihi ayarla
        month += 1;
        String selectedDate = year + "-" + month + "-" + dayOfMonth;
        binding.layoutDatePicker.tvDate.setText(selectedDate);
        isDateSelected = true;
    }


    private void displayTimePicker() {
        // Saat seçiciyi açan metod
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_hour_picker);

        // Saat seçici ve onay butonu tanımlama
        timePicker = dialog.findViewById(R.id.timePicker);
        Button okButton = dialog.findViewById(R.id.ok_button);

        // Onay butonu tıklanınca
        okButton.setOnClickListener(v -> {
            minute = timePicker.getMinute();
            hourOfDay = timePicker.getHour();
            setTimeTextField(hourOfDay, minute); // Seçilen saati görüntüleme alanına ayarla
            checkFieldsNotEmpty();
            dialog.dismiss();
        });

        dialog.show(); // Dialog penceresini göster
    }

    private void setTimeTextField(int hourOfDay, int minute) {
        // Görüntüleme alanına seçilen saati ayarla
        String selectedDate = hourOfDay + ":" + minute;
        binding.layoutTimePicker.tvTime.setText(selectedDate);
        isTimeSelected = true;
    }


    private void displaySoundMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.layout_sound_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sound_first:
                        // Ses 1 öğesine tıklanıldığında
                        alarmSound = getResources().openRawResource(R.raw.alarm_sound);
                        binding.layoutSoundMenu.tvSound.setText(getString(R.string.ses_1));
                        String inputStreamString = convertInputStreamToString(alarmSound);
                        alarmSoundSource = inputStreamString;
                        isSoundSelected = true;
                        checkFieldsNotEmpty();
                        return true;
                    case R.id.sound_second:
                        // Ses 2 öğesine tıklanıldığında
                        alarmSound = getResources().openRawResource(R.raw.alarm_sound2);
                        binding.layoutSoundMenu.tvSound.setText(getString(R.string.ses_2));
                        inputStreamString = convertInputStreamToString(alarmSound);
                        alarmSoundSource = inputStreamString;
                        isSoundSelected = true;
                        checkFieldsNotEmpty();
                        return true;
                    case R.id.sound_third:
                        // Ses 3 öğesine tıklanıldığında
                        alarmSound = getResources().openRawResource(R.raw.alarm_sound3);
                        binding.layoutSoundMenu.tvSound.setText(getString(R.string.ses_3));
                        inputStreamString = convertInputStreamToString(alarmSound);
                        alarmSoundSource = inputStreamString;
                        isSoundSelected = true;
                        checkFieldsNotEmpty();
                        return true;
                    case R.id.sound_fourth:
                        // Ses 4 öğesine tıklanıldığında
                        alarmSound = getResources().openRawResource(R.raw.alarm_sound4);
                        binding.layoutSoundMenu.tvSound.setText(getString(R.string.ses_4));
                        inputStreamString = convertInputStreamToString(alarmSound);
                        alarmSoundSource = inputStreamString;
                        isSoundSelected = true;
                        checkFieldsNotEmpty();
                        return true;
                    default:
                        // Diğer durumlarda
                        isSoundSelected = false;
                        checkFieldsNotEmpty();
                        return false;
                }
            }
        });

        popupMenu.show(); // Ses menüsünü göster
    }


    private String convertInputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            // Satır satır okuma işlemi gerçekleştir
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close(); // InputStream'i kapat
                bufferedReader.close(); // BufferedReader'ı kapat
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString(); // Dönüştürülen String'i geri döndür
    }

    private void openTimeOptionsMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.layout_reminder_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.min_10:
                    // 10 dakika öğesine tıklanıldığında
                    selectedReminderTime = getString(R.string.dakika_10);
                    binding.layoutReminder.tvReminderTime.setText(selectedReminderTime);
                    alarmReminderTime = 600000;
                    isReminderSelected = true;
                    checkFieldsNotEmpty();
                    return true;
                case R.id.min_20:
                    // 20 dakika öğesine tıklanıldığında
                    selectedReminderTime = getString(R.string.dakika_20);
                    binding.layoutReminder.tvReminderTime.setText(selectedReminderTime);
                    alarmReminderTime = 1200000;
                    isReminderSelected = true;
                    checkFieldsNotEmpty();
                    return true;
                case R.id.min_30:
                    // 30 dakika öğesine tıklanıldığında
                    selectedReminderTime = getString(R.string.dakika_30);
                    binding.layoutReminder.tvReminderTime.setText(selectedReminderTime);
                    alarmReminderTime = 1800000;
                    isReminderSelected = true;
                    checkFieldsNotEmpty();
                    return true;
                case R.id.min_40:
                    // 40 dakika öğesine tıklanıldığında
                    selectedReminderTime = getString(R.string.dakika_40);
                    binding.layoutReminder.tvReminderTime.setText(selectedReminderTime);
                    alarmReminderTime = 2400000;
                    isReminderSelected = true;
                    checkFieldsNotEmpty();
                    return true;
                default:
                    // Diğer durumlarda
                    isReminderSelected = false;
                    checkFieldsNotEmpty();
                    return false;
            }
        });

        popupMenu.show(); // Zaman menüsünü göster
    }

    private long getDateTimeInMillis(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        // Yeni bir takvim örneği oluşturuluyor.
        calendar = Calendar.getInstance();

        // Takvim örneği, belirtilen tarih ve saat bilgileriyle güncelleniyor.
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0); // Saniyeyi 0 olarak ayarla
        calendar.set(Calendar.MILLISECOND, 0); // Milisaniyeyi 0 olarak ayarla

        // Milisaniye cinsinden zaman değeri döndürülüyor.
        return calendar.getTimeInMillis();
    }

    private void getYearAndMonthFromMillis(long millis) {
        // Yeni bir takvim örneği oluşturuluyor ve milisaniye cinsinden zaman ile güncelleniyor.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        // Takvim örneğinden yıl, ay, gün, saat ve dakika bilgileri alınıyor.
        myear = calendar.get(Calendar.YEAR);
        mmonth = calendar.get(Calendar.MONTH);
        mdayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mhourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        mminute = calendar.get(Calendar.MINUTE);
    }

    private void setAlarm(long reminderId, long alarmTimeMillis, long delay) {

        long timeDifferenceMillis = alarmTimeMillis - delay;
        //şimdiki zamanı alıyoruz
        long currentTimeMillis = System.currentTimeMillis();

        //eğer push zamanı şimdiden önce ise kontrolü
        if (currentTimeMillis > timeDifferenceMillis) {
            // o zaman şimdiden 3 sn sonra push yollamak için değişken değeri değiştiriliyor
            timeDifferenceMillis = currentTimeMillis + 3000;
            // alarm ekranını da alarm zamanından şimdiki zaman arasındaki fark kadar ertelemek için delay setliyoruz
            delay = alarmTimeMillis - currentTimeMillis;
        }

        // AlarmManager ve Intent oluştur
        Log.e("Intent", "Intent olusturuldu");
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("reminderId", reminderId);  // Hatırlatıcı kimliğini intent'e ekleyin
        alarmIntent.putExtra("delay", delay);

        // Yeni bir yayın için PendingIntent oluştur
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) reminderId,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Android Sürümü kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android S sürümünde çalışıyorsa
            if (alarmManager.canScheduleExactAlarms()) {
                // Tam zamanlı alarmları destekleyip desteklemediğini kontrol eder.
                Log.e("alarmManager", "1");
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        timeDifferenceMillis,
                        pendingIntent
                );
            } else {
                // Tam zamanlı alarmları desteklemiyorsa standart bir alarm ayarlar.
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        timeDifferenceMillis,
                        pendingIntent
                );
            }
        } else {
            // Android S sürümünden önceki sürümlerde çalışıyorsa
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    timeDifferenceMillis,
                    pendingIntent
            );
        }
    }
}