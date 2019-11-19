package com.example.faridomar.messengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ContactActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = "ContactActivity";

private RecyclerView contactsRecyclerView;
private ContactAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /***/
        /***/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


contactsRecyclerView = (RecyclerView) findViewById(R.id.contact_list_recycler_view);
contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

CModel model = CModel.get(getBaseContext());
List<Contact> contacts = model.getContacts();

adapter = new ContactAdapter(contacts);
contactsRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        G.appContext=ContactActivity.this;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            G.sendFriendRequest();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

/***/private class ContactHolder extends RecyclerView.ViewHolder {
    private TextView txtcontact;
    private Contact contacts;

    public ContactHolder(View itemView) {
        super(itemView);

        txtcontact = (TextView) itemView.findViewById(R.id.contact_jid);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inside here we start the chat activity
                Intent intent = new Intent(ContactActivity.this
                        , ChatActivity.class);
                intent.putExtra("EXTRA_CONTACT_JID", contacts.getJid());
                startActivity(intent);


            }
        });
    }


    public void bind(Contact contact) {
        contacts = contact;
        if (contacts == null) {
            Log.d(TAG, "null Contact");
            return;
        }
        txtcontact.setText(contacts.getJid());

    }
}


private class ContactAdapter extends RecyclerView.Adapter<ContactHolder> {
    private List<Contact> contacts;

    public ContactAdapter(List<Contact> contactList) {
        contacts = contactList;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater
                .inflate(R.layout.contact_list, parent,
                        false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.bind(contact);

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}

}