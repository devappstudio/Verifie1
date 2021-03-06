package com.devappstudio.verifie;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.kuassivi.view.ProgressProfileView;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import datastore.Api;
import datastore.ApprovedRequests;
import datastore.ContactsList;
import datastore.Location_Stats;
import datastore.ReceivedRequests;
import datastore.SentRequests;
import datastore.User;
import datastore.Visibility;


public class TwoFragment extends Fragment{
    private List<NearBy> movieList = new ArrayList<>();
    private List<NearBy> movieListContacts = new ArrayList<>();
    private EmptyRecyclerView recyclerView,recyclerViewContacts;
    private NearByAdaptor mAdapter;
    private NearByAdaptor mAdapterContacts;
    View emptyNear,emptyContacts;
    Switch visibility;
    private Request.Priority priority = Request.Priority.HIGH;
    int visible;
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
        recyclerView = (EmptyRecyclerView) myView.findViewById(R.id.recycler_view);
        recyclerViewContacts = (EmptyRecyclerView) myView.findViewById(R.id.recycler_view_contact);
         emptyNear = myView.findViewById(R.id.near_empty);
        emptyContacts = myView.findViewById(R.id.contacts_empty);




        mAdapter = new NearByAdaptor(movieList,getContext());
        mAdapterContacts = new NearByAdaptor(movieListContacts,getContext());

        visibility = (Switch)myView.findViewById(R.id.visibility);
        //Just got here check if visibility has been set already
        if(!Visibility.listAll(Visibility.class).isEmpty())
        {

            visibility.setChecked(Visibility.first(Visibility.class).isStatus());
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
                final List<NearBy> ListContacts = new ArrayList<>();
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
                for (int i = 0; i< movieListContacts.size(); i++) {
                    final NearBy text = movieListContacts.get(i);
                    if (text.getTelephone_number().startsWith(query) || text.getTelephone_number().contains(query) || text.getTelephone_number().endsWith(query)) {
                        ListContacts.add(movieListContacts.get(i));
                    }
                    if (text.getName().toLowerCase().contains(query) || text.getName().toLowerCase().startsWith(query) || text.getName().toLowerCase().endsWith(query)) {
                        ListContacts.add(movieListContacts.get(i));
                    }
                    if (text.getScreen_name().toLowerCase().contains(query) || text.getScreen_name().toLowerCase().startsWith(query) || text.getScreen_name().toLowerCase().endsWith(query)) {
                        ListContacts.add(movieListContacts.get(i));
                    }
                }


                mAdapter = new NearByAdaptor(List,getContext());
                mAdapterContacts = new NearByAdaptor(ListContacts,getContext());

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerViewContacts.setLayoutManager(mLayoutManager1);
                recyclerView.setEmptyView(emptyNear);
                recyclerViewContacts.setEmptyView(emptyContacts);

                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recyclerViewContacts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());

                recyclerView.setAdapter(mAdapter);
                recyclerViewContacts.setAdapter(mAdapterContacts);

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
                final List<NearBy> ListContacts = new ArrayList<>();
                for (int i = 0; i< movieList.size(); i++) {
                    final NearBy text = movieList.get(i);
                    if (text.getTelephone_number().startsWith(query) || text.getTelephone_number().contains(query) || text.getTelephone_number().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                    if (text.getName().toLowerCase().contains(query) || text.getName().toLowerCase().startsWith(query) || text.getName().toLowerCase().endsWith(query)) {
                        List.add(movieList.get(i));
                    }
                    try {

                        if (text.getScreen_name().toLowerCase().contains(query) || text.getScreen_name().toLowerCase().startsWith(query) || text.getScreen_name().toLowerCase().endsWith(query)) {
                            List.add(movieList.get(i));
                        }

                    }
                    catch (Exception e)
                    {

                    }
                  }
                for (int i = 0; i< movieListContacts.size(); i++) {
                    final NearBy text = movieListContacts.get(i);
                    if (text.getTelephone_number().startsWith(query) || text.getTelephone_number().contains(query) || text.getTelephone_number().endsWith(query)) {
                        ListContacts.add(movieListContacts.get(i));
                    }
                    if (text.getName().toLowerCase().contains(query) || text.getName().toLowerCase().startsWith(query) || text.getName().toLowerCase().endsWith(query)) {
                        ListContacts.add(movieListContacts.get(i));
                    }
                    try{
                        if (text.getScreen_name().toLowerCase().contains(query) || text.getScreen_name().toLowerCase().startsWith(query) || text.getScreen_name().toLowerCase().endsWith(query)) {
                            ListContacts.add(movieListContacts.get(i));
                        }

                    }
                    catch (Exception e)
                    {

                    }
                  }

                mAdapter = new NearByAdaptor(List,getContext());
                mAdapterContacts = new NearByAdaptor(ListContacts,getContext());

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity());

