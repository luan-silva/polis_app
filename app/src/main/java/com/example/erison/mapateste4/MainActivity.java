package com.example.erison.mapateste4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import static android.view.View.GONE;

/**
 * Activity principal
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private PerfilPagerAdapter perfilSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static ViewPager perfilViewPager;
    public static TabLayout tabLayout;
    Data mApp = Data.getInstance();

    // DRAWABLE
    Toolbar toolbar;

    private Session session;

    static private Context ctx;

    private static Dialog fullScreenImage;//the dialog of fullScreen


    private RecyclerView searchRecycler;
    static private SearchViewAdapter searchAdapter;
    private static NestedScrollView SearchList;//the dialog of fullScreen

    static private MapsLayout map;
    /**
     * Começa o aplicativo como main
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //recebe  a seção atual
        session = new Session(this);


        mApp.setUser(session.getLogedPerson());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = getApplication();

        map = new MapsLayout();
        map.onCreate(savedInstanceState);

        mApp.getData().clear();
        mApp.getData().addAll(DatabaseManager.TakeMarkersFromString(session.getLastPosts()));


        //##################### SEARCH BOX DIALOG ##############################
        SearchList = (NestedScrollView) findViewById(R.id.ViewSearch);
        SearchList.setVisibility(GONE);

        searchRecycler = (RecyclerView) findViewById(R.id.searchView);
        searchRecycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        ArrayList<SearchItem> data = new ArrayList<>();
        searchAdapter = new SearchViewAdapter(this, data);
        //set the recycler adapter
        searchRecycler.setAdapter(searchAdapter);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));




        //Cria o dialog de full screen, que é nada mais doq uma imageView q ocupa a tela toda
        //cria o dialog
        fullScreenImage = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
        //diz qual o layou do dalog, no caso é o full_screen_image
        fullScreenImage.setContentView(R.layout.full_screen_image);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);


        //   Perfil viewPage
        perfilSectionsPagerAdapter = new PerfilPagerAdapter(getSupportFragmentManager());
        perfilViewPager = (ViewPager) findViewById(R.id.perfilPage);
        perfilViewPager.setAdapter(perfilSectionsPagerAdapter);



        //    TABS
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(1).select();

        int[] arr_drawable = {R.drawable.feed_icon, R.drawable.mapa_icon, R.drawable.voting_icon};
        String[] nomes = {"Feed","Mapa","Votar"};
        tabLayout.getTabAt(0).select();
        //put the icons of the test data
        for (int i = 0; i < 3; i++) {
            tabLayout.getTabAt(i).setIcon(arr_drawable[i]);
            tabLayout.getTabAt(i).setText(nomes[i]);
        }

        //set the tollbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        perfilViewPager.setVisibility(View.GONE);

        //########### CREATE THE DRAWABLE ################
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView ft = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.drawerFoto);

        // carrega as imagens
        Picasso.with(ctx).load("https://erisonmiller.000webhostapp.com/images/f"+mApp.getUser().username+".jpg")
                .error(R.drawable.background_city)
                .into(ft);

        ft = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.drawerCapa);
        Picasso.with(ctx).load("https://erisonmiller.000webhostapp.com/images/c"+mApp.getUser().username+".jpg")
                .error(R.drawable.background_city)
                .into(ft);

    }


    /**
     * Povoa a view de pesquisa
     * @param list
     */
    static void FillSearch(ArrayList<SearchItem> list){
        searchAdapter.data.addAll(list);
        searchAdapter.notifyDataSetChanged();
    }

    /**
     * Oculta a view de pesquisa
     */
    static void hideSearch(){
        SearchList.setVisibility(View.GONE);
    }

    /**
     * Mostra uma imagem em um dialog
     * @param imagem
     */
    static void ShowImage(Drawable imagem){
        //faz a imagem do diallog receber a imagem q eu quero mostrar
        ImageView full_image = (ImageView) fullScreenImage.findViewById(R.id.image_to_full_screen);
        full_image.setImageDrawable(imagem);
        //Torna o dialog visivel
        fullScreenImage.show();
    }

    /**
     * Ações do botão voltar
     */
    public void onBackPressed() {

        if(perfilViewPager.getVisibility()==View.VISIBLE){
            perfilViewPager.setVisibility(View.GONE);
            return;
        }
        if(SearchList.getVisibility() == View.VISIBLE){
            SearchList.setVisibility(View.GONE);
            return;
        }
        if(tabLayout.getSelectedTabPosition()!=0){
            tabLayout.getTabAt(0).select();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }


    /**
     * Povoa o Toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem( R.id.search_settings);
        SearchView search = (SearchView)item.getActionView();


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty() || !DatabaseManager.isOnline(ctx)){
                    SearchList.setVisibility(View.GONE);
                }else{
                    SearchList.setVisibility(View.VISIBLE);
                    searchAdapter.data.clear();
                    DatabaseManager.SearchPerson(newText,getApplicationContext());
                    MapsLayout.getSearchPlaceList(newText, getApplicationContext());
                }

                return false;
            }
        });

        return true;
    }

    /**
     * Diz qual vai ser as ações dos botões do Toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar Item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(Gravity.LEFT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * PagerAdapter das abas de Feed, Mapa e Votos
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        //control tabs
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                //Feed
                case 0:
                    return new FeedLayout();
                //Map
                case 1:
                    return map;
                //Votos
                case 2:
                    return new VotingLayout();
            }
            //oi
            return new FeedLayout();

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 3;
        }
    }

    /**
     * PagerAdapter pro perfiil
     */
    public class PerfilPagerAdapter extends FragmentPagerAdapter {

        public PerfilPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        //control tabs
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new Perfil();
            }
            //oi
            return new Perfil();

        }

        @Override
        public int getCount() {
            // Show 1 total pages.
            return 1;
        }
    }


    /**
     * Mostra o perfil
     */
    static void showPerfil(String user){
        perfilViewPager.setVisibility(View.VISIBLE);
        DatabaseManager.getPerson(user, ctx);
        DatabaseManager.FillDataFromPerson(user, ctx);
    }

    /**
     * Mostra o editar perfil
     */
    static void showEditarPerfil(String user){
        perfilViewPager.setVisibility(View.VISIBLE);
        Perfil.ShowEditar();
    }

    /**
     * Esconde o perfil
     */
    static void hidePerfil(){
        perfilViewPager.setVisibility(View.GONE);
    }



    // ############# CONFIGURAÇÕES ###################

    /**
     * Drawer da aplicação
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ir_perfil) {
            showPerfil(Data.getInstance().getUser().username);
        } else if (id == R.id.nav_editar_senha) {
            final Dialog telaEditarSenha = new Dialog(this,R.style.AppTheme);
            telaEditarSenha.setContentView(R.layout.editarsenha);

            final EditText edit_senhaAtual = (EditText) telaEditarSenha.findViewById(R.id.senhaAtualid);
            final EditText edit_novaSenha = (EditText) telaEditarSenha.findViewById(R.id.novaSenhaid);
            final EditText edit_confirmarSenha = (EditText) telaEditarSenha.findViewById(R.id.confirmarSenhaid);
            final Button edit_alterarSenha = (Button) telaEditarSenha.findViewById(R.id.botaoAlterarid);
            final TextInputLayout senhaAtual = (TextInputLayout) telaEditarSenha.findViewById(R.id.senhaAtualLayoutid);
            final TextInputLayout novaSenha = (TextInputLayout) telaEditarSenha.findViewById(R.id.novaSenhaLayoutid);
            final TextInputLayout confirmarSenha = (TextInputLayout) telaEditarSenha.findViewById(R.id.confirmarSenhaLayoutid);

            telaEditarSenha.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    telaEditarSenha.hide();
                }
            });

            edit_alterarSenha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edit_senhaAtual.getText().toString().length()<6 ) {
                        //edit_senhaAtual.requestFocus();
                        senhaAtual.setError("Senha inválida");
                    }
                    else if(edit_novaSenha.getText().toString().length()<6){
                        //edit_novaSenha.requestFocus();
                        novaSenha.setError("Senha inválida");
                    }

                    else if(edit_confirmarSenha.getText().toString().length()<6){
                        //edit_confirmarSenha.requestFocus();
                        confirmarSenha.setError("Senha não confirmada");
                    }
                    else if(!edit_novaSenha.getText().toString().equals(edit_confirmarSenha.getText().toString())){
                        //edit_novaSenha.requestFocus();
                        novaSenha.setError("As senhas não correspondem");
                    }
                    else{
                        DatabaseManager.updateSenha(Data.getInstance().getUser().username, edit_novaSenha.getText().toString(), edit_senhaAtual.getText().toString(), ctx);
                        telaEditarSenha.dismiss();
                    }

                }
            });
            telaEditarSenha.show();

        } else if (id == R.id.nav_apagar_conta) {
            Alerta_apagar_conta();
        } else if (id == R.id.nav_sobre) {
            final Dialog telaSobre = new Dialog(this,R.style.AppTheme);
            telaSobre.setContentView(R.layout.sobre);
            telaSobre.show();
            //evento de clique do botão voltar da tela Sobre
            telaSobre.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    telaSobre.hide();
                }
            });

        } else if (id == R.id.nav_sair) {
            Alerta_sair();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /**
     * método para a opção sair das configurações
     * (fazer logout)
     */
    public void logout(){
        session.setLoggedOut();
        this.finish();
        startActivity(new Intent(ctx,tela_login.class));
    }

    /**
     * dialog para apagar a conta
     */
    private void Alerta_apagar_conta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta");
        builder.setMessage("Você realmente quer apagar a sua conta?");
        builder.setPositiveButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseManager.ApagarConta(session.getLogedPerson().username, ctx);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alerta!");
        builder.setMessage("Você realmente quer sair da aplicação?");
        builder.setPositiveButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                logout();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}