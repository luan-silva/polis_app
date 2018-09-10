package com.example.erison.mapateste4;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;

/**
 * Controla o card de comentario
 */
class PersonComment extends RecyclerView.ViewHolder implements View.OnClickListener {
    ImageView icon;
    TextView comment, CommentInfo;
    Button CommentName;
    private Context context;
    private CommentElement c;

    public PersonComment(View itemView, Context context) {

        super(itemView);
        this.context = context;
        itemView.setOnClickListener(this);

        icon = (ImageView) itemView.findViewById(R.id.CardImage);
        CommentInfo = (TextView) itemView.findViewById(R.id.CommentInfo);
        comment = (TextView) itemView.findViewById(R.id.comment);


        CommentName = (Button) itemView.findViewById(R.id.CommentName);

        CommentName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.showPerfil(c.userName);
                FeedLayout.hideComments();
            }
        });

    }


    public void initi(final CommentElement c){
        this.c = c;

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showPerfil(c.userName);
                FeedLayout.hideComments();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context, "clicou :) #" + this.getLayoutPosition(), Toast.LENGTH_SHORT).show();
    }
}


/**
 * Controla a recycleview dos comentários
 */

public class CommentViewAdapter extends RecyclerView.Adapter<PersonComment>{

    private LayoutInflater inflater;
    ArrayList<CommentElement> data = new ArrayList<>();
    private Context context;

    public CommentViewAdapter(Context context, ArrayList<CommentElement> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public PersonComment onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.person_comment, parent, false);

        PersonComment holder = new PersonComment(view, context);

        return holder;
    }

    @Override
    public void onBindViewHolder(PersonComment holder, int position) {
        CommentElement actual = data.get(position);

        Picasso.with(context).load("https://erisonmiller.000webhostapp.com/images/f"+actual.id_user+".jpg")
                .error(R.drawable.erro_loading)
                .into(holder.icon);

        holder.comment.setText(actual.text);
        holder.CommentInfo.setText(actual.id_user);

        holder.CommentName.setText(actual.userName);
        holder.initi(actual);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
