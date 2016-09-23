package com.devappstudio.verifie;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import datastore.Api;
import datastore.ContactsList;
import datastore.Facilities;
import datastore.User;
import datastore.VerificationStatus;
import io.realm.Realm;
import io.realm.RealmResults;

public class AppSetup extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private ProgressBar mRegistrationProgressBar;

    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_setup);
        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        ThreeBounce doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Realm realm = Realm.getDefaultInstance();
        RealmResults<ContactsList> clst = realm.where(ContactsList.class).findAll();
        if (clst.isEmpty())
        {
            AppSetup.readContacts task = new AppSetup.readContacts();
            task.execute("");
        }
        else
        {
            RealmResults<VerificationStatus>vs = realm.where(VerificationStatus.class).findAll();
            if(vs.isEmpty())
            {
                get_user();
            }
            else
            {
                final Intent intent = new Intent(AppSetup.this, main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        }



    }

    void get_user()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

       final Realm realm = Realm.getDefaultInstance();
        User clst = realm.where(User.class).findAll().first();

        Map<String, String> params = new HashMap<String, String>();
        params.put("id_user", clst.getServer_id());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"user_status",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONObject jo_stock = (JSONObject) response.get("data");
                                // JSONObject jo_company = response.getJSONObject("company");
                                //JSONObject jo_user = response.getJSONObject("user");
                                //save user
                                // save company


                                VerificationStatus user = new VerificationStatus();
                                realm.beginTransaction();
                                user.setId(1);
                                user.setDate_to_expire(jo_stock.get("expiry").toString());
                                user.setDate_verified(jo_stock.get("current").toString());
                                realm.copyToRealmOrUpdate(user);

                                realm.commitTransaction();
                                final Intent intent = new Intent(AppSetup.this, main.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();

                            }
                            else
                            {
                                RealmResults<VerificationStatus>vs = realm.where(VerificationStatus.class).findAll();
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
                                final Intent intent = new Intent(AppSetup.this, main.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            RealmResults<VerificationStatus>vs = realm.where(VerificationStatus.class).findAll();
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
                            final Intent intent = new Intent(AppSetup.this, main.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                RealmResults<VerificationStatus>vs = realm.where(VerificationStatus.class).findAll();
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
                final Intent intent = new Intent(AppSetup.this, main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
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

    private class readContacts extends AsyncTask<String, Void, String> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected String doInBackground(String... params) {
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            loadFacilities();

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {

                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //String image = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {

                            String phone = pCur.getString(
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            boolean is_stored = false;

                            phone = phone.trim();
                            phone = phone.replace("\\s+","");
                            phone = phone.replace(" ","");
                            System.out.println(phone);

                            Realm realm = Realm.getDefaultInstance();
                            RealmResults<ContactsList> clst = realm.where(ContactsList.class).equalTo("telephone",phone).findAll();
                            if(clst.size() > 0)
                            {
                                for (int i=0; i<clst.size(); i++)
                                {
                                    if(clst.get(i).getTelephone().equalsIgnoreCase(phone))
                                    {
                                        is_stored = true;

                                    }
                                }

                            }

                            if(!is_stored) {
                                ContactsList cl = new ContactsList();
                                cl.setTelephone(phone);
                                cl.setIs_on_verifie("0");
                                cl.setType("Other");
                                cl.setFile_name("");
                                cl.setName(name);
                                cl.setId( ((int) realm.where(ContactsList.class).maximumInt("id")) + 1);
                                realm.beginTransaction();
                                realm.copyToRealm(cl);
                                realm.commitTransaction();
                            }
                        }
                        pCur.close();
                    }
                }
            }
            return null;
        }


        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param s The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final Intent intent = new Intent(AppSetup.this, main.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }


    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        if(!isReceiverRegistered) {
            /*LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
                    */
            isReceiverRegistered = true;
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

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }
}
