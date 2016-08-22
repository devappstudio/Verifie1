package com.devappstudio.verifie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 8/1/16.
 */

public class NearByAdaptor extends RecyclerView.Adapter<NearByAdaptor.MyViewHolder> {


    private List<NearBy> nearByList;
    static private Context context;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public CircleImageView img;
        public ImageButton imb;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            img = (CircleImageView) view.findViewById(R.id.thumbnail);
            imb = (ImageButton) view.findViewById(R.id.requestRollar);
        }
    }


    public NearByAdaptor(List<NearBy> nearByList, Context context) {
        this.nearByList = nearByList;
        NearByAdaptor.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearby_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NearBy nearBy = nearByList.get(position);
        Picasso.with(context).load(nearBy.getImage_url()).into(holder.img);

        holder.name.setText(nearBy.getName());
        holder.imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO SEND GCM TO
               System.out.print(position+" Clicked For Request For "+nearBy.getTelephone_number()+" -- "+nearBy.getServer_id());
            }
        });
    }

    @Override
    public int getItemCount() {
        return nearByList.size();
    }


}
