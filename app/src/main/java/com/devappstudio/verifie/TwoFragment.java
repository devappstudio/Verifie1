package com.devappstudio.verifie;


import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastore.Api;
import datastore.ContactsList;
import datastore.RealmController;
import datastore.User;
import datastore.Visibility;
import io.realm.Realm;
import io.realm.RealmResults;


public class TwoFragment extends Fragment{
    private List<NearBy> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NearByAdaptor mAdapter;
    Switch visibility;
    int visible;
    private Realm realm;
    Handler mHandler = new Handler();



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
        mAdapter = new NearByAdaptor(movieList,getContext());
        this.realm = RealmController.with(this).getRealm();

        visibility = (Switch)myView.findViewById(R.id.visibility);
        //Just got here check if visibility has been set already
        if(RealmController.with(getActivity()).hasVisible())
        {
            visibility.setChecked(RealmController.with(getActivity()).getVisibility(1).isStatus());
        }
        else
        {
            //visibility not set confirm from server and save
            check_visibility();
        }
            visibility.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                change_visibility();

            }
        });
        RealmController.with(getActivity()).clearContacts();




        mHandlerTask.run();


       // prepareMovieData();
        TwoFragment.readContacts task = new readContacts();
        task.execute("");


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
                Toast.makeText(getActivity(), movie.getTelephone_number() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return myView;
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


    void change_visibility()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.loading_dialog_layout);
        dialog.setTitle("Contacting Our Servers ...");
        TextView text = (TextView) dialog.findViewById(R.id.message);
        text.setText("Please wait ...");

        dialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("server_id", RealmController.with(getActivity()).getUser(1).getServer_id());

        if(visibility.isChecked())
        {
            params.put("visibility","1");
        }
        else
        {
            params.put("visibility","0");

        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"change_user_visibility",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        dialog.hide();
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                               RealmController.with(getActivity()).clearAllVisibility();
                                Visibility user = new Visibility(visibility.isChecked());
                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();
                                if(visibility.isChecked())
                                {
                                    Toast.makeText(getActivity(),"Your visibility is on",Toast.LENGTH_LONG).show();
                                    near_by_users();
                                }
                                else
                                {
                                    movieList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    near_by_offline_users();
                                    Toast.makeText(getActivity(),"Your visibility is off",Toast.LENGTH_LONG).show();

                                }

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
                dialog.hide();
                Toast.makeText(getActivity(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();

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

    void check_visibility()
    {

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_logn";

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.loading_dialog_layout);
        dialog.setTitle("Contacting Our Servers ...");
        TextView text = (TextView) dialog.findViewById(R.id.message);
        text.setText("Please wait ...");

        dialog.show();

        Map<String, String> params = new HashMap<String, String>();
        params.put("server_id", RealmController.with(getActivity()).getUser(1).getServer_id());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"chek_user_visibility",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        dialog.hide();
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                               RealmController.with(getActivity()).clearAllVisibility();
                                Visibility user = new Visibility();
                                if(response.get("data").toString().equalsIgnoreCase("0"))
                                {
                                     user = new Visibility(false);
                                    visibility.setChecked(false);

                                }
                                if(response.get("data").toString().equalsIgnoreCase("1"))
                                {
                                     user = new Visibility(true);
                                    visibility.setChecked(true);
                                }

                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();
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
                dialog.hide();
                Toast.makeText(getActivity(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();
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

    void near_by_users()
    {

        final String tag = "new_user_logn";


        Map<String, String> params = new HashMap<String, String>();
        params.put("server_id", RealmController.with(getActivity()).getUser(1).getServer_id());
        params.put("longitude",RealmController.with(getActivity()).getLocation(1).getLongitude()+"");
        params.put("latitude",RealmController.with(getActivity()).getLocation(1).getLatitude()+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"near_users",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                movieList.clear();

                                RealmResults<ContactsList> RegisteredCls = RealmController.with(getActivity()).getAllRegisteredContacts();
                                RealmResults<ContactsList> UnRegisteredCls = RealmController.with(getActivity()).getAllUnRegisteredContacts();

                                for (int ii = 0; ii < RegisteredCls.size(); ii++)
                                {
                                    ContactsList cl = RegisteredCls.get(ii);
                                    NearBy dumb = new NearBy(cl.getName(),"",cl.getTelephone(),cl.getServer_id()+"",cl.getFile_name(),cl.getIs_on_verifie(),cl.getId()+"",cl.getScreen_name());
                                    movieList.add(dumb);
                                }

                                for (int ii = 0; ii < UnRegisteredCls.size(); ii++)
                                {
                                    ContactsList cl = UnRegisteredCls.get(ii);
                                    NearBy dumb = new NearBy(cl.getName(),"",cl.getTelephone(),cl.getServer_id()+"",cl.getFile_name(),cl.getIs_on_verifie(),cl.getId()+"",cl.getScreen_name());
                                    movieList.add(dumb);
                                }

                                JSONArray jaa = response.getJSONArray("data");

                                for (int i=0; i< jaa.length(); i++)
                                {

                                    JSONObject object = (JSONObject) jaa.get(i);
                                    User us = RealmController.with(getActivity()).getUser(1);
                                    if(!us.getServer_id().equalsIgnoreCase(object.get("id").toString()))
                                    {
                                        NearBy dumb = new NearBy(object.get("fullname").toString(),object.get("file_name").toString(),object.get("telephone").toString(),object.get("id").toString(),object.get("screen_name").toString());
                                        movieList.add(dumb);
                                    }

                                }
                                mAdapter.notifyDataSetChanged();
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
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }


    void near_by_offline_users()
    {


        movieList.clear();

        RealmResults<ContactsList> RegisteredCls = RealmController.with(getActivity()).getAllRegisteredContacts();
        RealmResults<ContactsList> UnRegisteredCls = RealmController.with(getActivity()).getAllUnRegisteredContacts();

        for (int ii = 0; ii < RegisteredCls.size(); ii++)
        {
            ContactsList cl = RegisteredCls.get(ii);
            NearBy dumb = new NearBy(cl.getName(),"",cl.getTelephone(),cl.getServer_id()+"",cl.getFile_name(),cl.getIs_on_verifie(),cl.getId()+"",cl.getScreen_name());
            movieList.add(dumb);
        }

        for (int ii = 0; ii < UnRegisteredCls.size(); ii++)
        {
            ContactsList cl = UnRegisteredCls.get(ii);
            NearBy dumb = new NearBy(cl.getName(),"",cl.getTelephone(),cl.getServer_id()+"",cl.getFile_name(),cl.getIs_on_verifie(),cl.getId()+"",cl.getScreen_name());
            movieList.add(dumb);
        }

        mAdapter.notifyDataSetChanged();
    }


    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            if(RealmController.with(getActivity()).hasVisible() && RealmController.with(getActivity()).hasLocation())
            {
                if(RealmController.with(getActivity()).getVisibility(1).isStatus())
                {
                    near_by_users();
                }
                else

                {
                    near_by_offline_users();

                }
            }
            mHandler.postDelayed(mHandlerTask, (1000 * 3 * 1));
        }
    };



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
            ContentResolver cr = getActivity().getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    ContactsList cl = new ContactsList();

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
                                cl.setTelephone(phone);
                                cl.setIs_on_verifie("0");
                                cl.setType("Other");
                                cl.setFile_name("");
                                cl.setName(name);
                                RealmResults <ContactsList> all_cl = realm.where(ContactsList.class).findAll();
                                cl.setId(all_cl.size()+1);
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
    }


}
//http://stackoverflow.com/questions/6106859/how-to-format-a-phone-number-using-phonenumberutils
//https://github.com/googlei18n/libphonenumber/blob/master/README.md