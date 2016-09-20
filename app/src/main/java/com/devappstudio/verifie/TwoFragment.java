package com.devappstudio.verifie;


import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastore.Api;
import datastore.ContactsList;
import datastore.RealmController;
import datastore.ReceivedRequests;
import datastore.SentRequests;
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

        TwoFragment.readContacts task = new TwoFragment.readContacts();
        task.execute("");

        SearchView search = (SearchView) myView.findViewById( R.id.search);
        SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener(){

            /**
             * Called when the user submits the query. This could be due to a key press on the
             * keyboard or due to pressing a submit button.
             * The listener can override the standard behavior by returning true
             * to indicate that it has handled the submit request. Otherwise return false to
             * let the SearchView handle the submission by launching any associated intent.
             *
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.toLowerCase();
                final List<NearBy> List = new ArrayList<>();
                for (int i = 0; i< movieList.size(); i++) {
                    final NearBy text = movieList.get(i);
                    if (text.getTelephone_number().startsWith(query) || text.getTelephone_number().contains(query) || text.getTelephone_number().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                    if (text.getName().toLowerCase().contains(query) || text.getName().toLowerCase().startsWith(query) || text.getName().toLowerCase().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                    if (text.getScreen_name().toLowerCase().contains(query) || text.getScreen_name().toLowerCase().startsWith(query) || text.getScreen_name().toLowerCase().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                }
                mAdapter = new NearByAdaptor(List,getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                return true;
            }

            /**
             * Called when the query text is changed by the user.
             *
             * @param query the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String query) {
                query = query.toLowerCase();
                final List<NearBy> List = new ArrayList<>();
                for (int i = 0; i< movieList.size(); i++) {
                    final NearBy text = movieList.get(i);
                    if (text.getTelephone_number().startsWith(query) || text.getTelephone_number().contains(query) || text.getTelephone_number().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                    if (text.getName().toLowerCase().contains(query) || text.getName().toLowerCase().startsWith(query) || text.getName().toLowerCase().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                    if (text.getScreen_name().toLowerCase().contains(query) || text.getScreen_name().toLowerCase().startsWith(query) || text.getScreen_name().toLowerCase().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                }
                mAdapter = new NearByAdaptor(List,getContext());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
                return true;
            }
        };

        search.setOnQueryTextListener(listener);


       // RealmController.with(getActivity()).clearContacts();




        mHandlerTask.run();


       // prepareMovieData();
 /*       TwoFragment.readContacts task = new readContacts();
        task.execute("");
*/
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
                //Toast.makeText(getActivity(), movie.getServer_id() + " is selected!", Toast.LENGTH_SHORT).show();
                if(movie.getServer_id().equalsIgnoreCase("0"))
                {
                    //Not On service so only invitation

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(movie.getTelephone_number(), null, "Check out verifie on your smart phone download it at https://drive.google.com/open?id=0B_hBEdD_-DHUcVFjWnZTb01ZMEk", null, null);
                        Toast.makeText(getContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                    }

                    catch (Exception e) {
                        Toast.makeText(getActivity(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                else
                {
                    call_method(movie.getServer_id(),movie);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        near_by_offline_users();

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
                                near_by_offline_users();
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
                near_by_offline_users();

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
                                    near_by_offline_users();

                                }
                                if(response.get("data").toString().equalsIgnoreCase("1"))
                                {
                                     user = new Visibility(true);
                                    visibility.setChecked(true);
                                    near_by_offline_users();

                                }

                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();
                            }
                            else
                            {
                                near_by_offline_users();

                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            near_by_offline_users();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Toast.makeText(getActivity(),"Sorry A Network Error Occurred",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                near_by_offline_users();

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

                                RegisteredCls.sort("name",true);
                                UnRegisteredCls.sort("name",true);

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
                                near_by_offline_users();

                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            near_by_offline_users();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                near_by_offline_users();

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


        RegisteredCls.sort("name",true);
        UnRegisteredCls.sort("name",true);

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
            mHandler.postDelayed(mHandlerTask, (1000 * 2 * 1));
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
            check_on_verifie();

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

            Map<String, String> params = new HashMap<String, String>();
            params.put("telephone", phone);
            final int finalI = i;
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+"check_is_on_verifie",new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString());
                            try {

                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
                                    ContactsList contactsList = cl.get(finalI);

                                    trealm.beginTransaction();
                                    contactsList.setIs_on_verifie("1");
                                    trealm.copyToRealmOrUpdate(contactsList);
                                    trealm.commitTransaction();
                                    near_by_offline_users();
                                }
                                else
                                {
                                    check_on_verifie1(cl.get(finalI));
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

    }

    void  check_on_verifie1(final ContactsList contactsList)
    {

        String phone = contactsList.getTelephone();
        phone = phone.trim();
        phone = phone.replace("\\s+","");
        phone = phone.replace(" ","");

            final String tag = "new_user_logn";

        final Realm trealm = Realm.getDefaultInstance();

            Map<String, String> params = new HashMap<String, String>();
            params.put("telephone", phone);
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    Api.getApi()+"check_is_on_verifie",new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.print(response.toString());
                            try {

                                if(response.get("status").toString().equalsIgnoreCase("1"))
                                {
                                    trealm.beginTransaction();
                                    contactsList.setIs_on_verifie("1");
                                    trealm.copyToRealmOrUpdate(contactsList);
                                    trealm.commitTransaction();
                                    near_by_offline_users();

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


    void call_method(String server_id, final NearBy nearBy)
    {
        String text="Request";


        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Realm realm = Realm.getDefaultInstance();

        if(realm.where(ReceivedRequests.class).contains("id_send",server_id).findAll().isEmpty())
        {
            //we have not received request from this user
            //lets check if we have sent to user
            if(realm.where(SentRequests.class).contains("id_receipent",server_id).findAll().isEmpty())
            {
                // we havent sent yet
                text = "Request";
                dialog.setContentView(R.layout.request_bar);
                Button allow = (Button)dialog.findViewById(R.id.send_request);
                Button deny = (Button)dialog.findViewById(R.id.cancel);
                deny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestRotabar(nearBy.getServer_id());
                    }
                });

            }
            else
            {
                //we have sent so lets check the result
                if(realm.where(SentRequests.class).contains("id_receipent",server_id).findAll().first().getReply() == 1)
                {
                    //accepted
                    text = "View RotaBar";
                    dialog.setContentView(R.layout.request_denied);

                }
                else
                {
                    //request denied
                    text = "Request Denied";
                    dialog.setContentView(R.layout.request_denied);
                }
            }



        }
        else
        {
            //we have received request from this user so lets see the result
            if(realm.where(ReceivedRequests.class).contains("id_send",server_id).findAll().first().getStatus().equalsIgnoreCase("0"))
            {
                //Pending
                text = "Pending Request";
                dialog.setContentView(R.layout.reply_request);
                Button allow = (Button)dialog.findViewById(R.id.send_request);
                Button deny = (Button)dialog.findViewById(R.id.cancel);
                deny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        replyRequest(nearBy.getServer_id());
                    }
                });

            }
            else
            {
                //not pending we have replied
                text = "Request";
                dialog.setContentView(R.layout.request_bar);
                Button allow = (Button)dialog.findViewById(R.id.send_request);
                Button deny = (Button)dialog.findViewById(R.id.cancel);
                deny.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestRotabar(nearBy.getServer_id());
                    }
                });


            }
        }
        dialog.show();
    }



    void requestRotabar(String server_id)
    {

    }
    void replyRequest(String server_id)
    {

    }




}

//