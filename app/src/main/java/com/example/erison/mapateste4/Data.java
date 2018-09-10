package com.example.erison.mapateste4;

import android.os.StrictMode;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe para manter variaveis globais permanentes
 * this is using a program model, i dont remember the name, sory, but is that one that has only one instance, so when i need to
 * use some data i just need to get this istance, and existe only one, so i can get the resources back
 */
public class Data {

    //the markes data from the feed
    private ArrayList<Marker> data = new ArrayList<>();
    //actual logged user
    private Pessoa user;
    //i dont remember ' -'
    //must be removed after
    public String jsonText;

    //the data instance
    private static final Data ourInstance = new Data();

    /**
     * returns the only instancied data
     * @return
     */
    public static Data getInstance() {
        return ourInstance;
    }

    /**
     * private constructor, so the only how can create new datas is the data itself
     */
    private Data() {
    }

    //getters and setters
    public ArrayList<Marker> getData() {
        return data;
    }

    public void setData(ArrayList<Marker> data) {
        this.data = data;
    }

    public Pessoa getUser() {
        return user;
    }

    public void setUser(Pessoa user) {
        this.user = user;
    }


}
