package com.example.erison.mapateste4;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.example.erison.mapateste4.R.id.followRecycle;
import static com.example.erison.mapateste4.R.id.foto;

/**
 * The frangment controller to the perfil xml file
 */

public class Perfil extends Fragment{
    //this view
    static View perfil;
    //this context
    private static Context ctx;
    //the person that is being showed in the perfil
    private static Pessoa pessoa = new Pessoa("Carregando", "Carregando", "Carregando","padrao","padrao",0,0,0);
    //the person after being edited
    private static Pessoa editedPessoa;

    private static int RESULT_LOAD_IMAGE = 1;//this is for get images
    private static View myBottomSheet;// the bottonSheetToEdit
    private static BottomSheetDialog editPerfil;
    private static String byteString;///this is to upload the image
    int viewToPutImage;//the view to image fullScreen
    Uri imageUri;//the path of the image to be getted

    //seguidores
    private static Dialog followDialog;//the dialog of fullScreen
    private RecyclerView FollowsRecycler;
    private static PersonViewAdapter FollowAdapter;


    //Posts do usuário
    private static RecyclerView postsRecycler;
    private static RviewAdapter postsAdapter;
    /**
     * Start the profile
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //carrega da view do perfil
        perfil = (View) inflater.inflate(R.layout.perfil, container,false);
        ctx = getContext();
        //carrega os valores da pessoa com o username passado como parametro
        //DatabaseManager.getPerson(Data.getInstance().getUser().username, ctx);


        //botões da view
        final FloatingActionButton seguir = (FloatingActionButton) perfil.findViewById(R.id.seguir);
        final ImageButton editar = (ImageButton) perfil.findViewById(R.id.edit);
        final ImageView foto = (ImageView) perfil.findViewById(R.id.foto);
        final ImageView capa = (ImageView) perfil.findViewById(R.id.Fundo);


        //################ POSTS MAKE ################
        postsRecycler = (RecyclerView) perfil.findViewById(R.id.MyPostsView);
        postsRecycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        ArrayList<Marker> dataPosts = new ArrayList<>();
        postsAdapter = new RviewAdapter(getActivity(), dataPosts);
        //set the recycler adapter
        postsRecycler.setAdapter(postsAdapter);
        postsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        //################ FOLLOW MAKE ################

        //cria o dialog
        followDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        //diz qual o layou do dalog, no caso é o full_screen_image
        followDialog.setContentView(R.layout.follow_dialog);
        FollowsRecycler = (RecyclerView) followDialog.findViewById(R.id.followRecycle);
        FollowsRecycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        ArrayList<Pessoa> data = new ArrayList<>();
        FollowAdapter = new PersonViewAdapter(getActivity(), data);
        //set the recycler adapter
        FollowsRecycler.setAdapter(FollowAdapter);
        FollowsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        followDialog.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFollowers();
            }
        });


        //change the button style if the person is already be folloewd or not
        seguir.setSelected(pessoa.ja_sigo);

        /**
         * change the button style and put in the data when follow is clicked
         */
        seguir.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                DatabaseManager.follow(Data.getInstance().getUser().username, pessoa.username, getContext());//put on the data
                //change the button
                if(pessoa.ja_sigo){
                    pessoa.ja_sigo=false;
                    //diminui os seguidores
                    pessoa.seguidores--;
                }else{
                    pessoa.ja_sigo=true;
                    //Aumenta os seguidores
                    pessoa.seguidores++;
                }
                TextView segueTxt = (TextView) perfil.findViewById(R.id.seguem0);
                segueTxt.setText(String.valueOf(pessoa.seguidores));
                seguir.setSelected(pessoa.ja_sigo);
            }
        });


        /**
         * Full screen the foto imageview when clicked by using a dialog
         */
        foto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                MainActivity.ShowImage(foto.getDrawable());
            }
        });

        /**
         * The same thing for the cape
         */
        capa.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                MainActivity.ShowImage(capa.getDrawable());
            }
        });

        perfil.findViewById(R.id.SeguemCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "Os seguidores são anônimos", Toast.LENGTH_SHORT).show();
            }
        });

        perfil.findViewById(R.id.SeguidosCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pessoa.username.equals(Data.getInstance().getUser().username)) {
                    DatabaseManager.followFill(pessoa.username, ctx);
                    followDialog.show();
                }else{
                    Toast.makeText(ctx, "Quem as outras pessoas seguem é anônimo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        perfil.findViewById(R.id.voltar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.hidePerfil();
            }
        });
        perfil.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowEditar();
            }
        });

        //so ths one is a hard part
        //here i declare th bottomSheet for show the editPerfil
        editPerfil = new BottomSheetDialog(getActivity());

        //########### CREATE BOTTOM SHEET E TALZ ################

        //set the BottomSheetDialog to be hte comments layout
        myBottomSheet = getActivity().getLayoutInflater().inflate(R.layout.perfil_edit, null);
        editPerfil.setContentView(myBottomSheet);

        BottomSheetBehavior myBottomBehavior = BottomSheetBehavior.from((View) myBottomSheet.getParent());
        myBottomBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, getResources().getDisplayMetrics()));

        //criar funções de editar
        //cancelar
        myBottomSheet.findViewById(R.id.cancelEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editPerfil.hide();
            }
        });
        //salvar
        myBottomSheet.findViewById(R.id.ConfirmEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap imgBitmap;
                ImageView fotoEdited, fotoNormal;
                //Set Images into the image view after puting this shit, hey me i ddont dont am making nothing hero to
                //say any shit if the image is not uploaded, so this can maybe lie to the user, but i dnt care, i hate put comments in
                //this shit, if you are reading this have a nice day :)



                //salvando as imagens
                fotoEdited = (ImageView) myBottomSheet.findViewById(R.id.FotoEdit);//salvando e mudando ft
                fotoNormal = (ImageView) perfil.findViewById(R.id.foto);
                imgBitmap = ((BitmapDrawable)fotoEdited.getDrawable()).getBitmap();
                fotoNormal.setImageBitmap(imgBitmap);
                ImageControl.upload(imgBitmap,"f"+pessoa.username+".jpg",getContext());

                fotoEdited =(ImageView) myBottomSheet.findViewById(R.id.CapaEdit);//salvando e mudando capa
                fotoNormal = (ImageView) perfil.findViewById(R.id.Fundo);
                imgBitmap = ((BitmapDrawable)fotoEdited.getDrawable()).getBitmap();
                fotoNormal.setImageBitmap(imgBitmap);
                ImageControl.upload(imgBitmap,"c"+pessoa.username+".jpg", getContext());

                TextView txt = (TextView) myBottomSheet.findViewById(R.id.nomeEditado);//mudando nome
                editedPessoa.nome= txt.getText().toString();
                txt = (TextView) myBottomSheet.findViewById(R.id.sobreEditado);//mudando sobre
                editedPessoa.sobre= txt.getText().toString();

                //TODO FAZER FUNCIONAR - ISSO LIMITA A QUANTIDADE DE LINHAS QUE SE PODE ESCREVER
                // Todo -- (para pesquisa)Set maximum number of text lines for an EditText
              /*  txt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) { }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (null != txt.getLayout() && txt.getLayout().getLineCount() > 5) {
                            //erro no .delete aqui
                            // txt.getText().delete(txt.getText().length() - 1, txt.getText().length());
                        }
                    }
                });
                */


                Picasso.with(ctx).invalidate("https://erisonmiller.000webhostapp.com/images/f"+pessoa.username+".jpg");
                Picasso.with(ctx).invalidate("https://erisonmiller.000webhostapp.com/images/c"+pessoa.username+".jpg");
                //atualiza no banco
                DatabaseManager.updatePerson(editedPessoa, getContext());
                fillPerfil(editedPessoa);
                editPerfil.hide();//esconde o editor e termina
            }
        });
        //load foto
        myBottomSheet.findViewById(R.id.editarFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewToPutImage = 0;
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, RESULT_LOAD_IMAGE);

            }
        });

        //load capa
        myBottomSheet.findViewById(R.id.editarCapa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewToPutImage = 1;
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, RESULT_LOAD_IMAGE);
            }
        });

        return perfil;
    }

    /**
     * Esconde o dialog q mostra os seguidores
     */
    static void hideFollowers(){
        followDialog.hide();
    }

    /**
     * Preenche o dialog de seguidores e o mostra
     * @param p array list de pessoa para carregar como seguidores
     */
    static void setFollows(ArrayList<Pessoa> p){
        FollowAdapter.data.clear();
        FollowAdapter.data.addAll(p);
        FollowAdapter.notifyDataSetChanged();
    }

    /**
     * Mostra o dialog de editar perfil
     */
    static void ShowEditar(){
        final ImageView foto = (ImageView) perfil.findViewById(R.id.foto);
        final ImageView capa = (ImageView) perfil.findViewById(R.id.Fundo);

        ImageView editedImage = (ImageView) myBottomSheet.findViewById(R.id.FotoEdit);
        editedImage.setImageDrawable(foto.getDrawable());
        editedImage = (ImageView) myBottomSheet.findViewById(R.id.CapaEdit);
        editedImage.setImageDrawable(capa.getDrawable());
        editPerfil.show();
    }

    /**
     * Bring the part of creat bottomSheet for here after
     * @param editPerfil
     */
    public void CreateBottomSheet(final BottomSheetDialog editPerfil){

    }

    /**
     * This is an good part to understend, this is used to recieve the result of te activit called, in this case, the load pic activity
     * this will send backk a result, so we get this result in this class and put the result (an imageUri in this case) and put on the
     * image views
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //se o resultado for ok
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            if(viewToPutImage==0){//coloca na ft
                ImageView i = (ImageView) myBottomSheet.findViewById(R.id.FotoEdit);
                i.setImageURI(imageUri);
            }
            if(viewToPutImage==1){//coloca na capa
                ImageView i = (ImageView) myBottomSheet.findViewById(R.id.CapaEdit);
                i.setImageURI(imageUri);
            }
        }
    }

    /**
     * Preenche os posts do usuário
     * @param m
     */
    static void setUserPosts(ArrayList<Marker> m){

        int size = postsAdapter.data.size();
        postsRecycler.removeAllViewsInLayout();
        postsAdapter.data.clear();
        postsAdapter.notifyItemRangeRemoved(0, size);
        postsAdapter.data.addAll(m);
        postsAdapter.notifyDataSetChanged();
    }

    /**
     * Fill the view with the data of the person p
     * @param p the person tha the perfil will show
     */
    public static void fillPerfil(Pessoa p){
        FollowAdapter.data.clear();
        // load the images
        ImageView foto =(ImageView) perfil.findViewById(R.id.foto);
        Picasso.with(ctx).load("https://erisonmiller.000webhostapp.com/images/f"+p.username+".jpg")
                .error(R.drawable.erro_loading)
                .into(foto);

        ImageView capa = (ImageView) perfil.findViewById(R.id.Fundo);
        Picasso.with(ctx).load("https://erisonmiller.000webhostapp.com/images/c"+p.username+".jpg")
                .error(R.drawable.background_city)
                .into(capa);

        pessoa = p;

        //ocultar as coisas caso não seja seu perfil
        if(pessoa.username.equals(Data.getInstance().getUser().username)){
            perfil.findViewById(R.id.seguir).setVisibility(View.GONE);
            perfil.findViewById(R.id.edit).setVisibility(View.VISIBLE);
        }else{
            perfil.findViewById(R.id.seguir).setVisibility(View.VISIBLE);
            perfil.findViewById(R.id.seguir).setSelected(pessoa.ja_sigo);
            perfil.findViewById(R.id.edit).setVisibility(View.GONE);
        }

        //put the texts by the person passed
        TextView txt =(TextView) perfil.findViewById(R.id.user_name);
        txt.setText(p.username);
        txt =(TextView) perfil.findViewById(R.id.Nome);
        txt.setText(p.nome);
        txt =(TextView) perfil.findViewById(R.id.sobre);
        txt.setText(p.sobre);
        txt =(TextView) perfil.findViewById(R.id.seguem0);
        txt.setText(String.valueOf(p.seguidores));
        txt =(TextView) perfil.findViewById(R.id.seguidos);
        txt.setText(String.valueOf(p.seguindo));

        editedPessoa = pessoa;

        //Iniciar o nome e a descrição com a padrão
        txt = (TextView) myBottomSheet.findViewById(R.id.nomeEditado);
        txt.setText(pessoa.nome);
        txt = (TextView) myBottomSheet.findViewById(R.id.sobreEditado);


        txt.setText(pessoa.sobre);
    }
}