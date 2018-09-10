package com.example.erison.mapateste4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Comment;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Control the feed
 */
public class FeedLayout extends Fragment {
    private View view;
    //get the data of the feed
    Data mApp = Data.getInstance();

    private static RecyclerView feedRecycler;
    static private RviewAdapter adapter;
    private static BottomSheetDialog dialog;
    static int commentId, commentPosi;
    private static Context ctx;
    static private CommentViewAdapter CommentsAdapter;

    private static Dialog postarDialog;

    // POST DIALOG

    /**
     * Start the feed view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = (View) inflater.inflate(R.layout.feed_tab, container,false);

        Activity act = getActivity();
        ctx = getContext();

        Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //create
        feedRecycler = (RecyclerView) view.findViewById(R.id.FeedRecycle);
        feedRecycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        Session session = new Session(ctx);
        adapter = new RviewAdapter(getActivity(), DatabaseManager.TakeMarkersFromString(session.getLastPosts()));
        //set the recycler adapter
        feedRecycler.setAdapter(adapter);
        feedRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        /// POSTAR DIALOG
        postarDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        //diz qual o layou do dalog, no caso é o full_screen_image
        postarDialog.setContentView(R.layout.add_post);

        //ocult image
        final ImageView i = (ImageView) postarDialog.findViewById(R.id.postPicture);
        i.setVisibility(View.GONE);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.setVisibility(View.GONE);
            }
        });

        final TextView postName = (TextView) postarDialog.findViewById(R.id.postTitle);
        postName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    if (postName.getText().length() < 4) {
                        postName.setError("O título precisa ter no minimo 4 caracteres");
                    } else {
                        postName.setError(null);
                    }}}});


        final TextView postTxt = (TextView) postarDialog.findViewById(R.id.postText);
        postName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    if (i.getVisibility()==View.GONE && postTxt.getText().length() < 2) {
                        postTxt.setError("Caso não poste foto é necessário postar um texto");
                    } else {
                        postTxt.setError(null);
                    }}}});

        // back post button
        postarDialog.findViewById(R.id.postBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postarDialog.hide();
            }
        });

        //post pick image button
        postarDialog.findViewById(R.id.postPickImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, 1);
            }
        });

        //posi button
        final ImageButton posi = (ImageButton) postarDialog.findViewById(R.id.postLocation);
        posi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocationManager manager = (LocationManager) ctx.getSystemService( Context.LOCATION_SERVICE );
                if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                    Toast.makeText(getContext(),"O GPS está desativado", Toast.LENGTH_SHORT).show();
                    return;}
                posi.setSelected(!posi.isSelected());
            }
        });

        postarDialog.findViewById(R.id.postSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postName.getText().length() < 4) {
                    postName.setError("O título precisa ter no minimo 4 caracteres");
                    return;
                } else {
                    postName.setError(null);
                }
                if (i.getVisibility()==View.GONE && postTxt.getText().length() < 2) {
                    postTxt.setError("Caso não poste foto é necessário postar um texto");
                    return;
                } else {
                    postTxt.setError(null);
                }

                Marker m = new Marker();
                //type
                m.type = mApp.getUser().username;
                //posi
                if(posi.isSelected()){
                    Location l = MapsLayout.myLocationStatic;
                    m.lat = l.getLatitude();
                    m.lng = l.getLongitude();
                }else{
                    m.lat = m.lng = 99999;
                }

                //foto
                Bitmap imgBitmap = ((BitmapDrawable)i.getDrawable()).getBitmap();
                if(i.getVisibility()==View.VISIBLE) {
                    m.foto = 1;
                }

                TextView txt = (TextView) postarDialog.findViewById(R.id.postTitle);
                //titulo
                m.description = txt.getText().toString();

                //texto
                txt = (TextView) postarDialog.findViewById(R.id.postText);
                m.texto = txt.getText().toString();

                //contador de chars
                //final TextView count_char = (TextView)postarDialog.findViewById(R.id.count_chars);

                //adiciona
                DatabaseManager.AddPost(m, imgBitmap,ctx);
                postarDialog.hide();
            }
        });


        view.findViewById(R.id.postar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DatabaseManager.isOnline(getContext())){
                    Toast.makeText(getContext(),"Você não pode postar estando offline", Toast.LENGTH_SHORT).show();return;}
                postarDialog.show();
            }
        });



        //set the BottomSheetDialog to be hte comments layout
        dialog = new BottomSheetDialog(getActivity());
        View myBottomSheet = getActivity().getLayoutInflater().inflate(R.layout.dialog_comment, null);
        dialog.setContentView(myBottomSheet);
        BottomSheetBehavior myBottomBehavior = BottomSheetBehavior.from((View) myBottomSheet.getParent());
        myBottomBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1000, getResources().getDisplayMetrics()));

        RecyclerView CommentsRecycle = (RecyclerView) myBottomSheet.findViewById(R.id.CommentsRecycle);
        CommentsRecycle.setNestedScrollingEnabled(false);

        ArrayList<CommentElement> cmTeste = new ArrayList<CommentElement>();

        CommentsAdapter = new CommentViewAdapter(dialog.getContext(), cmTeste);
        CommentsRecycle.setAdapter(CommentsAdapter);
        CommentsRecycle.setLayoutManager(new LinearLayoutManager(dialog.getContext()));

        final EditText comment = (EditText) myBottomSheet.findViewById(R.id.CommentText);
        //button of comments to put the comments inside the db
        myBottomSheet.findViewById(R.id.SendComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseManager.PutComment(commentId, mApp.getUser().username, comment.getText().toString(), ctx);
                //adapter.data.get(commentPosi).comments++;
                //adapter.notifyItemChanged(commentPosi);
                FeedCard f = (FeedCard) feedRecycler.findViewHolderForLayoutPosition(commentPosi);
                Marker m = f.getMarker();
                m.comments++;
                adapter.data.set(commentPosi,m);
                adapter.notifyItemChanged(commentPosi);

                comment.setText("");
                dialog.hide();
            }
        });
        myBottomSheet.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        return view;
    }

    static void AddItemOnFeed(Marker m){
        adapter.data.add(0, m);
        adapter.notifyItemInserted(0);
    }

    static void FillFeed(ArrayList<Marker> arrayList){
        /*adapter.data.clear();
        adapter.data.addAll(arrayList);
        adapter.notifyDataSetChanged();*/

