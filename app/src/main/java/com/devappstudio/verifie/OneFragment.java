package com.devappstudio.verifie;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuassivi.view.ProgressProfileView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class OneFragment extends Fragment{
    String url;
    int number_slides = 3;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private TextView percentage;
    private Button btnSkip, btnNext;
    static private Float level = 0f;
    public OneFragment() {
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


        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/M/yyyy");

        try {
            Calendar calendar = Calendar.getInstance();
            String strDate = "" + simpleDateFormat.format(calendar.getTime());

            Date date1 = simpleDateFormat.parse(strDate);
            Date date2 = simpleDateFormat.parse("13/10/2017");
            Long t =  printDifference(date1, date2)/7;
            if(t > 52)
            {
                level =  (float)(t/52)*100;

            }
            else
            {
                level =  (float)(52/t)*100;

            }

        } catch (ParseException e) {
            e.printStackTrace();
            level = 50f;
        }


        View myView = null;
        myView = inflater.inflate(R.layout.fragment_one, container, false);

        if(level <= 25f)
        {
             myView = inflater.inflate(R.layout.fragment_one, container, false);
        }
       if(level > 25f && level <= 65)
        {
             myView = inflater.inflate(R.layout.fragment_one1, container, false);
        }
       if(level > 65f && level <= 100)
        {
             myView = inflater.inflate(R.layout.fragment_one2, container, false);
        }
        final ProgressProfileView profile = (ProgressProfileView) myView.findViewById(R.id.profile);

        profile.setProgress(level);

        profile.startAnimation();

        percentage = (TextView)myView.findViewById(R.id.percent_view) ;

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profile.getAnimator().addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        profile.getAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percentage.setText(animation.getAnimatedValue().toString()+"%");
                /*
                Float dd = (Float)Float.parseFloat(animation.getAnimatedValue().toString());
                if(dd <= 25f )
                {
                    profile.setProgressRingColor(R.color.rotabar_red_last);
                   // profile.set
                }

                if(dd <= 65f && dd > 25f)
                {
                  //  profile.setProgressRingColor(R.color.rotabar_light_green_second);
                }
              if(dd < 100f && dd > 65f)
                {
                   // profile.setProgressRingColor(R.color.rotabar_green_first);
                }
            if(dd > 100f)
                {
                   // profile.setProgressRingColor(R.color.darkgreen);
                }
                */

            }
        });


       viewPager = (ViewPager) myView.findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) myView.findViewById(R.id.layoutDots);

        layouts = new int[number_slides];

        for (int i=0; i<number_slides; i++)
        {
            layouts[i] = R.layout.mid_1;

        }

        // adding bottom dots
        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        return myView;
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getActivity());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT

            } else {
                // still pages are left

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv;
            View view = layoutInflater.inflate(layouts[position], container, false);
            iv = (ImageView)view.findViewById(R.id.imageView);
            Bitmap icon = null;
            if(position == 0)
            {
                icon = BitmapFactory.decodeResource(getActivity().getResources(),
                        R.drawable.bd1);
            }
           if(position == 1)
            {
                icon = BitmapFactory.decodeResource(getActivity().getResources(),
                        R.drawable.bd2);

            }
          if(position == 2)
            {
                icon = BitmapFactory.decodeResource(getActivity().getResources(),
                        R.drawable.bd3);

            }
            iv.setImageBitmap((icon));

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


    public Long printDifference(Date startDate, Date endDate){

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

       return elapsedDays;


    }

}