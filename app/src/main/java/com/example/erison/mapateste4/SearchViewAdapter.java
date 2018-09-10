package com.example.erison.mapateste4;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Vew adapter de pesquisa
 */

public class SearchViewAdapter extends RecyclerView.Adapter<searchItemView> {

    private LayoutInflater inflater;
    ArrayList<SearchItem> data = new ArrayList<>();
    private Context context;

    public SearchViewAdapter(Context context, ArrayList<SearchItem> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public searchItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_element, parent, false);

        searchItemView holder = new searchItemView(view, context);


        return holder;
    }

    @Override
    public void onBindViewHolder(searchItemView holder, int position) {
        SearchItem actual = data.get(position);

        if(actual.type==0){
            Picasso.with(context).load("https://erisonmiller.000webhostapp.com/images/f"+actual.subNome+".jpg")
                    .error(R.drawable.erro_loading)
                    .into(holder.icon);
        }else {
            holder.icon.setImageResource(R.drawable.ic_place_white_24dp);
        }

        holder.nome.setText(actual.name);
        holder.subNome.setText(actual.subNome);

        holder.initi(actual);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}


/**
 * this class is to control the search item
 */
class searchItemView extends RecyclerView.ViewHolder{
    ImageView icon;
    TextView nome, subNome;
    private Context context;
    private SearchItem searchItem;

    /**
     * constructor
     * @param itemView
     * @param context
     */
    public searchItemView(final View itemView, final Context context) {

        super(itemView);
        this.context = context;
        //inicia os itens do card
        icon = (ImageView) itemView.findViewById(R.id.CardImage);

        nome = (TextView) itemView.findViewById(R.id.CardDescription1);
        subNome = (TextView) itemView.findViewById(R.id.Cardusername);

    }

    /**
     * it is just to start the search item
     * @param s
     */
    public void initi(final SearchItem s){
        searchItem = s;

        itemView.findViewById(R.id.goToSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.hideSearch();
                if(searchItem.type==0){
                    MainActivity.showPerfil(searchItem.subNome);
                }else{
                    MapsLayout.GoToLocationZoom(searchItem.lat,searchItem.lng,17);
                    MainActivity.tabLayout.getTabAt(1).select();
                }
            }
        });

    }
}
