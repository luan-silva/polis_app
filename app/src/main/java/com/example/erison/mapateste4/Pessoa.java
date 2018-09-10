package com.example.erison.mapateste4;

/**
 * Possui tudo que uma pessoa deve ter
 */

public class Pessoa {
    public String email, nome, username, senha, sobre, foto, capa;
    public int seguindo, seguidores;
    public boolean ja_sigo;

    //só um bocado de montadores
    public Pessoa(String username, String nome){
        this(username,"",nome,"","","",0,0,1,"");
    }

    public Pessoa(String username, String senha, String nome, String sobre, String foto, String capa){
        this(username,senha,nome,sobre,foto,0,0,0);
    }
    public Pessoa(String username, String nome, String sobre, String foto, String capa, int seguindo, int seguidores, int ja_sigo){
        this(username,"",nome,sobre,foto,capa,seguindo,seguidores,ja_sigo, "");
    }

    public Pessoa(String username, String nome, String sobre, String foto, String capa, int seguindo, int seguidores, int ja_sigo, String email){
        this(username,"",nome,sobre,foto,capa,seguindo,seguidores,ja_sigo,email);
    }


    //não passa de um bocado de montadores msm
    //this shold be a struct, i dont know why i make this like a class ' -'
    public Pessoa(String username, String senha, String nome, String sobre, String foto, String capa, int seguindo, int seguidores, int ja_sigo, String email){
        this.nome=nome;
        this.username=username;
        this.sobre=sobre;
        this.seguidores=seguidores;
        this.seguindo=seguindo;
        this.capa=capa;
        this.foto=foto;
        this.senha = senha;
        this.email = email;
        if(ja_sigo==0) {
            this.ja_sigo = false;
        }else{
            this.ja_sigo = true;
        }
    }
}
