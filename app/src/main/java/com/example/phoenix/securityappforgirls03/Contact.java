package com.example.phoenix.securityappforgirls03;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "table01")
public class Contact {
    @PrimaryKey
    @NonNull
    public int id;
    public String contact;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact  ;
    }
}
