package com.example.hatirlatici3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hatirlatici3.databinding.ActivityMainBinding;
import com.example.hatirlatici3.helper.DatabaseHelper;
import com.example.hatirlatici3.helper.OnItemClickListener;
import com.example.hatirlatici3.helper.SharedPreferencesManager;
import com.example.hatirlatici3.model.Reminder;
import com.example.hatirlatici3.model.ReminderUiModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BroadcastReceiver updateReceiver;
    private ReminderAdapter adapter;

    private static final int PICK_MP3_REQUEST_CODE = 123;
    ActivityMainBinding binding;
    DatabaseHelper dbHelper;
    List<Reminder> reminderList;
    boolean isDeleteModeOn = false;
    private OnItemClickListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Activity'nin oluşturulduğu ana metot
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbarApp.toolbarTitle.setText(getString(R.string.anasayfa_title));
        binding.toolbarApp.ivDeleteButton.setVisibility(View.VISIBLE);
        setOnClickListeners();

        dbHelper = new DatabaseHelper(this);
        reminderList = dbHelper.getAllReminders();
        // Hatırlatıcı listesi boşsa gerekli görüntülemeler
        if (reminderList.size() == 0) {
            binding.tvEmptyList.setVisibility(View.VISIBLE);
            binding.remindersRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            binding.tvEmptyList.setVisibility(View.INVISIBLE);
            binding.remindersRecyclerView.setVisibility(View.VISIBLE);
            binding.remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        binding.addReminderButton.setOnClickListener(view -> {
            // Kullanıcıya hatırlatıcı eklemek için yeni aktiviteye geçiş
            Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
            intent.putExtra("isFromList", false);
            startActivity(intent);
            finish();
        });

        // BroadcastReceiver'ı oluştur
        updateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Hatırlatıcı listesini güncelle
                updateReminderList(R.drawable.icon_heart);
            }
        };

        // BroadcastReceiver'ı kaydet
        registerReceiver(updateReceiver, new IntentFilter("UPDATE_REMINDER_LIST"));

        // Hatırlatıcı listesini güncelle
        updateReminderList(R.drawable.icon_heart);
    }

    private void setOnClickListeners() {
        // Silme modu butonuna tıklanıldığında çağrılan metot
        binding.toolbarApp.ivDeleteButton.setOnClickListener(v -> {
            isDeleteModeOn = !isDeleteModeOn;
            updateReminderIcon();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // BroadcastReceiver'ı kayıttan kaldır
        unregisterReceiver(updateReceiver);
    }

    private void updateReminderIcon() {
        // Silme modu açıkken, ikon güncellemesi yapılır ve durumu SharedPreferences'e kaydedilir
        if (isDeleteModeOn) {
            updateReminderList(R.drawable.icon_delete);
            SharedPreferencesManager.saveIconState(this, true);
        } else {
            // Silme modu kapalıyken, ikon güncellemesi yapılır ve durumu SharedPreferences'e kaydedilir
            updateReminderList(R.drawable.icon_heart);
            SharedPreferencesManager.saveIconState(this, false);
        }
    }

    private void updateReminderList(int iconRes) {
        // Hatırlatıcı listesi güncellenir ve yeni ikon ile bir ReminderUiModel oluşturulup SharedPreferences'e kaydedilir
        reminderList = dbHelper.getAllReminders();
        ReminderUiModel reminderUiModel = new ReminderUiModel(reminderList, iconRes);
        SharedPreferencesManager.saveReminderUiModel(getApplicationContext(), reminderUiModel);

        // Hatırlatıcıları listeleyen adapter güncellenir ve tıklama olayları izlenir
        adapter = new ReminderAdapter(this, reminderUiModel, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (isDeleteModeOn) {
                    // Silme modu açıkken, hatırlatıcı düzenleme ekranına geçiş yapılır.
                    Log.d("test", "test");
                    Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("isFromList", true); // Model intent ile geçirilir
                    startActivity(intent);
                    finish();
                }
            }
        });
        binding.remindersRecyclerView.setAdapter(adapter);
    }


    private void updateMainActivityReminderList() {
        // MainActivity'deki hatırlatıcı listesini güncelle
        Intent updateIntent = new Intent("UPDATE_REMINDER_LIST");
        sendBroadcast(updateIntent);
    }
}