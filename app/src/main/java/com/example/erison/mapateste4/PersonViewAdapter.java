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

import java.util.ArrayList;

/**
 * View adapter para seguidores
 * (Pode extender para outras partes que usem uma pessoa)
 */

public class PersonViewAdapter extends RecyclerView.Adapter<PersonCard> {

    private LayoutInflater inflater;
    ArrayList<Pessoa> data = new ArrayList<>();
    private Context context;

    public PersonViewAdapter(Context context, ArrayList<Pessoa> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public PersonCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.person_follow_card, parent, false);

        PersonCard holder = new PersonCard(view, context);


        return holder;
    }

    @Override
    public void onBindViewHolder(PersonCard holder, int position) {
        Pessoa actual = data.get(position);

        Picasso.with(context).load("https://erisonmiller.000webhostapp.com/images/f"+actual.username+".jpg")
                .error(R.drawable.erro_loading)
                .into(holder.icon);

        holder.description.setText(actual.nome);
        holder.cardUsername.setText(actual.username);

        holder.initi(actual,Data.getInstance().getUser().username);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}


/**
 * this class is to control the person card that is used in the follow area and can be used in others later
 */
class PersonCard extends RecyclerView.ViewHolder{
    ImageView icon;
    Button follow;
    TextView description, cardUsername;
    private Context context;
    private Pessoa pessoa;
    String ActualUser;

    /**
     * constructor
     * @param itemView
     * @param context
     */
    public PersonCard(final View itemView, final Context context) {

        super(itemView);
        this.context = context;
        //inicia os itens do card
        icon = (ImageView) itemView.findViewById(R.id.CardImage);

        description = (TextView) itemView.findViewById(R.id.CardDescription1);
        cardUsername = (TextView) itemView.findViewById(R.id.Cardusername);

        follow = (Button) itemView.findViewById(R.id.followButton);
        follow.setSelected(true);

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.follow(ActualUser, pessoa.username, context);
                follow.setSelected(!follow.isSelected());
                if(follow.isSelected()){
                    Toast.makeText(context, "Seguindo", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Parou de seguir", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * it is just to start the person card
     * (name, picture, username and follow button)
     * @param p
     */
    public void initi(final Pessoa p, String username){
        pessoa = p;
        ActualUser = username;

        itemView.findViewById(R.id.CardDescription1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showPerfil(p.username);
                Perfil.hideFollowers();
            }
        });
    }


}
