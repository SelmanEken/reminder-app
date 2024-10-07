package com.example.hatirlatici3.model;

import java.io.Serializable;
import java.util.List;

// Serializable arayüzü, nesnelerin serileştirilebilir olduğunu belirtir
public class ReminderUiModel implements Serializable {
    private List<Reminder> reminders; // Hatırlatıcı listesi
    private int icon; // Hatırlatıcı arayüz ikonu

    // Hatırlatıcı kullanıcı arayüzü modelini oluşturur
    public ReminderUiModel(List<Reminder> reminders, int icon) {
        this.reminders = reminders;
        this.icon = icon;
    }

    // Hatırlatıcı listesi getter metodu
    public List<Reminder> getReminders() {
        return reminders;
    }

    // Hatırlatıcı listesi setter metodu
    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    // Hatırlatıcı arayüz ikonu getter metodu
    public int getIcon() {
        return icon;
    }

    // Hatırlatıcı arayüz ikonu setter metodu
    public void setIcon(int icon) {
        this.icon = icon;
    }
}
