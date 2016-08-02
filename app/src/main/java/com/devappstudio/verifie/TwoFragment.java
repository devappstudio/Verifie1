package com.devappstudio.verifie;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class TwoFragment extends Fragment{
    private List<NearBy> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NearByAdaptor mAdapter;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myView = inflater.inflate(R.layout.fragment_two, container, false);
        recyclerView = (RecyclerView) myView.findViewById(R.id.recycler_view);
        mAdapter = new NearByAdaptor(movieList);
        prepareMovieData();

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NearBy movie = movieList.get(position);
                Toast.makeText(getActivity(), movie.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return myView;
    }

    private void prepareMovieData() {
        NearBy movie = new NearBy("Mad Max: Fury Road", "Action & Adventure", "2015"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Inside Out", "Animation, Kids & Family", "2015"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Star Wars: Episode VII - The Force Awakens", "Action", "2015"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Shaun the Sheep", "Animation", "2015"," ----- ");
        movieList.add(movie);

        movie = new NearBy("The Martian", "Science Fiction & Fantasy", "2015"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Mission: Impossible Rogue Nation", "Action", "2015"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Up", "Animation", "2009"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Star Trek", "Science Fiction", "2009"," ----- ");
        movieList.add(movie);

        movie = new NearBy("The LEGO Movie", "Animation", "2014"," ----- ");
        movieList.add(movie);

        movie = new NearBy("Iron Man", "Action & Adventure", "2008"," ----- ");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TwoFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TwoFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}