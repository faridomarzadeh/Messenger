package com.example.faridomar.messengerapp;

/**
 * Created by Farid omar on 1/28/2017.
 */
public class Contact {

    private String jid;

    public Contact(String id )
    {
        jid = id;
    }

    public String getJid()
    {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }
}
