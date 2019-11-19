package com.example.faridomar.messengerapp;

import android.content.Context;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Farid omar on 1/29/2017.
 */
public class CModel {

    private static CModel sCModel;
    private List<Contact> contacts;
    //  private Connection mConnection;

    public static CModel get(Context context) {
        if (sCModel == null) {
            sCModel = new CModel(context);
        }
        return sCModel;
    }

    private CModel(Context context) {
        contacts = new ArrayList<>();
        populateWithInitialContacts(context);

    }

    private void populateWithInitialContacts(Context context) {
        //Create the Foods and add them to the list;
        Roster roster = Roster.getInstanceFor(G.XMPPCONNECT);
        if (!roster.isLoaded())
            try {
                roster.reloadAndWait();
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

        Collection<RosterEntry> entries = roster.getEntries();
        Presence presence;

        for (RosterEntry entry : entries) {
            presence = roster.getPresence(entry.getUser());
            contacts.add(new Contact(entry.getUser()));

        }


    }

    public List<Contact> getContacts() {
        return contacts;
    }

}
