package com.example.erison.mapateste4;

/**
 * Created by Erison on 01/06/2017.
 */


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * View adapter para votos
 */

public class VottingViewAdapter extends RecyclerView.Adapter<VotoViewCard> {

    private LayoutInflater inflater;
    ArrayList<Voto> data = new ArrayList<>();
    private Context context;

    public VottingViewAdapter(Context context, ArrayList<Voto> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public VotoViewCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.voto, parent, false);

        VotoViewCard holder = new VotoViewCard(view, context);


        return holder;
    }

    @Override
    public void onBindViewHolder(VotoViewCard holder, int position) {
        Voto actual = data.get(position);


        Picasso.with(context).load("https://erisonmiller.000webhostapp.com/images/v"+actual.id+".jpg")
                .error(R.drawable.erro_loading)
                .into(holder.icon);

        holder.description.setText(actual.descrição);
        holder.subDescription.setText(actual.nome);
        holder.CardText.setText(actual.text);
        holder.CardData.setText(actual.data);

        holder.initi(actual);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}


/**
 * this class is to control the Vote card, he just start the voto with the corrects params, that he gets from the data
 */
class VotoViewCard extends RecyclerView.ViewHolder {
    ImageView icon;
    TextView subDescription, CardText,CardData, description;
    FloatingActionButton SimButton, NaoButton, NeutroButton;
    private Context context;
    private Voto voto;
    private CardView card;
    private PieChart pieChart;
    private View view;
    private Dialog confirmVote;
    /**
     * constructor
     * @param itemView
     * @param context
     */
    public VotoViewCard(final View itemView, final Context context) {

        super(itemView);
        this.context = context;
        view = itemView;
        //inicia os itens do card
        icon = (ImageView) itemView.findViewById(R.id.CardImage);
        subDescription = (TextView) itemView.findViewById(R.id.CardSubDescription);
        CardText = (TextView) itemView.findViewById(R.id.CardText);
        CardData= (TextView) itemView.findViewById(R.id.cardData);

        description = (TextView) itemView.findViewById(R.id.CardDescription);
        SimButton = (FloatingActionButton) itemView.findViewById(R.id.Aprova);
        NaoButton = (FloatingActionButton) itemView.findViewById(R.id.Desaprova);
        NeutroButton = (FloatingActionButton) itemView.findViewById(R.id.Neutro);

        SimButton.hide();
        NaoButton.hide();
        NeutroButton.hide();


        card = (CardView)  itemView.findViewById(R.id.card);

        pieChart = (PieChart) itemView.findViewById(R.id.popChart);


        //confirme vote dialog
        confirmVote = new Dialog(context,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        //diz qual o layou do dalog, no caso é o full_screen_image
        confirmVote.setContentView(R.layout.confirm_vote);

        //description equals to the name in the card
        //set on the name clicked action
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+voto.link));
                context.startActivity(i);
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card.setSelected(!card.isSelected());
                if(card.isSelected()){
                    CardText.setMaxLines(20);
                    SimButton.show();
                    NaoButton.show();
                    NeutroButton.show();
                }else{
                    CardText.setMaxLines(3);
                    SimButton.hide();
                    NaoButton.hide();
                    NeutroButton.hide();
                }
            }
        });

        //Votos
        confirmVote.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmVote.hide();
            }
        });
        NeutroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDialog(0,"#616161", "Neutro");
            }
        });
        SimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDialog(1,"#9C27B0", "A favor");
            }
        });
        NaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDialog(2,"#F44336", "contra");
            }
        });

    }

    /**
     * Mostra o dialogo de confirmaçaõ da reação
     * @param react
     * @param cor
     * @param texto
     */
    final void SetDialog(final int react, String cor, String texto){

        confirmVote.show();
        TextView txt = (TextView) confirmVote.findViewById(R.id.confirmeText);
        txt.setText(texto);
        txt.setTextColor(Color.parseColor(cor));

        ImageView img = (ImageView) confirmVote.findViewById(R.id.confirmIcon);
        img.setImageResource(R.drawable.like0+react);

        confirmVote.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.Votar(voto.id,Data.getInstance().getUser().username, react, context);
                voto.ja_votou = true;
                Votou(react);
                confirmVote.hide();
            }
        });
    }

    /**
     * Modifica a view caso o usuario já tenha votado ou não
     * @param react
     */
    final void Votou(int react){
        view.findViewById(R.id.reactions).setVisibility(View.GONE);
        view.findViewById(R.id.votouLayout).setVisibility(View.VISIBLE);
        ImageView img = (ImageView) view.findViewById(R.id.meuVotoAgora);
        img.setImageResource(R.drawable.like0+react);
    }

    /**
     * Povoa o Grafico de votação com os dados passsados na variavel voto
     */
    private void PopulateChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(voto.peopleS, "Sim"));
        pieEntries.add(new PieEntry(voto.peopleN, "Não"));
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(new int[]{R.color.colorPrimaryLigth , R.color.colorRed500} , context);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(R.color.colorGrayMedium);

        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawSliceText(false);
        pieChart.animateY(500);
        pieChart.invalidate();
    }

    /**
     * it is just to start the likes button putting the rigth colors if is actived or not
     * @param v
     */
    public void initi(Voto v){
        voto = v;

        view.findViewById(R.id.votouLayout).setVisibility(View.GONE);
        if(v.ja_votou) {
            if(v.decision!=-1) {
                PopulateChart();
                SetIcons();
                view.findViewById(R.id.reactions).setVisibility(View.GONE);
                view.findViewById(R.id.feedBack).setVisibility(View.VISIBLE);
            }else{
                Votou(v.reaction);
            }
        }else{
            view.findViewById(R.id.feedBack).setVisibility(View.GONE);
            if(v.reaction!=-1){
                Votou(v.reaction);
            }else {
                view.findViewById(R.id.reactions).setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Coloca o item dependendo do voto do usuario
     */
    private void SetIcons() {
        //0 = Neutro
        //1 = Sim
        //2 = Não
        ImageView img = (ImageView) view.findViewById(R.id.meuVoto);
        img.setImageResource(R.drawable.like0+voto.reaction);
        img = (ImageView) view.findViewById(R.id.camVoto);
        img.setImageResource(R.drawable.like0+voto.decision);
    }

}