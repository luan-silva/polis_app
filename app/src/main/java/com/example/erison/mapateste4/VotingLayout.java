package com.example.erison.mapateste4;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * View Controller para o Layout de votação
 */

public class VotingLayout extends Fragment {
    private View view;
    private RecyclerView vottingRecycler;
    private static VottingViewAdapter adapter;
    private static Context ctx;


    //votações passadas
    private static Dialog oldVottingDialog;//the dialog of fullScreen
    private RecyclerView OldVottingRecycler;
    private static VottingViewAdapter OldVotsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = (View) inflater.inflate(R.layout.voting_layout, container,false);

        ctx = getContext();

        final String username = Data.getInstance().getUser().username;
        DatabaseManager.VottingFill(username,ctx);

        view.findViewById(R.id.showOldVotation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.VottingOldFill(username, ctx);
                oldVottingDialog.show();
            }
        });

        //create
        vottingRecycler = (RecyclerView) view.findViewById(R.id.VottingRecycler);
        vottingRecycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        ArrayList<Voto> data = new ArrayList<>();
        adapter = new VottingViewAdapter(getActivity(), data);
        //set the recycler adapter
        vottingRecycler.setAdapter(adapter);
        vottingRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));


        //########### OLD VOTATIONS DIALOG ##########
        //cria o dialog
        oldVottingDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        //diz qual o layou do dalog, no caso é o full_screen_image
        oldVottingDialog.setContentView(R.layout.follow_dialog);
        OldVottingRecycler = (RecyclerView) oldVottingDialog.findViewById(R.id.followRecycle);
        OldVottingRecycler.setNestedScrollingEnabled(false);
        //past the data to the adapter
        ArrayList<Voto> data1 = new ArrayList<>();
        OldVotsAdapter = new VottingViewAdapter(getActivity(), data1);
        //set the recycler adapter
        OldVottingRecycler.setAdapter(OldVotsAdapter);
        OldVottingRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextView txt = (TextView) oldVottingDialog.findViewById(R.id.TolbarName);
        txt.setText("Votações anteriores");
        oldVottingDialog.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldVottingDialog.hide();
            }
        });



        return view;
    }

    /**
     * Fill the vots recycler view using the arrayList
     * preenche só com os votos em aberto
     * @param arrayList
     */
    static void fillVotos(ArrayList<Voto> arrayList){
        adapter.data.clear();
        adapter.data.addAll(arrayList);
        adapter.notifyDataSetChanged();
    }


    /**
     * Fill the vots recycler view using the arrayList
     * preenche só com os votos que já foram decididos na camara
     * @param arrayList
     */
    static void fillOldVotos(ArrayList<Voto> arrayList){
        OldVotsAdapter.data.clear();
        OldVotsAdapter.data.addAll(arrayList);
        OldVotsAdapter.notifyDataSetChanged();
    }
}
