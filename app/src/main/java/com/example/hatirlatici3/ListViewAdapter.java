package com.example.hatirlatici3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hatirlatici3.model.Reminder;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Reminder> {

    private Context context;
    private List<Reminder> reminderList;

    public ListViewAdapter(Context context, List<Reminder> reminderList) {
        super(context, R.layout.list_item, reminderList);
        this.context = context;
        this.reminderList = reminderList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Hatırlatıcı öğesinin görünümünü oluştur
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item, null);
        }

        // Hatırlatıcı verisini al
        Reminder reminder = getItem(position);

        // Gerekli görünümleri bul
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        TextView dateTimeTextView = view.findViewById(R.id.dateTimeTextView);

        // Görünümleri güncelle
        titleTextView.setText(reminder.getTitle());
        descriptionTextView.setText(reminder.getDescription());

        return view;
    }
}
