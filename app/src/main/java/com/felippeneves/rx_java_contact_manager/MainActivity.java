package com.felippeneves.rx_java_contact_manager;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felippeneves.rx_java_contact_manager.adapter.ContactsAdapter;
import com.felippeneves.rx_java_contact_manager.db.ContactDAO;
import com.felippeneves.rx_java_contact_manager.db.ContactsAppDatabase;
import com.felippeneves.rx_java_contact_manager.db.entity.Contact;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Context context = MainActivity.this;

    private ContactDAO contactDAO;

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContactsAppDatabase contactsAppDatabase;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Contacts Manager ");

        recyclerView = findViewById(R.id.recycler_view_contacts);
        contactsAppDatabase = Room.databaseBuilder(context, ContactsAppDatabase.class, "ContactDB").build();

        contactDAO = contactsAppDatabase.getContactDAO();

        contactsAdapter = new ContactsAdapter(context, contactArrayList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        disposable.add(
                contactDAO.getContacts()
                        .subscribeOn(Schedulers.computation()) // Run DB fetch in the background
                        .observeOn(AndroidSchedulers.mainThread()) // Switch back to UI thread
                        .subscribe(contacts -> {
                            contactArrayList.clear();
                            contactArrayList.addAll(contacts);
                            contactsAdapter.notifyDataSetChanged();
                        }, throwable -> Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show())
        );

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> addAndEditContacts(false, null, -1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void addAndEditContacts(final boolean isUpdate, final Contact contact, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.layout_add_contact, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        TextView contactTitle = view.findViewById(R.id.new_contact_title);
        final EditText newContact = view.findViewById(R.id.name);
        final EditText contactEmail = view.findViewById(R.id.email);

        contactTitle.setText(!isUpdate ? "Add New Contact" : "Edit Contact");

        if (isUpdate && contact != null) {
            newContact.setText(contact.getName());
            contactEmail.setText(contact.getEmail());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(isUpdate ? "Update" : "Save", (dialogBox, id) -> {
                })
                .setNegativeButton("Delete",
                        (dialogBox, id) -> {
                            if (isUpdate) {
                                deleteContact(contact);
                            } else {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            if (TextUtils.isEmpty(newContact.getText().toString())) {
                Toast.makeText(MainActivity.this, "Enter contact name!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                alertDialog.dismiss();
            }

            if (isUpdate && contact != null) {

                updateContact(newContact.getText().toString(), contactEmail.getText().toString(), position);
            } else {

                createContact(newContact.getText().toString(), contactEmail.getText().toString());
            }
        });
    }

    private void deleteContact(Contact contact) {
        disposable.add(
                Completable.fromAction(() -> contactDAO.deleteContact(contact))
                        .subscribeOn(Schedulers.io()) // Perform DB deletion in the background
                        .observeOn(AndroidSchedulers.mainThread()) // Switch back to UI thread
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, "Contact deleted successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void updateContact(String name, String email, int position) {
        Contact contact = contactArrayList.get(position);
        contact.setName(name);
        contact.setEmail(email);

        disposable.add(
                Completable.fromAction(() -> contactDAO.updateContact(contact))
                        .subscribeOn(Schedulers.io()) // Perform DB deletion in the background
                        .observeOn(AndroidSchedulers.mainThread()) // Switch back to UI thread
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, "Contact updated successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void createContact(String name, String email) {
        disposable.add(
                Completable.fromAction(() -> contactDAO.addContact(new Contact(name, email)))
                        .subscribeOn(Schedulers.io()) // Perform DB deletion in the background
                        .observeOn(AndroidSchedulers.mainThread()) // Switch back to UI thread
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, "Contact added successfully!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }
}
