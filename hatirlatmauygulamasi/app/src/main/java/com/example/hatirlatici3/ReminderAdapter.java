package com.example.hatirlatici3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hatirlatici3.helper.DatabaseHelper;
import com.example.hatirlatici3.helper.OnItemClickListener;
import com.example.hatirlatici3.helper.SharedPreferencesManager;
import com.example.hatirlatici3.model.Reminder;
import com.example.hatirlatici3.model.ReminderUiModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> implements OnItemClickListener {

    private ReminderUiModel reminders;

    private OnItemClickListener clickListener;
    private Context context;

    public ReminderAdapter(@Nullable Context context, ReminderUiModel reminderList, @Nullable OnItemClickListener listener) {
        // ReminderAdapter oluşturulurken gerekli parametreler alınıyor
        this.context = context;
        this.reminders = reminderList;
        this.clickListener = listener;
    }

    @Override
    public void onItemClick(int position) {
        // RecyclerView elemanlarına tıklandığında çağrılan metod
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // RecyclerView elemanlarının görünümünü temsil eden sınıf
        public ImageView icon;
        public TextView titleTextView;
        public TextView descriptionTextView;
        public TextView dateTimeTextView;
        public LinearLayout layoutItemText;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            layoutItemText = itemView.findViewById(R.id.layout_text);
            icon = itemView.findViewById(R.id.iv_model_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // tıklama işleminde çağrılan metod
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(position);
            }
        }
    }


    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Yeni bir ReminderViewHolder örneği oluşturuluyor ve bu örnek belirtilen layout ile ilişkilendiriliyor
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        // RecyclerViewdeki belirli bir konumdaki verilerin görüntülenmesini sağlayan metod
        Reminder reminder = reminders.getReminders().get(position);

        // ReminderViewHolderın bileşenleri hatırlatıcı özelliklerine göre güncelleniyor
        holder.titleTextView.setText(reminder.getTitle());
        holder.descriptionTextView.setText(reminder.getDescription());
        holder.icon.setImageResource(reminders.getIcon());
        String formattedDateTime = getFormattedDateTime(reminder.getDateTime());
        holder.dateTimeTextView.setText("Görev Saati: " + formattedDateTime);
        SharedPreferencesManager.saveReminderModel(context, reminder);

        // Silme modu açıkken her hatırlatıcı elemanının ikonuna tıklandığında silme işlemi gerçekleşecek
        boolean isDeleteModeOn = SharedPreferencesManager.getIconState(context);
        if (isDeleteModeOn) {
            holder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Sil butonuna tıklandığında yapılacak işlemler
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Silinecek hatırlatıcıyı al
                        Reminder reminderToDelete = reminders.getReminders().get(position);
                        // Hatırlatıcıyı veritabanından ve listeden sil
                        reminders.getReminders().remove(position);
                        notifyItemRemoved(position);
                        DatabaseHelper dbHelper = new DatabaseHelper(context);
                        dbHelper.deleteReminder(reminderToDelete.getId());
                        cancelAlarm(reminderToDelete.getId());
                    }
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        // Veri kümesindeki hatırlatıcı öğelerinin sayısını döndürme
        return reminders.getReminders().size();
    }

    private void cancelAlarm(int reminderId) {
        // Belirli bir hatırlatıcı için var olan bir alarmı iptal etme
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reminderId, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private String getFormattedDateTime(long dateTimeMillis) {
        // Verilen tarih ve saat bilgisini belirli bir formatta geri döndürme
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        Date date = new Date(dateTimeMillis);
        return sdf.format(date);
    }

    public void updateItem(int position, Reminder updatedModel) {
        // Belirli bir konumdaki hatırlatıcıyı güncelleme
        reminders.getReminders().set(position, updatedModel);
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.updateReminder(updatedModel);
        notifyItemChanged(position);
    }
}

