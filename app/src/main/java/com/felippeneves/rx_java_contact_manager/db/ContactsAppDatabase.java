package com.felippeneves.rx_java_contact_manager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.felippeneves.rx_java_contact_manager.db.entity.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactsAppDatabase extends RoomDatabase {
    public abstract ContactDAO getContactDAO();
}
