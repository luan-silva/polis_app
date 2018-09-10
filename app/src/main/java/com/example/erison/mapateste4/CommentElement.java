package com.example.erison.mapateste4;

/**
 * Classe para um comentario
 *
 * int id_c, id_pub;
 * public String text,id_user, userName;
 */

public class CommentElement {
    int id_c, id_pub;
    public String text,id_user, userName;
    //montador
    public CommentElement(int id_c, int id_pub, String id_user, String text, String userName){
        this.id_c = id_c;
        this.id_pub = id_pub;
        this.text = text;
        this.id_user = id_user;
        this.userName = userName;
    }
}
