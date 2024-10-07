package com.example.hatirlatici3.model;

import java.io.Serializable;

// Serializable arayüzü, nesnelerin serileştirilebilir olduğunu belirtir
public class Reminder implements Serializable {
    private int id; // Hatırlatıcı ID'si
    private String title; // Hatırlatıcı başlığı
    private String description; // Hatırlatıcı açıklaması
    private long dateTimeMillis; // Hatırlatıcı tarih ve saat bilgisi

    // Hatırlatıcı nesnesini oluşturur
    public Reminder(int id, String title, String description, long dateTimeMillis) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTimeMillis = dateTimeMillis;
    }

    // ID getter metodu
    public int getId() {
        return (int) id;
    }

    // ID setter metodu
    public void setId(int id) {
        this.id = id;
    }

    // Başlık getter metodu
    public String getTitle() {
        return title;
    }

    // Başlık setter metodu
    public void setTitle(String title) {
        this.title = title;
    }

    // Açıklama getter metodu
    public String getDescription() {
        return description;
    }

    // Açıklama setter metodu
    public void setDescription(String description) {
        this.description = description;
    }
    // Hatırlatıcı tarih ve saat bilgisini getiren getter metot
    public long getDateTime() {
        return dateTimeMillis;
    }

    // Tarih ve saat bilgisi getter metodu
    public long getDateTimeMillis() {
        return dateTimeMillis;
    }

    // Tarih ve saat bilgisi setter metodu
    public void setDateTimeMillis(long dateTimeMillis) {
        this.dateTimeMillis = dateTimeMillis;
    }
}
