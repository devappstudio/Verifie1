package com.devappstudio.verifie;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.ContactsList;
import datastore.Facilities;
import datastore.Location_Stats;
import datastore.RealmController;
import datastore.User;
import datastore.VerificationStatus;
import datastore.Visibility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by root on 8/8/16.
 */

public class MyLocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Realm realm;
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
        this.realm = RealmController.with(getApplication()).getRealm();


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
        Realm rRealm = Realm.getDefaultInstance();
        if (!rRealm.where(Visibility.class).findAll().isEmpty() && !rRealm.where(Location_Stats.class).findAll().isEmpty()) {
            if (rRealm.where(Visibility.class).findFirst().isStatus()) {
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
        Realm rRealm = Realm.getDefaultInstance();
        //TODO This where the location is first requested
        if (!rRealm.where(Visibility.class).findAll().isEmpty() && !rRealm.where(Location_Stats.class).findAll().isEmpty()) {
            if (rRealm.where(Visibility.class).findFirst().isStatus()) {
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
        Realm mRealm = Realm.getDefaultInstance();

        Map<String, String> params = new HashMap<String, String>();
        params.put("server_id", mRealm.where(User.class).findFirst().getServer_id());
        params.put("longitude",myLocationVar.getLongitude()+"");
        params.put("latitude",myLocationVar.getLatitude()+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"update_user_location",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {
                            Realm rRealm = Realm.getDefaultInstance();


                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                Location_Stats ls = new Location_Stats(myLocationVar.getLongitude(),myLocationVar.getLatitude());
                                rRealm.beginTransaction();
                                rRealm.clear(Location_Stats.class);
                                rRealm.copyToRealm(ls);
                                rRealm.commitTransaction();
                            }
                            else
                            {

                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Realm rr = Realm.getDefaultInstance();
                            try {
                                rr.cancelTransaction();
                                Realm rRealm = Realm.getDefaultInstance();


                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
                                    Location_Stats ls = new Location_Stats(myLocationVar.getLongitude(),myLocationVar.getLatitude());
                                    rRealm.beginTransaction();
                                    rRealm.clear(Location_Stats.class);
                                    rRealm.copyToRealm(ls);
                                    rRealm.commitTransaction();
                                }
                                else
                                {

                                }

                            }
                            catch (Exception ew)
                            {
                                ew.printStackTrace();
                            }

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
        final Realm trealm = Realm.getDefaultInstance();
        final RealmResults<ContactsList> cl = trealm.where(ContactsList.class).equalTo("is_on_verifie","0").findAll();

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
            final int finalI = cl.get(i).getId();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+"check_is_on_verifie",new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString()+ " Telephone "+phon);
                            try {

                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
                                    final Realm rrealm = Realm.getDefaultInstance();
                                    ContactsList contactsList = rrealm.where(ContactsList.class).equalTo("id",finalI).findFirst();
                                    JSONObject jo_stock = (JSONObject) response.get("data");
                                    rrealm.beginTransaction();
                                    contactsList.setIs_on_verifie("1");
                                    contactsList.setServer_id(jo_stock.get("id").toString());
                                    contactsList.setFile_name(jo_stock.get("file_name").toString());
                                    contactsList.setScreen_name(jo_stock.get("screen_name").toString());
                                    rrealm.copyToRealmOrUpdate(contactsList);
                                    rrealm.commitTransaction();
                                }
                            }
                            catch (Exception e)
                            {
                                final Realm rrealm = Realm.getDefaultInstance();
                                try {
                                    rrealm.cancelTransaction();

                                }
                                catch (Exception jh)
                                {

                                }
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

        final Realm realm = Realm.getDefaultInstance();
        User clst = realm.where(User.class).findAll().first();

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_user", clst.getServer_id());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"load_facilities",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final Realm realm = Realm.getDefaultInstance();

                        System.out.print(response.toString());
                        try {

                            JSONArray jaa = response.getJSONArray("data");

                            for (int i=0; i< jaa.length(); i++)
                            {

                                JSONObject object = (JSONObject) jaa.get(i);
                                RealmResults<Facilities> f = realm.where(Facilities.class).equalTo("server_id",object.get("id").toString()).findAll();

                                if(f.isEmpty())
                                {
                                    Facilities fac = new Facilities(((int) realm.where(Facilities.class).maximumInt("id")),object.get("name").toString(),object.get("contact_person_name").toString(),object.get("contact_person_telephone").toString(),object.get("location").toString(),object.get("id").toString());
                                    realm.beginTransaction();
                                    realm.copyToRealm(fac);
                                    realm.commitTransaction();
                                }
                                else
                                {
                                    Facilities fac = f.first();
                                    realm.beginTransaction();
                                    fac.setName(object.get("name").toString());
                                    fac.setContact_person(object.get("contact_person_name").toString());
                                    fac.setContact_phone(object.get("contact_person_telephone").toString());
                                    fac.setLocation(object.get("location").toString());
                                    realm.copyToRealmOrUpdate(fac);
                                    realm.commitTransaction();
                                }

                            }

                        }
                        catch (Exception e)
                        {
                            try {
                                realm.cancelTransaction();

                                JSONArray jaa = response.getJSONArray("data");

                                for (int i=0; i< jaa.length(); i++)
                                {

                                    JSONObject object = (JSONObject) jaa.get(i);
                                    RealmResults<Facilities> f = realm.where(Facilities.class).equalTo("server_id",object.get("id").toString()).findAll();

                                    if(f.isEmpty())
                                    {
                                        Facilities fac = new Facilities(((int) realm.where(Facilities.class).maximumInt("id")),object.get("name").toString(),object.get("contact_person_name").toString(),object.get("contact_person_telephone").toString(),object.get("location").toString(),object.get("id").toString());
                                        realm.beginTransaction();
                                        realm.copyToRealm(fac);
                                        realm.commitTransaction();
                                    }
                                    else
                                    {
                                        Facilities fac = f.first();
                                        realm.beginTransaction();
                                        fac.setName(object.get("name").toString());
                                        fac.setContact_person(object.get("contact_person_name").toString());
                                        fac.setContact_phone(object.get("contact_person_telephone").toString());
                                        fac.setLocation(object.get("location").toString());
                                        realm.copyToRealmOrUpdate(fac);
                                        realm.commitTransaction();
                                    }

                                }

                            }
                            catch (Exception ee)
                            {
                                try {
                                    realm.cancelTransaction();

                                }
                                catch (Exception mn)
                                {

                                }
                            }

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

        final Realm realm = Realm.getDefaultInstance();
        User clst = realm.where(User.class).findAll().first();

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_user", clst.getServer_id());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"user_status",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final Realm realm1 = Realm.getDefaultInstance();
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

                                realm1.beginTransaction();
                                user.setId(1);
                                user.setDate_to_expire(jo_stock.get("expiry").toString());
                                user.setDate_verified(jo_stock.get("current").toString());
                                user.setDate_recommended(jo_stock.get("recommended").toString());
                                user.setCentre(jo_stock.get("facility").toString());
                                realm1.copyToRealmOrUpdate(user);
                                realm1.commitTransaction();
                            }
                            else
                            {
                                RealmResults<VerificationStatus> vs = realm1.where(VerificationStatus.class).findAll();
                                if(vs.isEmpty())
                                {
                                    VerificationStatus user = new VerificationStatus();
                                    realm1.beginTransaction();
                                    user.setId(1);
                                    user.setDate_to_expire("N/A");
                                    user.setDate_verified("N/A");
                                    realm1.copyToRealmOrUpdate(user);
                                    realm1.commitTransaction();
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
                            RealmResults<VerificationStatus>vs = realm1.where(VerificationStatus.class).findAll();
                            if(vs.isEmpty())
                            {
                                VerificationStatus user = new VerificationStatus();
                                realm1.beginTransaction();
                                user.setId(1);
                                user.setDate_to_expire("N/A");
                                user.setDate_verified("N/A");
                                realm1.copyToRealmOrUpdate(user);
                                realm1.commitTransaction();
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
                final Realm realm1 = Realm.getDefaultInstance();
                RealmResults<VerificationStatus>vs = realm1.where(VerificationStatus.class).findAll();
                if(vs.isEmpty())
                {
                    VerificationStatus user = new VerificationStatus();
                    realm1.beginTransaction();
                    user.setId(1);
                    user.setDate_to_expire("N/A");
                    user.setDate_verified("N/A");
                    realm1.copyToRealmOrUpdate(user);
                    realm1.commitTransaction();
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