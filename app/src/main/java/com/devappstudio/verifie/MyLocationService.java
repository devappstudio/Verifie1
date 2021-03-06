package com.devappstudio.verifie;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastore.Api;
import datastore.ContactsList;
import datastore.Facilities;
import datastore.Location_Stats;
import datastore.User;
import datastore.VerificationStatus;
import datastore.Visibility;

/**
 * Created by root on 8/8/16.
 */

public class MyLocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static Location myLocationVar;

    public MyLocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("MyLocationService.onLocationChanged");
        myLocationVar = location;
        // do your work here with location
        //TODO This is where location is updated
        if (!Visibility.listAll(Visibility.class).isEmpty() && !Location_Stats.listAll(Location_Stats.class).isEmpty()) {
            if (Visibility.first(Visibility.class).isStatus()) {
                new_location();
            }
        }

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(3000); // Update location every 300 second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        myLocationVar = loc;
        //TODO This where the location is first requested
        if (!Visibility.listAll(Visibility.class).isEmpty() && !Location_Stats.listAll(Location_Stats.class).isEmpty()) {
            if (Visibility.first(Visibility.class).isStatus()) {
                if (myLocationVar != null)
                {
                    MyLocationService.readContacts task = new MyLocationService.readContacts();
                    task.execute("");

                }
             }
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onDestroy() {

       // Toast.makeText(this, "Location services stopped", Toast.LENGTH_LONG).show();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }



    void new_location()
    {


        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        Map<String, String> params = new HashMap<String, String>();
        params.put("server_id", User.first(User.class).getServer_id());
        params.put("longitude",myLocationVar.getLongitude()+"");
        params.put("latitude",myLocationVar.getLatitude()+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"update_user_location",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                Location_Stats ls = new Location_Stats(myLocationVar.getLongitude(),myLocationVar.getLatitude());
                                ls.save();
                            }
                            else
                            {

                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
// Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }



    //


    private class readContacts extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            check_on_verifie();
            loadFacilities();
            get_user();
            new_location();
            return null;
        }
    }


    void  check_on_verifie()
    {
        final List<ContactsList> cl = Select.from(ContactsList.class).where(Condition.prop("isonverifie").eq("0")).list();
        for (int i=0; i<cl.size();i++)
        {

            String phone = cl.get(i).getTelephone();
            try {
                PhoneNumberUtil pnu = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber pn = null;
                pn = pnu.parse(phone, "GH");
                phone = pnu.format(pn, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }

            final String tag = "new_user_logn";
            phone = phone.trim();
            phone = phone.replace("\\s+","");
            phone = phone.replace(" ","");

            final String phon = phone;

            Map<String, String> params = new HashMap<String, String>();
            params.put("telephone", phone);
            final int finalI = cl.get(i).getId().intValue();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+"check_is_on_verifie",new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString()+ " Telephone "+phon);
                            try {

                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
                                    ContactsList contactsList = ContactsList.findById(ContactsList.class,(long) finalI);
                                    JSONObject jo_stock = (JSONObject) response.get("data");
                                    contactsList.setIs_on_verifie("1");
                                    contactsList.setServer_id(jo_stock.get("id").toString());
                                    contactsList.setFile_name(jo_stock.get("file_name").toString());
                                    contactsList.setScreen_name(jo_stock.get("screen_name").toString());
                                    contactsList.save();
                                }
                            }
                            catch (Exception e)
                            {

                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
// Adding request to request queue
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(jsonObjReq, tag);
        }
    }


    void loadFacilities()
    {
        final String tag = "new_user_logn";

        User clst = User.first(User.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_user", clst.getServer_id());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"load_facilities",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            JSONArray jaa = response.getJSONArray("data");

                            for (int i=0; i< jaa.length(); i++)
                            {

                                JSONObject object = (JSONObject) jaa.get(i);
                                List<Facilities> f = Select.from(Facilities.class).where(Condition.prop("serverid").eq(object.get("id").toString())).list();
                                if(f.isEmpty())
                                {
                                    Facilities fac = new Facilities(object.get("name").toString(),object.get("contact_person_name").toString(),object.get("contact_person_telephone").toString(),object.get("location").toString(),object.get("id").toString());
                                    fac.save();
                                }
                                else
                                {
                                    Facilities fac = f.get(1);
                                    fac.setName(object.get("name").toString());
                                    fac.setContact_person(object.get("contact_person_name").toString());
                                    fac.setContact_phone(object.get("contact_person_telephone").toString());
                                    fac.setLocation(object.get("location").toString());
                                    fac.save();
                                }

                            }

                        }
                        catch (Exception e)
                        {
                           e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loadFacilities();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
// Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }

    void get_user()
    {
        System.out.println("Called Get User");
//        Toast.makeText(getActivity(),"Checking",Toast.LENGTH_LONG).show();

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";
        System.out.println("Started");

        User clst = User.first(User.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_user", clst.getServer_id());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"user_status",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print("result "+response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = (JSONObject) response.get("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company
                                //Toast.makeText(getActivity(),jo_stock.toString(),Toast.LENGTH_LONG).show();


                                VerificationStatus user = new VerificationStatus();
                                //realm1.clear(VerificationStatus.class);
                                user.setId((long)1);
                                user.setDate_to_expire(jo_stock.get("expiry").toString());
                                user.setDate_verified(jo_stock.get("current").toString());
                                user.setDate_recommended(jo_stock.get("recommended").toString());
                                user.setCentre(jo_stock.get("facility").toString());
                                user.save();
                            }
                            else
                            {
                                List<VerificationStatus> vs = VerificationStatus.listAll(VerificationStatus.class);
                                if(vs.isEmpty())
                                {
                                    VerificationStatus user = new VerificationStatus();
                                    user.setId((long)1);
                                    user.setDate_to_expire("N/A");
                                    user.setDate_verified("N/A");
                                    user.save();
                                }
                                else
                                {
                                    /*
                                    VerificationStatus vv = vs.first();
                                    realm.beginTransaction();
                                    vv.setDate_to_expire("13/09/2015");
                                    vv.setDate_verified("13/09/2015");
                                    realm.copyToRealmOrUpdate(vv);
                                    realm.commitTransaction();
                                    */
                                }
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            List<VerificationStatus>vs = VerificationStatus.listAll(VerificationStatus.class);
                            if(vs.isEmpty())
                            {
                                VerificationStatus user = new VerificationStatus();
                                user.setId((long)1);
                                user.setDate_to_expire("N/A");
                                user.setDate_verified("N/A");
                                user.save();
                            }
                            else
                            {
                                /*
                                VerificationStatus vv = vs.first();
                                realm.beginTransaction();
                                vv.setDate_to_expire("13/09/2015");
                                vv.setDate_verified("13/09/2015");
                                realm.copyToRealmOrUpdate(vv);
                                realm.commitTransaction();
                                */
                            }

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                List<VerificationStatus>vs = VerificationStatus.listAll(VerificationStatus.class);
                if(vs.isEmpty())
                {
                    VerificationStatus user = new VerificationStatus();
                    user.setId((long)1);
                    user.setDate_to_expire("N/A");
                    user.setDate_verified("N/A");
                    user.save();
                }
                else
                {
                    /*
                    VerificationStatus vv = vs.first();
                    realm.beginTransaction();
                    vv.setDate_to_expire("13/09/2015");
                    vv.setDate_verified("13/09/2015");
                    realm.copyToRealmOrUpdate(vv);
                    realm.commitTransaction();
                    */
                }
                get_user();
                System.out.println("Error Occurred In Here");
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
// Adding request to request queue

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }


}