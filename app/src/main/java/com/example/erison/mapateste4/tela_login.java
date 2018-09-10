package com.example.erison.mapateste4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class tela_login extends AppCompatActivity {
    private static Context login_context;
    private Session session;
    public static RelativeLayout spinner;
    public static CountDownTimer contador;
    static AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_login);

        session = new Session(this);
        final EditText login = (EditText) findViewById(R.id.username);
        final EditText senha = (EditText) findViewById(R.id.password);
        final TextView txt_cadastro = (TextView) findViewById(R.id.cadastro);
        final TextInputLayout InputLayoutEmail = (TextInputLayout) findViewById(R.id.usernameTIL);
        final TextInputLayout InputLayoutSenha = (TextInputLayout) findViewById(R.id.passwordTIL);
        TextView txt_esqueceu_senha = (TextView) findViewById(R.id.txt_esqueceu_senha);

        //loading
        spinner=(RelativeLayout)findViewById(R.id.progressBar);

        Button botaologar = (Button) findViewById(R.id.botao_login);

        if (session.loggedin()) {
            startActivity(new Intent(tela_login.this, MainActivity.class));
            finish();
        }

        //efeito sublinha o texto da textview
        txt_cadastro.setPaintFlags(txt_cadastro.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abreCadastro = new Intent(tela_login.this, tela_cadastro.class);
                startActivity(abreCadastro);

                //Chama a animação entre telas
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

         //sublinha o texto
        txt_esqueceu_senha.setPaintFlags(txt_esqueceu_senha.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        //Evento para exibir dialog de recuperação de senha
        txt_esqueceu_senha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(tela_login.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_recuperar_senha, null);
                final EditText pegaEmail = (EditText) mView.findViewById(R.id.recuperarEmail);
                final Button botaoEnviar = (Button) mView.findViewById(R.id.botaoEnviar);
                final Button botaoCancelar = (Button) mView.findViewById(R.id.btn_cancelar_rec);
                final TextInputLayout layout_email = (TextInputLayout) mView.findViewById(R.id.txt_inputlayout_recuperar_email);


                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();

                //Evento dialog para recuperar senha
                botaoEnviar.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (pegaEmail.getText().length()==0){
                            layout_email.setError("Campo Vazio");
                            layout_email.requestFocus();

                        }else if(!pegaEmail.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){

                            layout_email.setError("Email Inválido");
                            layout_email.requestFocus();
                        }else{
                            DatabaseManager.getSenha(pegaEmail.getText().toString(), mBuilder.getContext());
                        }
                    }
                });

                botaoCancelar.setOnClickListener(new  View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        //Evento para validação de dados e realizar login
        botaologar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DatabaseManager.isOnline(login_context)) {
                    if (login.getText().length()==0) {
                        Toast.makeText(login_context, "Digite o seu email", Toast.LENGTH_SHORT).show();
                        InputLayoutEmail.requestFocus();
                    } else if(senha.getText().length()==0){
                        Toast.makeText(login_context, "Digite sua senha", Toast.LENGTH_SHORT).show();
                        InputLayoutSenha.requestFocus();
                    } else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);;
                        contador = new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                //spinner.setVisibility(View.VISIBLE);
                                DatabaseManager.VerificaLogin(login.getText().toString(), senha.getText().toString(), tela_login.this);
                            }
                            @Override
                            public void onFinish() {
                                spinner.setVisibility(View.INVISIBLE);
                            }
                        };
                        contador.start();
                    }
                } else {
                    Toast.makeText(login_context, "Falha na conexão", Toast.LENGTH_SHORT).show();
                }


            }
        });
        login_context = this;
    }


    public static void erroLogin(){
        spinner.setVisibility(View.INVISIBLE);
    }

}
