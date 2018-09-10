package com.example.erison.mapateste4;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class recuperar_senha extends AppCompatActivity {
    private static Context ctx;
    Button botaoEnviar;
    EditText pegaEmail;
    String[] corpo = new String[3];
    //String email;
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recuperar_senha);
        pegaEmail = (EditText)findViewById(R.id.recuperarEmail);
        botaoEnviar = (Button)findViewById(R.id.botaoEnviar);

        botaoEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.getSenha(pegaEmail.getText().toString(), recuperar_senha.this);
            }
        });
        ctx = getApplicationContext();
    }*/

    /**
     * Metodo que envia email para recuperação de dados
     * @param email
     * @param corpo
     * @param ctx
     */

    public static void EnviarEmail(final String email, final String[] corpo, Context ctx){

        //String nome = txtNome.getText().toString();

        if(DatabaseManager.isOnline(ctx)) {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    Mail m = new Mail("polisalcathea2017@gmail.com", "luanerisonthalita");

                    String[] toArr = {email};
                    m.setTo(toArr);

                    m.setFrom("polisalcathea2017@gmail.com");
                    m.setSubject("Recuperação de dados - POLIS");
                    m.setBody(" Olá "+corpo[0]+"!<br /> O seu username é:"+corpo[1]+" <br /> A sua senha é: "+corpo[2]+" ");

                    try {
                        //m.addAttachment("pathDoAnexo");//anexo opcional
                        m.send();
                    }
                    catch(RuntimeException rex){ }//erro ignorado
                    catch(Exception e) {
                        //tratar algum outro erro aqui
                    }


                }
            }).start();
        }
        else {
            Toast.makeText(ctx, "Não estava online para enviar e-mail!", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
    }
}
