package com.example.erison.mapateste4;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v4.app.FragmentTransaction;


/**
 * Created by Thalita on 23/05/2017.
 */

public class Configurações extends Fragment {

    private View view;
    private LinearLayout editarPerfil;
    private LinearLayout editarSenha;
    private LinearLayout apagarConta;
    private LinearLayout sobre;
    private LinearLayout sair;
    private Session session;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getActivity());




        /**
         * Evento Editar Perfil
         */
        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showPerfil(Data.getInstance().getUser().username);
            }

        });


        /**
         * cria um dialog para exibir a tela 'Sobre'
         */
        final Dialog telaSobre = new Dialog(getActivity(),R.style.AppTheme);
        telaSobre.setContentView(R.layout.sobre);
        sobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaSobre.show();

            }

        });

        //verifica se usuário está logado
        // enquanto não for apertado o botao sair, mantém o login permanente
        if (!session.loggedin()) {
            logout();
        }


        /**
         * evento do botão sair
         */
        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerta_sair();
            }
        });

        //sistema de dialog para editar senha
        final Dialog telaEditarSenha = new Dialog(getActivity(),R.style.AppTheme);
        telaEditarSenha.setContentView(R.layout.editarsenha);

        final EditText edit_senhaAtual = (EditText) telaEditarSenha.findViewById(R.id.senhaAtualid);
        final EditText edit_novaSenha = (EditText) telaEditarSenha.findViewById(R.id.novaSenhaid);
        final EditText edit_confirmarSenha = (EditText) telaEditarSenha.findViewById(R.id.confirmarSenhaid);
        final Button edit_alterarSenha = (Button) telaEditarSenha.findViewById(R.id.botaoAlterarid);


        edit_alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_senhaAtual.getText().toString().length()==0 ) {
                    edit_senhaAtual.requestFocus();
                    edit_senhaAtual.setError("Senha inválida");
                }
                else if(edit_novaSenha.getText().toString().length()==0){
                    edit_novaSenha.requestFocus();
                    edit_novaSenha.setError("Senha inválida.");
                }

                else if(edit_confirmarSenha.getText().toString().length()==0){
                    edit_confirmarSenha.requestFocus();
                    edit_confirmarSenha.setError("Senha não confirmada");
                }
                else if(!edit_novaSenha.getText().toString().equals(edit_confirmarSenha.getText().toString())){
                    edit_novaSenha.requestFocus();
                    edit_novaSenha.setError("As senhas não correspondem.");
                }
                else{
                    DatabaseManager.updateSenha(Data.getInstance().getUser().username, edit_novaSenha.getText().toString(), edit_senhaAtual.getText().toString(), getContext());
                }

            }
        });


        /**
         * Evento do botão Editar senha.
         * Chama outra tela  para a modificação da senha
         */
        editarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaEditarSenha.show();

            }
        });
        /**
         *Evento de botão voltar na tela de editar senha
         *
         */

        telaEditarSenha.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaEditarSenha.hide();
            }
        });
        /**
         * Evento para chamar função que apaga a conta do usuario
         */
        apagarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerta_apagar_conta();
            }
        });


            return view;
    }

    public static void AlterPassword(int result){

    }

    /**
     * método para a opção sair das configurações
     * (fazer logout)
     */
    public void logout(){
        session.setLoggedOut();
        getActivity().finish();
        startActivity(new Intent(getActivity(),tela_login.class));
    }

    /**
     * dialog para apagar a conta
     */
    private void Alerta_apagar_conta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alerta!");
        builder.setMessage("Você realmente quer apagar a sua conta?");
        builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                }
        });
        builder.setNegativeButton("Apagar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseManager.ApagarConta(session.getLogedPerson().username, getActivity());
                dialog.dismiss();
                logout();
            }
       });
        AlertDialog dialog = builder.create();
        dialog.show();
        }

    /**
     * Alertdialog para Sair da aplicação
     */
    private void Alerta_sair() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alerta!");
        builder.setMessage("Você realmente quer sair da aplicação?");
        builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                logout();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}