package com.devappstudio.verifie;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import com.squareup.picasso.Downloader;
import datastore.Api;
import datastore.ReceivedRequests;
import datastore.SentRequests;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 8/1/16.
 */

public class NearByAdaptor extends RecyclerView.Adapter<NearByAdaptor.MyViewHolder> {


    private List<NearBy> nearByList;
    static private Context context;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,screen;
        public CircleImageView img;
        public Button imb;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            screen = (TextView) view.findViewById(R.id.screen_name);
            img = (CircleImageView) view.findViewById(R.id.thumbnail);
            imb = (Button) view.findViewById(R.id.requestRollar);
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
    static String phone ;

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final NearBy nearBy = nearByList.get(position);
        if(is_local(nearBy.getLocal_id()))
        {
            phone = nearBy.getTelephone_number();
            try {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber pn = null;

                pn = pnu.parse(phone, "GH");
                phone = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

            } catch (NumberParseException e) {
                e.printStackTrace();
            }

            // check if the user is registered on verifie or not
            if(nearBy.getOn_verifie().equalsIgnoreCase("0"))
            {
                //Not On

                holder.name.setText(nearBy.getName());
                holder.screen.setText(phone);
                holder.imb.setText("Invite");
                holder.img.setImageResource(R.drawable.circular);
                holder.imb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO SEND GCM TO
                        System.out.print(position+" Clicked For Request For "+phone+" -- "+nearBy.getServer_id());
                    }
                });

            }
            else
            {
                if(!nearBy.getOn_verifie().equalsIgnoreCase("0") && !nearBy.getScreen_name().equalsIgnoreCase(""))
                    Picasso.with(context)
                            .load(Api.getImage_end()+nearBy.getServer_id())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .into(holder.img, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    // Try again online if cache failed
                                    Picasso.with(context)
                                            .load(Api.getImage_end()+nearBy.getServer_id())
                                            .placeholder(R.drawable.circular)
                                            .error(R.drawable.circular)
                                            .into(holder.img);
                                }
                            });
                holder.name.setText(nearBy.getName());
                holder.imb.setText(get_button(nearBy.getServer_id()));
                holder.screen.setText(nearBy.getScreen_name()+" - "+phone);
                holder.imb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO SEND GCM TO
                        System.out.print(position+" Clicked For Request For "+phone+" -- "+nearBy.getServer_id());
                    }
                });
            }
        }
        else
        {
            Picasso.with(context)
                    .load(Api.getImage_end()+nearBy.getServer_id())
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(holder.img, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            // Try again online if cache failed
                            Picasso.with(context)
                                    .load(Api.getImage_end()+nearBy.getServer_id())
                                    .placeholder(R.drawable.circular)
                                    .error(R.drawable.circular)
                                    .into(holder.img);
                        }
                    });
            holder.name.setText(nearBy.getName());
            holder.screen.setText(nearBy.getScreen_name());
            holder.imb.setText(get_button(nearBy.getServer_id()));
            holder.imb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO SEND GCM TO
                    System.out.print(position+" Clicked For Request For "+nearBy.getTelephone_number()+" -- "+nearBy.getServer_id());
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return nearByList.size();
    }

    String get_button(String server_id)
    {
        String text="Request";

        //realm.where(ReceivedRequests.class).contains("",server_id).findAll().isEmpty()
        if(Select.from(ReceivedRequests.class).where(Condition.prop("idsend").eq(server_id)).list().isEmpty())
        {
            //we have not received request from this user
            //lets check if we have sent to user
            //realm.where(SentRequests.class).contains("",server_id).findAll().isEmpty()
            if(Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).list().isEmpty())
            {
                // we havent sent yet
                text = "Request";
            }
            else
            {
                //we have sent so lets check the result
                //realm.where(SentRequests.class).contains("",server_id).findAll().first().getReply
                if( Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).first().getReply() == 1)
                {
                    //accepted
                    text = "View RotaBar";

                }
                else
                {
                    //request denied
                    text = "Request Denied";
                    if (Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).first().getTime_replied().equalsIgnoreCase(""))
                    {
                        //request denied
                        text = "Pending Reply";
                        //dialog.setContentView(R.layout.request_denied);
                    }
                    else
                    {
                        //request denied
                        text = "Request Denied";
                    }
                }
            }



        }
        else
        {
            //we have received request from this user so lets see the result
            if(Select.from(ReceivedRequests.class).where(Condition.prop("idsend").eq(server_id)).first().getStatus().equalsIgnoreCase("0"))
            {
                //Pending
                text = "Pending Request";
            }
            else
            {
                //not pending we have replied
                text = "Request";
            }
        }

        return  text;
    }

    private boolean is_local(String local_id)
    {
        if(local_id.equalsIgnoreCase("") || local_id.equalsIgnoreCase(null))
            return  false;
        return true;
    }

}
