package com.example.erison.mapateste4;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Conjunto de operações de gerencia do banco de dados
 */

public class DatabaseManager {

    /**
     * Verifica se o usuario está online ou não
     * @param ctx um Contexto
     * @return um boolean
     */
    public static boolean isOnline(Context ctx) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        catch(Exception ex){
            Toast.makeText(ctx, "Erro ao verificar se estava online! (" + ex.getMessage() + ")", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    /**
     * Recebe uma string em formato json e retorna um ArrayList dos posts presentes nesta string
     * @param s
     * @return
     */
    static ArrayList<Marker> TakeMarkersFromString(String s){
        try {
            int i, f;
            JSONObject jo;
            ArrayList<Marker> arrayList = new ArrayList<>();
            while (s.length() > 4){
                i = s.indexOf("{");
                f = s.indexOf("}")+1;
                jo = new JSONObject(s.substring(i,f));
                arrayList.add(new Marker(
                        jo.getInt("id"),
                        jo.getString("type"),
                        jo.getDouble("lat"),
                        jo.getDouble("lng"),
                        jo.getString("description"),
                        jo.getString("subdescription"),
                        jo.getInt("upVotes"),
                        jo.getInt("downVotes"),
                        jo.getInt("shares"),
                        jo.getInt("comments"),
                        jo.getString("dia"),
                        jo.getString("ultimoDia"),
                        jo.getString("texto"),
                        jo.getInt("foto"),
                        jo.getDouble("impulsionar"),
                        jo.getInt("react")
                ));
                s = s.substring(f);
            }
            return arrayList;

            //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Carrega um bocado de marcadores a partir da posição atual
     * @param ctx
     * @param l
     */
    static void FillData(final Context ctx, Location l){
        double dist = 0.25;

        String query = "select m.*, IFNULL(reaction,-1) as react from markers as m left join " +
                "(select pub_id, reaction from pub_reaction where user_id='"+
                Data.getInstance().getUser().username+"') as r on r.pub_id = m.id where (" +
                "lat>"+String.valueOf(l.getLatitude()-dist)+
                " and lat<"+String.valueOf(l.getLatitude()+dist)+
                " and lng>"+String.valueOf(l.getLongitude()-dist)+
                " and lng<"+String.valueOf(l.getLongitude()+dist)+
                ") OR impulsionar>1 ORDER BY prioridade desc;";



        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //if(s.equals("Error")) return;
                Session session = new Session(ctx);
                session.setPosts(s);
                Data.getInstance().getData().clear();
                Data.getInstance().getData().addAll(TakeMarkersFromString(s));
                FeedLayout.FillFeed(TakeMarkersFromString(s));
                //MapsLayout.filtrarData();

            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }

    /**
     * Carrega um bocado de marcadores a partir de uma pessoa
     * @param ctx
     * @param username
     */
    static void FillDataFromPerson(String username, Context ctx){
        String query  = "SELECT m.*, IFNULL(reaction,-1) as react FROM " +
                "(SELECT m.* FROM markers as m LEFT JOIN shares as s ON m.id=s.post_id" +
                "  WHERE m.type='"+username+"' OR s.user_id='"+username+"') as m " +
                "LEFT JOIN (select pub_id, reaction from pub_reaction where user_id='"+
                Data.getInstance().getUser().username+"') as r on r.pub_id = m.id;";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //if(s.equals("Error")) return;
                Perfil.setUserPosts(TakeMarkersFromString(s));
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }

    //############ PERSON #####################

    /**
     * Procura pessoa no BD
     * @param name
     * @param ctx
     */
    static void SearchPerson(String name, Context ctx){
        String[] query = new String[1];
        query[0] = "SELECT username, nome FROM people" +
                " WHERE nome LIKE '%"+name+"%' OR username LIKE '%"+name+"%'" +
                " ORDER  BY LOWER(username) LIMIT 20;";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //if(s.equals("Error")) return;
                try {
                    int i, f;
                    JSONObject jo;
                    ArrayList<SearchItem> arrayList = new ArrayList<>();
                    while (s.length() > 4){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        arrayList.add(new SearchItem(0,
                                jo.getString("nome"),
                                jo.getString("username"),
                                0,0
                        ));
                        s = s.substring(f);
                    }
                    MainActivity.FillSearch(arrayList);

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query[0], "", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }


    /**
     * Puxa a Pessoa com o username passado como parametro do banco de dados
     * @param username String, código da pessoa a ser carregada
     * @param ctx
     */
    static void getPerson(String username, final Context ctx) {
        String query;

        query = "SELECT *, " +
                "(SELECT count(segue) FROM segue WHERE segue='"+username+"') as num_segue," +
                "(SELECT count(segue) FROM segue WHERE seguido='"+username+"') as num_seguido," +
                "(SELECT count(segue) FROM segue WHERE segue='"+Data.getInstance().getUser().username+"' AND seguido='"+username+"') as ja_segue" +
                " FROM people WHERE username='"+username+"';";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                System.out.println(s);

                try {

                    int i, f;
                    JSONObject jo;
                    Pessoa p = null;
                    while (s.length() > 4){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        p = new Pessoa(
                                jo.getString("username"),
                                jo.getString("nome"),
                                jo.getString("sobre"),
                                jo.getString("foto"),
                                jo.getString("capa"),
                                jo.getInt("num_segue"),
                                jo.getInt("num_seguido"),
                                jo.getInt("ja_segue")
                        );
                        s = s.substring(f);
                    }

                    System.out.println("chamando BD");
                    System.out.println("chamando BD");
                    System.out.println("chamando BD");
                    System.out.println("chamando BD");
                    System.out.println("chamando BD");

                    Perfil.fillPerfil(p);

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Pega nome de usuário e senha para enviar por email na recuperação de dados
     * @param email
     * @param ctx
     */
     
    static void getSenha(final String email, final Context ctx) {
        String query;
        final String[] corpo = new String[3];
        query = "SELECT username,email, nome, senha FROM people WHERE email='"+email+"';";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                System.out.println(s);
                try {
                    int i, f;
                    JSONObject jo;
                    if (s.length() > 4){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        corpo[0] = jo.getString("nome");
                        corpo[1] = jo.getString("username");
                        corpo[2] = jo.getString("senha");


                        recuperar_senha.EnviarEmail(email, corpo, ctx);
                        Toast.makeText(ctx, "Email enviado com sucesso!", Toast.LENGTH_SHORT).show();
                        tela_login.dialog.dismiss();
                    }else {
                        Toast.makeText(ctx, "Email inválido!", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Exclui a conta do usuario
     * @param username
     * @param ctx
     */
    static void ApagarConta(String username, final Context ctx){
        String query;
        query = "DELETE FROM people WHERE username='"+username+"'";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                System.out.println(s);
                Toast.makeText(ctx,"Conta apagada!", Toast.LENGTH_LONG).show();
            }
        };
        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }

    /**
     * Ação do botão seguir, chama a procedure seguir no banco de dados e atualiza os valores
     * @param username1 //username da pessoa que está seguindo
     * @param username2 //username da pessoa que está sendo seguida
     * @param ctx
     */
    static void follow(String username1, String username2, Context ctx){
        String query;

        query = "CALL seguir('"+username1+"','"+username2+"');";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {

            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/insert.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Atualiza os valores de uma pessoa no banco de dados a partir de uma Pessoa passada como parâmetro
     * Ultilizado para dar update no perfil, senha e fotos;
     * @param p //Pessoa que será atualizada
     * @param ctx
     */
    static void updatePerson(Pessoa p, final Context ctx){
        String query;

        query = "UPDATE people SET nome='"+p.nome+"', sobre='"+p.sobre+"', foto='"+p.foto+"', capa='"+p.capa+"' WHERE username='"+p.username+"';";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(ctx, "Perfil Alterado com sucesso :)", Toast.LENGTH_LONG);
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/insert.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }



    /**
     * Altera a senha do usuario
     * @param username
     * @param newSenha
     * @param oldSenha
     * @param ctx
     */
     
    static void updateSenha(String username, String newSenha, String oldSenha, final Context ctx) {
        String query;

        query = "call editSenha('"+username+"','"+oldSenha+"','"+newSenha+"', @val);";
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    int i, f;
                    JSONObject jo;


                    System.out.println("AQUIII"+s);

                    i = s.indexOf("{");
                    f = s.indexOf("}")+1;
                    //isola s e tranforma em um JSONobject
                    jo = new JSONObject(s.substring(i,f));
                    //se a variavel json existe for igual a zero então não existe
                    if(jo.getInt("resposta")==1){
                        Toast.makeText(ctx, "Senha modificada com sucesso", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ctx, "Senha incorreta", Toast.LENGTH_LONG).show();
                    }

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }


    /**
     * Adiciona uma pessoa no banco de dados
     * Nota: Essa pessoa deve ter sido criada antes usando a classe Pessoa;
     * @param p Pessoa que será adicionada
     * @param ctx
     */
    static void addPerson(Pessoa p, final Context ctx){
        String query;

        query = "call register('"+p.username+"','"+p.email+"','"+p.senha+"','"+p.nome+"', @val); ";

         Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    int i, f;
                    JSONObject jo;

                    i = s.indexOf("{");
                    f = s.indexOf("}")+1;

                    System.out.println("AQUIII"+s);
                    System.out.println("AQUIII"+s);
                    System.out.println("AQUIII"+s);
                    System.out.println("AQUIII"+s);
                    //isola s e tranforma em um JSONobject
                    jo = new JSONObject(s.substring(i,f));
                    //se a variavel json existe for igual a zero então não existe
                    if(jo.getInt("resposta")==1){
                        Toast.makeText(ctx, "Usuário já usado", Toast.LENGTH_LONG).show();
                    }else if (jo.getInt("resposta")== 2){
                        Toast.makeText(ctx, "Email já usado", Toast.LENGTH_LONG).show();
                    } else{
                        Toast.makeText(ctx, "Usuário cadastrado com sucesso", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ctx, tela_login.class);
                        ctx.startActivity(intent);
                        ((Activity) ctx).finish();
                        ((Activity) ctx).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Verifica se o usuário passado existe ou não
     * @param username username de login
     * @param senha senha de login
     * @param ctx
     */
    static void VerificaLogin(final String username, String senha, final Context ctx)   {

        String query;

        query = "SELECT username,nome, count(username)as existe FROM people WHERE (username='"+username+"' OR email='"+username+"') AND senha='"+senha+"';";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //recebe a string s em formato json
                try {
                    int i, f;
                    JSONObject jo;

                    i = s.indexOf("{");
                    f = s.indexOf("}")+1;


                    System.out.println(s);System.out.println(s);System.out.println(s);System.out.println(s);
                    //isola s e tranforma em um JSONobject
                    jo = new JSONObject(s.substring(i,f));
                    //se a variavel json existe for igual a zero então não existe
                    if(jo.getInt("existe")==0){
                        //não deu
                        Toast.makeText(ctx, "Usuário ou senha incorretos", Toast.LENGTH_LONG).show();
                        tela_login.erroLogin();

                    }else{//c.c. então existe
                        //deu, dzr oq q vai fzr;
                        Intent intent = new Intent(ctx, MainActivity.class);
                        ctx.startActivity(intent);

                        //seta como logado
                        Session session = new Session(ctx);
                        session.setLoggedin(jo.getString("username"),jo.getString("nome"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        tela_login.spinner.setVisibility(View.VISIBLE);
        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Adicionar um marcador no banco de dados
     *
     * @param m Marker, para isso ver classe Marker
     * @param ctx
     */
    static void PutMarker(final Marker m, final Context ctx){
        m.texto = "";
        m.foto = 0;
        final String query = "call addMarker('"+ m.type +
                "'," + String.valueOf(m.lat) +
                "," + String.valueOf(m.lng) +",'"+m.description+"','"+m.texto+"',0,'"
                +Data.getInstance().getUser().username+"', @val);";

        final Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    int i, f;
                    System.err.println(s);
                    System.err.println(s);
                    System.err.println(s);
                    System.err.println(s);
                    JSONObject jo;
                    i = s.indexOf("{");
                    f = s.indexOf("}")+1;
                    jo = new JSONObject(s.substring(i,f));
                    if(jo.getInt("p_id")==-1){
                        Toast.makeText(ctx,"Marcador já exsite, você somente curtiu o marcador existente", Toast.LENGTH_LONG).show();
                    }else if(jo.getInt("p_id")==-2){
                        Toast.makeText(ctx,"Marcador já exsite e você já curtiu ele", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ctx,"Marcador adicionado nas suas proximidades", Toast.LENGTH_LONG).show();
                        m.id=jo.getInt("p_id");
                        m.react = -1;
                        FeedLayout.AddItemOnFeed(m);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Adicionar um post :)
     *
     * @param m Marker, para isso ver classe Marker
     * @param ctx
     */
    static void AddPost(final Marker m, final Bitmap img, final Context ctx){

        final String query = "call postar('"+m.type +
                "'," + String.valueOf(m.lat) +
                "," + String.valueOf(m.lng) +
                ",'"+m.description+"','"+m.texto+"',"+m.foto+", @val);";

        final Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //return;}
                try {
                    int i, f;
                    JSONObject jo;
                    i = s.indexOf("{");
                    f = s.indexOf("}")+1;
                    jo = new JSONObject(s.substring(i,f));
                    if(m.foto!=0) {
                        ImageControl.upload(img, "p" + jo.getString("p_id") + ".jpg", ctx);
                        Toast.makeText(ctx, "Post adicionado com sucesso", Toast.LENGTH_LONG).show();
                    }
                    m.id=jo.getInt("p_id");
                    m.react = -1;
                    FeedLayout.AddItemOnFeed(m);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }


    //#################################################################
    //######################### REACTIONS #############################
    //#################################################################

    /**
     * Adiciona um comentário no banco de dados
     * @param postId id do post comentado
     * @param username usuário que colocou o comentario
     * @param comment comentario
     * @param ctx
     */
    static void PutComment(int postId, String username, String comment, Context ctx) {
        String query;

        query = "CALL comentar("+postId+",'"+username+"','"+comment+"');";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);System.out.println(s);System.out.println(s);
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/insert.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }

    /**
     * Carrega os comentarios a partir do id da publicação
     * @param postId
     * @param ctx
     */
    static void LoadComments(int postId, Context ctx){
        String query;

        query = "SELECT c.*,p.username, p.nome FROM comments as c join people as p ON c.user_id=p.username WHERE c.post_id="+postId+";";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                try {
                    ArrayList<CommentElement> commentElementArrayList = new ArrayList<>();
                    int i, f;
                    JSONObject jo;
                    CommentElement c;
                    while (s!=null && s.length() > 2){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        c = new CommentElement(
                                jo.getInt("id_c"),
                                jo.getInt("post_id"),
                                jo.getString("user_id"),
                                jo.getString("post_comment"),
                                jo.getString("nome")
                        );
                        s = s.substring(f);
                        commentElementArrayList.add(c);
                    }
                    FeedLayout.setComments(commentElementArrayList);

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }

    /**
     * Curte a publicação passada como parametro
     * @param id
     * @param type
     * @param ctx
     */
    static void like(int id, int type, Context ctx) {
        String query;

        query = "CALL curtir("+id+",'"+Data.getInstance().getUser().username+"',"+type+");";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);System.out.println(s);System.out.println(s);
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/insert.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);

    }

    /**
     * Compartilha o post passado como parametro
     * @param post
     * @param ctx
     */
    static void SharePost(int post, Context ctx){
        String query;

        query = "call sharePost("+post+",'"+Data.getInstance().getUser().username+"')";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/insert.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Carrega os seguidores do usuário
     * @param username
     * @param ctx
     */
    static void followFill(String username,Context ctx){
        String query;

        query = "SELECT seguido,nome FROM people JOIN segue ON seguido=username WHERE segue='"+username+"';";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                try {
                    ArrayList<Pessoa> arrayList = new ArrayList<>();
                    int i, f;
                    JSONObject jo;
                    Pessoa p;
                    while (s!=null && s.length() > 2){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        p = new Pessoa(
                            jo.getString("seguido"),
                            jo.getString("nome")
                        );
                        s = s.substring(f);
                        arrayList.add(p);
                    }
                    Perfil.setFollows(arrayList);

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }


    //#################################################################
    //########################### VOTOS ###############################
    //#################################################################

    /**
     * Carrega os topicos de votação antuais
     * @param username
     * @param ctx
     */
    static void VottingFill(String username,Context ctx){
        String query;

        query = "SELECT t.*,IFNULL(v.voto,-1) as voto FROM topicos as t LEFT JOIN votou as v ON v.v_user='"+username+"' AND v.voto_id=t.id_t WHERE ativo=3;";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                try {
                    ArrayList<Voto> arrayList = new ArrayList<>();
                    int i, f;
                    JSONObject jo;
                    Voto v;
                    while (s!=null && s.length() > 3){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        v = new Voto(
                                jo.getInt("id_t"),
                                jo.getString("nome"),
                                jo.getString("description"),
                                jo.getString("text"),
                                jo.getString("link"),
                                jo.getInt("voto"),
                                jo.getString("date")
                        );
                        s = s.substring(f);
                        arrayList.add(v);
                    }
                    VotingLayout.fillVotos(arrayList);

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Carrega os topicos de votação antigos
     * @param username
     * @param ctx
     */
    static void VottingOldFill(String username,Context ctx){
        String query;

        query = "SELECT t.*,IFNULL(v.voto,0) as voto FROM topicos as t LEFT JOIN votou as v ON v.v_user='"+username+"' AND v.voto_id=t.id_t WHERE ativo=2;";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println(s);
                try {
                    ArrayList<Voto> arrayList = new ArrayList<>();
                    int i, f;
                    JSONObject jo;
                    Voto v;
                    while (s!=null && s.length() > 2){
                        i = s.indexOf("{");
                        f = s.indexOf("}")+1;
                        jo = new JSONObject(s.substring(i,f));
                        v = new Voto(
                                jo.getInt("id_t"),
                                jo.getString("nome"),
                                jo.getString("description"),
                                jo.getString("text"),
                                jo.getString("link"),
                                jo.getInt("voto"),
                                jo.getInt("decision"),
                                jo.getInt("popS"),
                                jo.getInt("popN"),
                                true,
                                jo.getString("date")
                        );
                        s = s.substring(f);
                        arrayList.add(v);
                    }
                    VotingLayout.fillOldVotos(arrayList);

                    //Toast.makeText(, jsonObject.getString("reposta"), Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/get.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

    /**
     * Vota em um tópico de votação
     * @param topico
     * @param user
     * @param type
     * @param ctx
     */
    static void Votar(int topico, String user, int type, Context ctx){
        String query;

        query = "call votar("+topico+",'"+Data.getInstance().getUser().username+"',"+type+");";

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
            }
        };

        PutDataRequest put = new PutDataRequest("http://erisonmiller.000webhostapp.com/insert.php", query, "end", response);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(put);
    }

}

