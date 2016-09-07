package com.devappstudio.verifie;

import android.app.Dialog;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<ContactLocations> mData;
    private float mBaseElevation;

    public CardPagerAdapter(List<CardView> mViews,List<ContactLocations> mData) {

        this.mData = mData;
        this.mViews = mViews;

    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        final Dialog dialog = new Dialog(container.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.centers_layout);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView title,location,name,telephone;

                title = (TextView)dialog.findViewById(R.id.centerName);
                location = (TextView)dialog.findViewById(R.id.location);
                name = (TextView)dialog.findViewById(R.id.nameContactPerson);
                telephone = (TextView)dialog.findViewById(R.id.telephoneContactPerson);
                title.setText(mData.get(position).getName());
                location.setText(mData.get(position).getLocation());
                name.setText(mData.get(position).getPerson());
                telephone.setText(mData.get(position).getTelephone());

                Button dialogButton = (Button) dialog.findViewById(R.id.button3);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });




        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }



}
