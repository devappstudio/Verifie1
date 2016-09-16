package com.devappstudio.verifie;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kuassivi.view.ProgressProfileView;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import datastore.Api;
import datastore.RealmController;
import datastore.User;
import datastore.VerificationStatus;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;


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
    CircleImageView cimv;
    private Button btnSkip, btnNext;
    static private Float level = 0f;
    private Uri fileUri; // file url to store image/video
    private static List<CardView> mViews;
    private static List<ContactLocations> mData;
    static String server_id;

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

    ProgressProfileView profile;

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
        final Realm realm = Realm.getDefaultInstance();

        RealmResults<VerificationStatus> vs = realm.where(VerificationStatus.class).findAll();
        if(vs.isEmpty())
        {
            VerificationStatus user = new VerificationStatus();
            realm.beginTransaction();
            user.setId(1);
            user.setDate_to_expire("13/09/2015");
            user.setDate_verified("13/09/2015");
            realm.copyToRealmOrUpdate(user);
            realm.commitTransaction();
        }
        VerificationStatus vss = realm.where(VerificationStatus.class).findAll().first();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/M/yyyy");

        try {
            Calendar calendar = Calendar.getInstance();
            String strDate = "" + simpleDateFormat.format(calendar.getTime());

            Date date1 = simpleDateFormat.parse(strDate);
            Date date2 = simpleDateFormat.parse(vss.getDate_to_expire());
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
        profile = (ProgressProfileView) myView.findViewById(R.id.profile);

        profile.setProgress(level);

        profile.startAnimation();
        cimv = (CircleImageView)myView.findViewById(R.id.profile_image);
        cimv.setImageResource(R.drawable.panic);

        percentage = (TextView)myView.findViewById(R.id.percent_view) ;

        Realm Mrealm = Realm.getDefaultInstance();
        String url = Mrealm.where(User.class).findAll().first().getFile_name();
        server_id = Mrealm.where(User.class).findAll().first().getServer_id();
        try {
            Picasso.with(getActivity()).load(url).into(profile);
        }
        catch (Exception e)
        {
            System.out.println(url);
            e.printStackTrace();
        }


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
               /* final Intent intent = new Intent(getActivity(), ImageEditor.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getActivity().startActivity(intent);
                getActivity().finish();
                */

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

        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ContactLocations cl  = new ContactLocations();
            cl.setLocation("Location address");
            cl.setName("Name Of Hospital");
            cl.setPerson("Name Of Contact Person");
            cl.setTelephone("+233262141279");
            mData.add(cl);
            mViews.add(null);
        }


        mCardAdapter = new CardPagerAdapter(mViews,mData);

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(10);
        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Current integer "+mViewPager.getCurrentItem(), Toast.LENGTH_LONG).show();
            }
        });

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
            android.support.v7.widget.AppCompatImageView iv;
            View view = layoutInflater.inflate(layouts[position], container, false);
            iv = (android.support.v7.widget.AppCompatImageView)view.findViewById(R.id.imageView);
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
    final String imagename ="profile.jpg";
    private Uri outputFileUri;
    private static final int SELECT_PICTURE = 1;

    private void captureImage() {

        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "/verifie/profile/" + File.separator);
        root.mkdirs();
        final String fname = imagename;
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            final boolean isCamera;
            Uri ur;

            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                if (action == null) {
                    isCamera = false;
                } else {
                    isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }
            String path="";
            Uri selectedImageUri;
            if (isCamera) {
                Toast.makeText(getContext(),"Camera",Toast.LENGTH_LONG).show();
                ur = outputFileUri;
                path = outputFileUri.getPath().toString();

            } else {
                /*
                selectedImageUri = data == null ? null : data.getData();
                String uriString = selectedImageUri.toString();
                File myFile = new File(uriString);
                path = myFile.getAbsolutePath();
                */
                Toast.makeText(getContext(),"File",Toast.LENGTH_LONG).show();

                Uri uri = data.getData();
                ur = uri;
                String[] projection = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                path = cursor.getString(columnIndex); // returns null
                cursor.close();

            }
            uploadMultipart(getContext(),path);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ur);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            uploadImage();
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }


    public void uploadMultipart(final Context context,String path) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context, Api.getApi() + "upload_image")
                            .addFileToUpload(path, "file_name")
                            .addParameter("server_id", server_id)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(UploadInfo uploadInfo) {
                                    // your code here
//                                    if(pd != null)
//                                    {
//                                        pd.setMessage( uploadInfo.getProgressPercent()+" % uploaded ");
//                                    }

                                }

                                @Override
                                public void onError(UploadInfo uploadInfo, Exception exception) {
                                    // your code here
                                    exception.printStackTrace();
//                                    if(pd != null)
//                                    {
//                                        pd.hide();
//                                    }
//                                    Toast.makeText(getApplicationContext(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();

                                }

                                @Override
                                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    // your code here
                                    // if you have mapped your server response to a POJO, you can easily get it:
                                    // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                                    // JSONObject obj = serverResponse.getBodyAsString()
                                    String location = serverResponse.getBodyAsString();

                                    Realm realm = Realm.getDefaultInstance();
//                                    new GetImages(location, profile, imagename).execute() ;
                                    Picasso.with(getActivity()).load(location).into(profile);

                                    User us = realm.where(User.class).findFirst();
                                    realm.beginTransaction();
                                    us.setFile_name(location);
                                    realm.copyToRealmOrUpdate(us);
                                    realm.commitTransaction();
//                                    if(pd != null)
//                                    {
//                                        pd.hide();
//                                    }

                                }

                                @Override
                                public void onCancelled(UploadInfo uploadInfo) {
                                    // your code here
//                                    if(pd != null)
//                                    {
//                                        pd.hide();
//                                    }

                                }
                            })
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.getApi() + "upload_image",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(getContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                //String name = editTextName.getText().toString().trim();

                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("file_name", image);
                params.put("server_id", server_id);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private Bitmap bitmap;

}