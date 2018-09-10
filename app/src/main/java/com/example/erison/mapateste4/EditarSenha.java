package com.example.erison.mapateste4;



import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.content.Intent;

import static android.content.Intent.getIntent;

/**
 * Created by Thalita on 28/05/2017.
 */

/**
 *DOCUMENTO DESATIVADO POIS FORAM MOVIDAS AS FUNÇÕES PARA A MAINACTIVITY COM MUDANÇAS AO LONGO DO DESENVOLVIMENTO
 */
public class EditarSenha extends Fragment{

    static  View edit_view;
    private Session sessao;
    private Pessoa usuario;
    private Dialog followDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessao = new Session(getActivity());

        edit_view = (View) inflater.inflate(R.layout.editarsenha, container, false);

        final EditText edit_senhaAtual = (EditText) edit_view.findViewById(R.id.senhaAtualid);
        final EditText edit_novaSenha = (EditText) edit_view.findViewById(R.id.novaSenhaid);
        final EditText edit_confirmarSenha = (EditText) edit_view.findViewById(R.id.confirmarSenhaid);
        final Button edit_alterarSenha = (Button) edit_view.findViewById(R.id.botaoAlterarid);

        followDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        followDialog.setContentView(R.layout.editarsenha);
        followDialog.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followDialog.hide();
            }
        });


        edit_alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = sessao.getLogedPerson();

                if(edit_senhaAtual.length()==0 ) {
                    edit_senhaAtual.requestFocus();
                    edit_senhaAtual.setError("Senha inválida");
                }
                if(edit_novaSenha.length()==0){
                    edit_novaSenha.requestFocus();
                    edit_novaSenha.setError("Senha inválida.");
                }

                if(edit_confirmarSenha.length()==0){
                    edit_confirmarSenha.requestFocus();
                    edit_confirmarSenha.setError("Senha não confirmada");
                }

                if(!usuario.senha.equals(edit_senhaAtual.toString())){
                    edit_senhaAtual.requestFocus();
                    edit_senhaAtual.setError("Senha incorreta.");
                }

                if(!edit_novaSenha.toString().equals(edit_confirmarSenha.toString())){
                    edit_novaSenha.requestFocus();
                    edit_novaSenha.setError("As senhas não correspondem.");
                }
                else{
                    usuario.senha = edit_novaSenha.getText().toString();
                }

            }
        });



        return edit_view;
    }
}
