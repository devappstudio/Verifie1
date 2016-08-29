package com.devappstudio.verifie;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kuassivi.view.ProgressProfileView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import datastore.Api;
import datastore.RealmController;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class OneFragment extends Fragment{

    private Button mButton;
    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;




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
    private Uri fileUri; // file url to store image/video

    // LogCat tag
    private static final String TAG = OneFragment.class.getSimpleName();


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 935;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    static TextView text;
    static String filePath;
    long totalSize = 0;



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
        ProgressProfileView profile = (ProgressProfileView) myView.findViewById(R.id.profile);

        profile.setProgress(level);

        profile.startAnimation();

        percentage = (TextView)myView.findViewById(R.id.percent_view) ;
        String imagename ="profile";
        if(ImageStorage.checkifImageExists(imagename))
        {
            File file = ImageStorage.getImage("/verifie/profile/"+imagename+".jpg");
            String path = file.getAbsolutePath();
            if (path != null){
                Bitmap b = BitmapFactory.decodeFile(path);
                profile.setImageBitmap(b);

            }
        } else {
            new GetImages(RealmController.with(getActivity()).getUser(1).getFile_name(), profile, imagename).execute() ;
        }


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // captureImage();
                final Intent intent = new Intent(getActivity(), ImageEditor.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getActivity().startActivity(intent);
                getActivity().finish();
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




        mViewPager = (ViewPager) myView.findViewById(R.id.viewPager);


        mCardAdapter = new CardPagerAdapter();
        mFragmentCardAdapter = new CardFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                dpToPixels(2, getActivity()));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(5);
        mCardShadowTransformer.enableScaling(true);

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

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        filePath = fileUri.getPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        OneFragment.this.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onActivityResult( requestCode,  resultCode,  data);
        }
    }
    private void launchUploadActivity(boolean isImage){
        new UploadFileToServer().execute();
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        ProgressDialog pd;

         @Override
        protected void onPreExecute() {
            // setting progress bar to zero

             pd = ProgressDialog.show(getActivity(),"Contacting Server ..."," Please wait uploading ... 0% done", true);

             super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

            // updating progress bar value

            // updating percentage value
            pd.setMessage("Please wait uploading ... "+String.valueOf(progress[0])+"% done");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(Api.FILE_UPLOAD_URL);
  /*
*/
            try {

                String boundary = "-------------" + System.currentTimeMillis();

                httppost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("id", new StringBody(RealmController.with(getActivity()).getUser(1).getServer_id()));

                totalSize = entity.getContentLength();
                httppost.setEntity((entity));

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            pd.hide();
            // showing the server response in an alert dialog
          //  showAlert(result);
            Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();

            super.onPostExecute(result);
        }

    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Api.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Api.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }


}