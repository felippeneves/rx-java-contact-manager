package com.felippeneves.rx_java_contact_manager.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.felippeneves.rx_java_contact_manager.db.entity.Contact;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface ContactDAO {

    @Insert
    long addContact(Contact contact);

    @Update
    void updateContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);

    @Query("" +
            "   select *        " +
            "   from contacts   ")
    Flowable<List<Contact>> getContacts();
}