                recyclerView.setLayoutManager(mLayoutManager);
                recyclerViewContacts.setLayoutManager(mLayoutManager1);

                recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                recyclerViewContacts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());

                recyclerView.setAdapter(mAdapter);
                recyclerViewContacts.setAdapter(mAdapterContacts);

                return true;
            }
        };

        search.setOnQueryTextListener(listener);


       // RealmController.with(getActivity()).clearContacts();

        near_by_offline_users();
        mHandlerTask.run();


       // prepareMovieData();
 /*       TwoFragment.readContacts task = new readContacts();
        task.execute("");
*/
        recyclerView.setHasFixedSize(true);
        recyclerViewContacts.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerViewContacts.setLayoutManager(mLayoutManager2);

        recyclerView.setEmptyView(emptyNear);
        recyclerViewContacts.setEmptyView(emptyContacts);



        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewContacts.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        recyclerViewContacts.setAdapter(mAdapterContacts);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NearBy movie = movieList.get(position);
                //Toast.makeText(getActivity(), movie.getServer_id() + " is selected!", Toast.LENGTH_SHORT).show();
                if(movie.getOn_verifie().equalsIgnoreCase("0"))
                {
                    //Not On service so only invitation

                    try {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", "Check out verifie on your smart phone download it at https://drive.google.com/open?id=0B_hBEdD_-DHUcVFjWnZTb01ZMEk");
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        sendIntent.setData(Uri.parse("sms:"+movie.getTelephone_number()));
                        getActivity().startActivity(sendIntent);
                    }

                    catch (Exception e) {
                        Toast.makeText(getActivity(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
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
        recyclerViewContacts.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerViewContacts, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NearBy movie = movieListContacts.get(position);
                //Toast.makeText(getActivity(), movie.getServer_id() + " is selected!", Toast.LENGTH_SHORT).show();
                if(movie.getOn_verifie().equalsIgnoreCase("0"))
                {
                    //Not On service so only invitation

                    try {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.putExtra("sms_body", "Check out verifie on your smart phone download it at https://drive.google.com/open?id=0B_hBEdD_-DHUcVFjWnZTb01ZMEk");
                        sendIntent.setType("vnd.android-dir/mms-sms");
                        sendIntent.setData(Uri.parse("sms:"+movie.getTelephone_number()));
                        getActivity().startActivity(sendIntent);
                    }

                    catch (Exception e) {
                        Toast.makeText(getActivity(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
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
        params.put("server_id", User.first(User.class).getServer_id());

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
                               Visibility.deleteAll(Visibility.class);
                                Visibility user = new Visibility(visibility.isChecked());
                               user.save();
                                if(visibility.isChecked())
                                {
                                    Toast.makeText(getActivity(),"Your visibility is on",Toast.LENGTH_LONG).show();
                                    near_by_users();
                                }
                                else
                                {
                                    movieList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(),"Your visibility is off",Toast.LENGTH_LONG).show();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

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
        params.put("server_id", User.first(User.class).getServer_id());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"check_user_visibility",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        dialog.hide();
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                              Visibility.deleteAll(Visibility.class);
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

                               user.save();
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
                Toast.makeText(getActivity(),"Sorry A Network Error Occurred Visibility",Toast.LENGTH_LONG).show();
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

    void near_by_users()
    {

        final String tag = "new_user_logn";


        Map<String, String> params = new HashMap<String, String>();
        params.put("server_id", User.first(User.class).getServer_id());
        params.put("longitude", Location_Stats.first(Location_Stats.class).getLongitude()+"");
        params.put("latitude",Location_Stats.first(Location_Stats.class).getLatitude()+"");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"near_users",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        try {

                            if(response.get("status").toString().equalsIgnoreCase("1"))
                            {
                                JSONArray jaa = response.getJSONArray("data");
                                movieList.clear();


                                for (int i=0; i< jaa.length(); i++)
                                {

                                    JSONObject object = (JSONObject) jaa.get(i);
                                    User us = User.first(User.class);
                                    if(!us.getServer_id().equalsIgnoreCase(object.get("id").toString()))
                                    {
                                        NearBy dumb = new NearBy(object.get("fullname").toString(),object.get("file_name").toString(),object.get("telephone").toString(),object.get("id").toString(),object.get("screen_name").toString());
                                        if(check_new(dumb))
                                        {
                                            movieList.add(dumb);
                                        }
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

    }


    void near_by_offline_users()
    {
        
        movieListContacts.clear();

        List<ContactsList> RegisteredCls = Select.from(ContactsList.class).where(Condition.prop("isonverifie").eq("1")).orderBy("name").list();
        List<ContactsList> UnRegisteredCls = Select.from(ContactsList.class).where(Condition.prop("isonverifie").eq("0")).orderBy("name").list();
        //TODO sort
        for (int ii = 0; ii < RegisteredCls.size(); ii++)
        {
            ContactsList cl = RegisteredCls.get(ii);
            NearBy dumb = new NearBy(cl.getName(),cl.getFile_name(),cl.getTelephone(),cl.getServer_id()+"","",cl.getIs_on_verifie(),cl.getId()+"",cl.getScreen_name());
            if(check_new(dumb))
            {
                movieListContacts.add(dumb);
            }
        }
        for (int ii = 0; ii < UnRegisteredCls.size(); ii++)
        {
            ContactsList cl = UnRegisteredCls.get(ii);
            NearBy dumb = new NearBy(cl.getName(),"",cl.getTelephone(),cl.getServer_id()+"","",cl.getIs_on_verifie(),cl.getId()+"",cl.getScreen_name());
            if(check_new(dumb))
            {
                movieListContacts.add(dumb);
            }
        }

        mAdapterContacts.notifyDataSetChanged();
    }


    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            if(!Visibility.listAll(Visibility.class).isEmpty() && !Location_Stats.listAll(Location_Stats.class).isEmpty())
            {
                if(Visibility.first(Visibility.class).isStatus())
                {
                    near_by_users();
                }
                else

                {

                }
            }
            mHandler.postDelayed(mHandlerTask, (1000 * 2 * 1));
        }
    };



    private class readContacts extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            check_on_verifie();
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
                                    ContactsList contactsList = ContactsList.findById(ContactsList.class,(long)finalI);
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
        near_by_offline_users();
    }

    void call_method(String server_id, final NearBy nearBy)
    {
        String text="Request";


        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//realm.where(ReceivedRequests.class).contains("",server_id).findAll().isEmpty()
        if(Select.from(ReceivedRequests.class).where(Condition.prop("idsend").eq(server_id)).list().isEmpty())
        {
            //we have not received request from this user
            //lets check if we have sent to user
            if(Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).list().isEmpty())
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
                        dialog.dismiss();
                        requestRotabar(nearBy.getServer_id());

                    }
                });

            }
            else
            {
                //we have sent so lets check the result
                if(Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).first().getReply() == 1)
                {
                    //accepted
                    text = "View RotaBar";
                   // dialog.setContentView(R.layout.view_rotabar);

                    SentRequests rr = Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).first();

                    SentRequests.delete(rr);


                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("dd/M/yyyy");

                    Float level = 0f;
                    ApprovedRequests vss = Select.from(ApprovedRequests.class).where(Condition.prop("serverid").eq(server_id)).first();

                    try {
                        Calendar calendar = Calendar.getInstance();
                        String strDate = "" + simpleDateFormat.format(calendar.getTime());
                        if(!vss.getDate_to_expire().equalsIgnoreCase("N/A"))
                        {
                            Date date1 = simpleDateFormat.parse(strDate);
                            Date date2 = simpleDateFormat.parse(vss.getDate_to_expire());
                            Long t =  printDifference(date1, date2)/7;
                            //System.out.println("Difference "+t+ " "+vss.getDate_to_expire()+" "+date1.toString());
                            float tt = t/60f;
                            level =  tt*100;

                        }
                        else
                        {
                            level = 0f;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        level = 0f;
                    }



                    if(level <= 25f)
                    {
                        dialog.setContentView(R.layout.pop1);
                    }
                    if(level > 25f && level <= 65f)
                    {
                        dialog.setContentView(R.layout.pop2);
                    }
                    if(level > 65f && level <= 100f)
                    {
                        dialog.setContentView(R.layout.pop3);
                    }
                    ProgressProfileView profile = (ProgressProfileView) dialog.findViewById(R.id.profile);

                    profile.setProgress(level);

                    profile.startAnimation();

                    TextView percentage = (TextView)dialog.findViewById(R.id.percent_view) ;
                    percentage.setText( level.intValue()+"%");


                    String url = User.first(User.class).getFile_name();
                    server_id = User.first(User.class).getServer_id();
                    try {
                        Picasso.with(getActivity()).load(Api.getImage_end()+nearBy.getServer_id()).into(profile);
                    }
                    catch (Exception e)
                    {
                        System.out.println(url);
                        e.printStackTrace();
                    }
                }
                else
                {
                    //realm.where(SentRequests.class).contains("",server_id).findAll().first()
                    if (Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).first().getTime_replied().equalsIgnoreCase(""))
                    {
                        //request Pending
                        text = "Request Denied";
                        //dialog.setContentView(R.layout.request_denied);
                        Toast.makeText(getContext(),"Request Still Pending Reply",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        //TODO DELETE

                        //request denied
                        text = "Request Denied";
                        try
                        {
                            SentRequests rr = Select.from(SentRequests.class).where(Condition.prop("idreceipent").eq(server_id)).first();
                            SentRequests.delete(rr);
                            dialog.setContentView(R.layout.request_denied);

                        }
                        catch (Exception e)
                        {

                            e.printStackTrace();
                        }
                     }

                }
            }



        }
        else
        {
            //we have received request from this user so lets see the result
            if(Select.from(ReceivedRequests.class).where(Condition.prop("idsend").eq(server_id)).first().getStatus().equalsIgnoreCase("0"))
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
                        replyRequest(nearBy.getServer_id(),""+0);

                    }
                });
                allow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        replyRequest(nearBy.getServer_id(),""+1);
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
                        dialog.dismiss();
                        requestRotabar(nearBy.getServer_id());
                    }
                });


            }
        }
        dialog.show();
    }

    void requestRotabar(final String server_id)
    {

                // Tag used to cancel the request
                  //  System.out.println("Sending To -- "+server_id);

                //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
                final String tag = "new_user_login";
                final ProgressDialog pd = ProgressDialog.show(getActivity(),"Contacting Server ..."," Please Wait Submitting Your Request ...", true);
         Map<String, String> params = new HashMap<String, String>();
                params.put("to_user", server_id);
                params.put("server_id", User.first(User.class).getServer_id());
                pd.show();

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        Api.getApi()+"request_rollarbar",new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                               // System.out.print(response.toString());
                                pd.hide();
                                try {
                                    Long tsLong = System.currentTimeMillis()/1000;
                                    String tsr = tsLong.toString();

                                    SentRequests sr = new SentRequests(server_id,tsr,"","0",0);
                                    // public SentRequests(int id, String id_receipent, String time_sent, String time_replied, String status, int reply)
                                    sr.save();

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                   // Toast.makeText(getActivity(), "Sorry An Error Occurred Please Try Again Later", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Sorry A Network Error Occurred Please Try Again Later",Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                        pd.hide();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                    @Override
                    public Priority getPriority() {
                        return priority;
                    }
                };
// Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);



            //send request to server



    }
    void replyRequest(final String server_id, final String reply)
    {

        // Tag used to cancel the request

        //`users`(`id`, `fullname`, `login_type`, `security_code`, `extra_code`, `id_from_provider`, `telephone`, `file_blob`, `file_name`, `is_visible`, `visibility_code`, ``, ``, ``, ``, ``)
        final String tag = "new_user_login";
        final ProgressDialog pd = ProgressDialog.show(getActivity(),"Contacting Server ..."," Please Wait Submitting Your Reply ...", true);

        Map<String, String> params = new HashMap<String, String>();
        params.put("to_user", server_id);
        params.put("reply", reply);
        params.put("server_id", User.first((User.class)).getServer_id());
        pd.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Api.getApi()+"reply_rollarbar",new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response.toString());
                        pd.hide();
                        try {
                            Long tsLong = System.currentTimeMillis()/1000;
                            String tsr = tsLong.toString();

                            ReceivedRequests rr = Select.from(ReceivedRequests.class).where(Condition.prop("idsend").eq(server_id)).first();
                            // public SentRequests(int id, String id_receipent, String time_sent, String time_replied, String status, int reply)
                            ReceivedRequests.delete(rr);

                        }
                        catch (Exception e)
                        {
                           e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Sorry A Network Error Occurred Please Try Again Later",Toast.LENGTH_LONG).show();
                error.printStackTrace();
                pd.hide();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
            @Override
            public Priority getPriority() {
                return priority;
            }
        };
// Adding request to request queue
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag);

        //send request to server


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



    boolean check_new(NearBy dumb)
    {

            for (int i=0; i<movieList.size(); i++)
            {
                NearBy temp = movieList.get(i);
                if(dumb.getTelephone_number().equalsIgnoreCase(temp.getTelephone_number()))
                {
                    return false;
                }

            }

        return  true;
    }


}

//