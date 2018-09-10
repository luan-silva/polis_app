package com.example.erison.mapateste4;

/**
 * Estrutura de um voto
 */

public class Voto {
    String nome, descrição, text, link, data;
    int id, reaction, decision;
    float peopleS, peopleN;
    boolean ja_votou;//diz se já foi votado na camara

    public Voto(int id,String nome, String descrição, String text, String link, int reaction, String data){
        this(id,nome,descrição,text,link,reaction, 0,0,0,false, data);
    }

    public Voto(int id, String nome, String descrição, String text, String link, int reaction, int decision, float peopleS, float peopleN, boolean ja_votou, String data){
        this.id = id;
        this.nome = nome;
        this.descrição = descrição;
        this.text = text;
        this.link = link;
        this.reaction = reaction;
        this.decision = decision;
        this.peopleS = peopleS;
        this.peopleN = peopleN;
        this.ja_votou=ja_votou;
        this.data = data;
    }
}
