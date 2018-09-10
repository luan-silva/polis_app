package com.example.erison.mapateste4;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import static com.example.erison.mapateste4.R.id.cad_email;
import static com.example.erison.mapateste4.R.id.cad_nome;
import static com.example.erison.mapateste4.R.id.cad_senha;
import static com.example.erison.mapateste4.R.id.cad_username;

public class tela_cadastro extends AppCompatActivity {
    private static Context cad_context;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_cadastro);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        session = new Session(this);
        final EditText cad_email = (EditText) findViewById(R.id.cad_email);
        final EditText cad_nome = (EditText) findViewById(R.id.cad_nome);
        final EditText cad_username = (EditText) findViewById(R.id.cad_username);
        final EditText cad_senha = (EditText) findViewById(R.id.cad_senha);
        final EditText cad_confirma_senha = (EditText) findViewById(R.id.cad_confirma_senha);
        final Button botaoCadastrar = (Button)findViewById(R.id.botaoCadastrar);
        ImageButton cad_voltar = (ImageButton)findViewById(R.id.cad_voltar);

        cad_voltar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String Nome=cad_nome.getText().toString();
                final String senha=cad_senha.getText().toString();
                final String email=cad_email.getText().toString();
                final String username =cad_username.getText().toString();
                final String confirma_senha = cad_confirma_senha.getText().toString();

                //VALIDAÇÃO
                if(Nome.length()==0)
                {
                    cad_nome.requestFocus();
                    cad_nome.setError("Nome não pode ficar vazio!");
                }
                else if(email.length()==0){
                    cad_email.requestFocus();
                    cad_email.setError("Email não pode ficar vazio!");
                }
                else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")){
                    cad_email.setError("Email não é válido!");
                }
                else if(username.length()==0){
                    cad_username.requestFocus();
                    cad_username.setError("Nome de usuário não pode ficar vazio!");
                }
                else if(username.length()<6){
                    cad_username.requestFocus();
                    cad_username.setError("Nome de usuário deve ter 6 caracteres ou mais!");
                }
                else if(!Nome.matches("[a-zA-Z ]+"))
                {
                    cad_nome.requestFocus();
                    cad_nome.setError("Digite apenas caracteres do alfabeto");
                }
                else if(senha.length()==0)
                {
                    cad_senha.requestFocus();
                    cad_senha.setError("Senha não pode ficar vazio!");
                }
                else if(!senha.equals(confirma_senha)){
                    cad_confirma_senha.requestFocus();
                    cad_confirma_senha.setError("Senha não confere!");
                }
                else if(senha.length()<6){
                    cad_confirma_senha.requestFocus();
                    cad_confirma_senha.setError("Senha deve ter 6 caracteres ou mais!");
                }
                else
                {
                    Pessoa pessoa_cad;
                    pessoa_cad = new Pessoa(cad_username.getText().toString(),
                            cad_senha.getText().toString(),
                            cad_nome.getText().toString(),
                            "","","", 0,0,0,
                            cad_email.getText().toString());


                    DatabaseManager.addPerson(pessoa_cad, tela_cadastro.this);
                    //Toast.makeText(MainActivity.this,"Validation Successful", Toast.LENGTH_LONG).show();
                }
            }
        });
        cad_context=this;


    }



    }

