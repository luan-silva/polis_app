package com.example.erison.mapateste4;

import android.location.Address;

/**
 * Classe de um item de pesquisa
 */

public class SearchItem {
    int type; //0 pessoa, 1 local
    String name, subNome;
    double lat, lng;

    public SearchItem(int type, String name, String subNome, double lat, double lng){
        this.type = type;
        this.subNome = subNome;
        this.name = name;
        this.lat = lat;
        this.lng = lng;

    }
}
