package com.example.erison.mapateste4;

import android.location.Location;
import android.media.Image;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Merker class, um marker pode ser tanto um marcador no mapa quanto um post no feed
 */

public class Marker {

    double lat, lng;
    int id, icon, upVotes, downVotes, shares, comments, react, foto=0;
    String type, description, subDescription,dia, ultimoDia, texto="";
    float impulsionar = 0;

    public Marker(){
        this(0,"",0,0,"","",0,0,0,0,"","","",0,0d,0);
    }

    public Marker(int id, String type, double lat, double lng,int icon, String description,String subDescription,
                  int upVotes,int downVotes,int shares, int comments){
        this.id = id;
        this.type = type;
        this.icon = icon;
        this.description = description;
        this.subDescription = subDescription;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.shares = shares;
        this.comments = comments;
        this.lat = lat;
        this.lng = lng;
        react = 0;
    }

    public Marker(int id, String type, double lat, double lng,int icon, String description, String dia, String ultimoDia){
        this.id = id;
        this.type = type;
        this.icon = icon;
        this.description = description;
        this.subDescription = description;
        this.upVotes = 0;
        this.downVotes = 0;
        this.shares = 0;
        this.comments = 0;
        this.lat = lat;
        this.lng = lng;
        this.dia = dia;
        this.ultimoDia = ultimoDia;
        this.impulsionar = 1;
        react = 0;
    }


    public Marker(int id, String type, Object lat, Object lng, String description, String subDescription,
                  int upVotes, int downVotes, int shares, int comments, String dia, String ultimoDia,
                  String texto, int imagem, Double impulso, int react){
        this.id = id;
        this.type = type;
        this.description = description;
        this.subDescription = dia;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.shares = shares;
        this.comments = comments;
        this.lat = Double.valueOf(lat.toString());
        this.lng = Double.valueOf(lng.toString());
        this.dia = dia;
        this.ultimoDia = ultimoDia;
        this.impulsionar = Float.parseFloat(impulso.toString());
        this.texto = texto;
        this.react = react;
        foto = imagem;
    }

    public Marker(int id, String type, Object lat, Object lng, String description){
        this.id = id;
        this.type = type;
        //this.icon = Integer.valueOf(icon.toString());
        this.description = description;
        this.subDescription = dia;
        this.lat = Double.valueOf(lat.toString());
        this.lng = Double.valueOf(lng.toString());
        this.upVotes = 0;
        this.downVotes = 0;
        this.shares = 0;
        this.comments = 0;
    }

}
