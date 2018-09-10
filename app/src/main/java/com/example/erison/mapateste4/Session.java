package com.example.erison.mapateste4;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by luans on 21/05/2017.
 */

public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("POLIS", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Deixa as coisas nulas qnd o usuario sai da aplicação
     */
    public void setLoggedOut(){
        editor.putString("username","");
        editor.putString("nome", "");
        editor.commit();
    }

    /**
     * Salva o nome e username do usuário
     * @param username
     * @param nome
     */
    public void setLoggedin(String username, String nome){
        editor.putString("username",username);
        editor.putString("nome", nome);
        editor.commit();
    }

    /**
     * Carrega a pessoa que está logada
     * @return
     */
    public Pessoa getLogedPerson(){
        Pessoa p = new Pessoa(
                prefs.getString("username", "erro"),
                prefs.getString("nome", "erro"),
            "Sem conexão","","",0,0,0);
        return p;
    }

    public void setPosts(String posts){
        editor.putString("offlinePosts",posts);
        editor.commit();
    }

    public String getLastPosts(){
        return prefs.getString("offlinePosts", "erro");
    }

    /**
     * Verifica se tem algm logado ou não
     * @return
     */
    public boolean loggedin(){
        if(prefs.getString("username", "").equals("")){
            return false;
        }else{
            return true;
        }
    }
}