        int size = adapter.data.size();
        feedRecycler.removeAllViewsInLayout();
        adapter.data.clear();
        adapter.notifyItemRangeRemoved(0, size);
        adapter.data.addAll(arrayList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Show the bottomSheet of the comments, this function can be used by the comment button of a feed card
     * he gets the id to can show the apropriate comments (and put the comments to)
     */
    static void showComments(int id, int posi){

        commentId = id;
        commentPosi = posi;
        CommentsAdapter.data.clear();
        CommentsAdapter.notifyDataSetChanged();
        DatabaseManager.LoadComments(id, ctx);
        dialog.show();

        dialog.findViewById(R.id.nuvemzinha).setVisibility(View.GONE);
        dialog.findViewById(R.id.NadaPraMostrar).setVisibility(View.GONE);
        dialog.findViewById(R.id.CommentText).setActivated(false);

        if(!DatabaseManager.isOnline(ctx)){
            dialog.findViewById(R.id.nuvemzinha).setVisibility(View.VISIBLE);
            dialog.findViewById(R.id.CommentText).setActivated(false);
        }
    }

    static void setComments(ArrayList<CommentElement> commentElementArrayList){
        if(commentElementArrayList.size()==0){
            dialog.findViewById(R.id.NadaPraMostrar).setVisibility(View.VISIBLE);
        }
        CommentsAdapter.data.addAll(commentElementArrayList);
        CommentsAdapter.notifyDataSetChanged();
    }

    static void hideComments(){
        dialog.hide();
    }

    /**
     * I have user this shit on perfil
     * so i wil don't explain here
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //se o resultado for ok
        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            ImageView i = (ImageView) postarDialog.findViewById(R.id.postPicture);
            i.setVisibility(View.VISIBLE);
            i.setImageURI(imageUri);
        }
    }
}
