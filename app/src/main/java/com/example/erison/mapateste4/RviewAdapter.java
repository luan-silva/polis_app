package com.example.erison.mapateste4;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * this class is to control the feed card, he just start the feed_card with the corrects params, that he gets from the data
 */
class FeedCard extends RecyclerView.ViewHolder{
    ImageView icon, postFoto, posiViewActived;
    TextView subDescription, upVotes, downVotes, shares, comments, feedPubText, description, date;
    FloatingActionButton PutComment, topButton, downButton;
    private Context context;
    private Marker m;

    /**
     * constructor
     * @param itemView
     * @param context
     */
    public FeedCard(final View itemView, final Context context) {

        super(itemView);
        this.context = context;
        //inicia os itens do card
        icon = (ImageView) itemView.findViewById(R.id.CardImage);
        posiViewActived = (ImageView) itemView.findViewById(R.id.posiCardActivated);
        postFoto = (ImageView) itemView.findViewById(R.id.FeedPubImage);

        postFoto.setVisibility(View.GONE);
        posiViewActived.setImageResource(R.drawable.ic_place_white_24dp);


        subDescription = (TextView) itemView.findViewById(R.id.CardSubDescription);
        upVotes = (TextView) itemView.findViewById(R.id.UpVotes);
        downVotes = (TextView) itemView.findViewById(R.id.DownVotes);
        shares = (TextView) itemView.findViewById(R.id.Shares);
        comments = (TextView) itemView.findViewById(R.id.Coments);
        feedPubText = (TextView) itemView.findViewById(R.id.FeedPubText);
        description = (TextView) itemView.findViewById(R.id.CardDescription1);
        date = (TextView) itemView.findViewById(R.id.cardDate);

        PutComment = (FloatingActionButton) itemView.findViewById(R.id.Neutro);
        topButton = (FloatingActionButton) itemView.findViewById(R.id.Aprova);
        downButton = (FloatingActionButton) itemView.findViewById(R.id.downButton);

        postFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.ShowImage(postFoto.getDrawable());
            }
        });


        //description equals to the name in the card
        //set on the name clicked action
        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsLayout.GoToLocationZoom(m.lat,m.lng, 18);
                MainActivity.tabLayout.getTabAt(1).select();
                MainActivity.hidePerfil();
            }
        });
        //show the comments bottomSheet
        PutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedLayout.showComments(m.id, getLayoutPosition());
            }
        });
        //share
        itemView.findViewById(R.id.shareButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DatabaseManager.isOnline(context)){
                    Toast.makeText(context, "Você está offline", Toast.LENGTH_SHORT).show();
                }else {
                    //deve abrir dialog
                    //todo fzr um dialog q chama isso se for confirmado
                    m.shares++;
                    shares.setText(String.valueOf(m.shares));
                    DatabaseManager.SharePost(m.id, context);
                    Toast.makeText(context, "Publicação compartilhada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TOPPER
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DatabaseManager.isOnline(context)){
                    Toast.makeText(context, "Você está offline", Toast.LENGTH_SHORT).show();
                }else {
                    //1 pra like
                    DatabaseManager.like(m.id, 1, context);

                    changeLike();
                    if (downButton.isSelected()) {
                        changeDeslike();
                    }
                }
            }
        });
        //Não TOPPER
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DatabaseManager.isOnline(context)){
                    Toast.makeText(context, "Você está offline", Toast.LENGTH_SHORT).show();
                }else {
                    //0 pra deslike
                    DatabaseManager.like(m.id, 0, context);

                    changeDeslike();
                    if (topButton.isSelected()) {
                        changeLike();
                    }
                }

            }
        });
    }

    /**
     * it is just to start the likes button putting the rigth colors if is actived or not
     * @param m
     */
    public void initi(final Marker m){
        this.m=m;
        if(m.react==0){
            m.downVotes--;
            changeDeslike();
        }
        if(m.react==1){
            m.upVotes--;
            changeLike();
        }

        if(m.lat<-200 || m.lat>200){
            posiViewActived.setImageResource(R.drawable.ic_person_pin_white_24dp);
            description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.showPerfil(m.type);
                }
            });
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showPerfil(m.type);
            }
        });
    }

    /**
     * change the like stats and colors
     * ## this class is incomplet
     */
    void changeLike(){
        TextView vUps = (TextView) itemView.findViewById(R.id.UpVotes);
        if(!topButton.isSelected()) {
            topButton.setSelected(true);
            m.upVotes++;
            vUps.setText(String.valueOf(m.upVotes));

            topButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                topButton.setImageTintList(ContextCompat.getColorStateList(context, android.R.color.white));
            }
        }else{
            topButton.setSelected(false);
            m.upVotes--;
            vUps.setText(String.valueOf(m.upVotes));

            topButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorGrayLight));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                topButton.setImageTintList(ContextCompat.getColorStateList(context, R.color.colorGrayMedium));
            }
        }
    }


    /**
     * change the dislike stats and colors
     * ## this class is incomplet
     */
    void changeDeslike(){
        TextView vDown = (TextView) itemView.findViewById(R.id.DownVotes);

        if(!downButton.isSelected()) {
            downButton.setSelected(true);
            m.downVotes++;
            vDown.setText(String.valueOf(m.downVotes));

            downButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorRed500));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                downButton.setImageTintList(ContextCompat.getColorStateList(context, android.R.color.white));
            }
        }else{
            downButton.setSelected(false);
            m.downVotes--;
            vDown.setText(String.valueOf(m.downVotes));

            downButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorGrayLight));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                downButton.setImageTintList(ContextCompat.getColorStateList(context, R.color.colorGrayMedium));
            }
        }
    }

    public void addComment(){
        m.comments++;
        comments.setText(String.valueOf(m.upVotes));
    }
    public Marker getMarker(){
        return m;
    }
}

/**
 * Created by Erison on 25/03/2017.
 */

public class RviewAdapter extends RecyclerView.Adapter<FeedCard> {

    private LayoutInflater inflater;
    ArrayList<Marker> data = new ArrayList<>();
    private Context context;

    public RviewAdapter(Context context, ArrayList<Marker> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public FeedCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_card, parent, false);

        FeedCard holder = new FeedCard(view, context);


        return holder;
    }

    @Override
    public void onBindViewHolder(FeedCard holder, int position) {
        Marker actual = data.get(position);

        Picasso.with(context).load("https://erisonmiller.000webhostapp.com/images/f"+actual.type+".jpg")
                .error(R.drawable.erro_loading)
                .into(holder.icon);

        holder.description.setText(actual.description);
        holder.subDescription.setText("@"+actual.type);
        holder.date.setText(actual.subDescription);
        holder.upVotes.setText(String.valueOf(actual.upVotes));
        holder.downVotes.setText(String.valueOf(actual.downVotes));
        holder.comments.setText(String.valueOf(actual.comments));
        holder.shares.setText(String.valueOf(actual.shares));
        holder.feedPubText.setText(actual.texto);

        if(actual.foto==1){
            Picasso.with(context).load("https://erisonmiller.000webhostapp.com/images/p"+actual.id+".jpg")
                    .error(R.drawable.erro_loading)
                    .into(holder.postFoto);
            holder.postFoto.setVisibility(View.VISIBLE);
        }

        holder.initi(actual);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
