package com.example.erison.mapateste4;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe responsável por se comunicar com o servidor através de string request
 */

public class PutDataRequest extends StringRequest {

    private Map<String, String> params;

    /**
     * Prepara um requerimento para o site, para saber isso é necessário ter acesso ao site e os
     * arquivos php lá exstentes, além de uma visão geral do banco de dados
     *
     * @param url url do arquivo php
     * @param query0 primeiro comando a ser passado
     * @param query1 segundo comando a ser passado
     * @param listener response listener, isso diz o que a funçao vai fzr qnd receber a resposta
     */
    public PutDataRequest(String url, String query0, String query1, Response.Listener<String> listener) {
        super(Method.POST, url, listener, null);
        params = new HashMap<>();
        params.put("query", query0);
        params.put("cond", query1);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}