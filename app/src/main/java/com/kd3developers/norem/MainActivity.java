package com.kd3developers.norem;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SERVER_URL = "http://192.168.137.1/android/norem/plotsdata.php";
    RecyclerView recyclerView;
    DataRecyclerAdapter dataRecyclerAdapter;
    private List<DataItems> listItems;

    Session session;

    String user_id;
    String plot_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new Session(this);
        // get user data from session
        if(session.checkLogin())
            finish();
            // get user data from session
            HashMap<String, String> user = session.getUserDetails();

            // get user id
            user_id = user.get(Session.KEY_ID);

            // get user password
            String password = user.get(Session.KEY_PASSWORD);

        //listItems = listOfItems();
        recyclerView = (RecyclerView)findViewById(R.id.myrecycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Observable<List<DataItems>> observable = Observable.create(new ObservableOnSubscribe<List<DataItems>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DataItems>> e) throws Exception {
                try {
                    //String data = "";
                    e.onNext(getOkData(SERVER_URL));
                    e.onComplete();
                } catch (Exception ex){
                    e.onError(ex);
                    Toast.makeText(getApplicationContext(),"Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DataItems>>() {
                    @Override
                    public void accept(List<DataItems> list) throws Exception {
                        dataRecyclerAdapter = new DataRecyclerAdapter(getApplicationContext(),list);
                        recyclerView.setAdapter(dataRecyclerAdapter);
                    }
                });


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),new RecyclerItemClickListener.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int i){
                DataRowHolder holder = new DataRowHolder(view);

                final String heading = holder.heading.getText().toString();
                final String description = holder.description.getText().toString();
                final String price = holder.price.getText().toString();
                final String beds = holder.bed_space.getText().toString();


                Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        try {
                            e.onNext(getbookingPlotId(heading));
                            e.onComplete();
                        } catch (Exception ex){
                            e.onError(ex);
                            Toast.makeText(getApplicationContext(),"Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Exception {

                                //Toast.makeText(getApplicationContext(),user_id + " " +s+ " " + heading+" "+description+" "+price+" "+beds,Toast.LENGTH_LONG).show();

                                try {
                                    openPlotActivity(user_id,s,heading,description,price,beds);
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG);
                                }
                            }
                        });


            }
        }));

    }
    public String getbookingPlotId(String plot) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("plot_name",plot)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.137.1/android/norem/plotdetails.php")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }

        String result = response.body().string();

        try {
            JSONArray posts = new JSONArray(result);
            JSONObject object;

            for (int i = 0; i < posts.length(); i++) {
                object = posts.getJSONObject(i);

                result = object.getString("id");

            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }

    public String getPlotId(String id) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("id",id)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.137.1/android/norem/plotid.php")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }

        String result = response.body().string();

        try {
            JSONArray posts = new JSONArray(result);
            JSONObject object;

            for (int i = 0; i < posts.length(); i++) {
                object = posts.getJSONObject(i);

                result = object.getString("plot_id");

            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return result;
    }


    public List<DataItems> getOkData(String url) throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();
        Response response = null;
        DataItems items;
        try {

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            response = client.newCall(request).execute();


        }catch (Exception e){

        }

        String result = response.body().string();

        JSONArray posts = new JSONArray(result);
        JSONObject object;

        listItems = new ArrayList<DataItems>();


        for (int i = 0; i < posts.length(); i++) {
            object = posts.getJSONObject(i);

            items = new DataItems(object.getString("image"), object.getString("name"), object.getString("description"), object.getString("rent"), object.getString("capacity"));

            listItems.add(items);

        }

        return listItems;
    }

    public void openPlotActivity(String user_id,String plot_id,String heading, String description, String price, String beds){

        Intent intent = new Intent(this,PlotActivity.class);

        intent.putExtra("ID",user_id);
        intent.putExtra("PLOTID",plot_id);
        intent.putExtra("HEADING",heading);
        intent.putExtra("DESCRIPTION",description);
        intent.putExtra("PRICE",price);
        intent.putExtra("BEDS",beds);

        startActivity(intent);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            try{
                //log out the user
                session.logoutUser();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_myRoom) {
            try {

                Intent intent = new Intent(this,RoomActivity.class);

                intent.putExtra("ID",user_id);

                startActivity(intent);

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_messages) {

            Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    try {
                        e.onNext(getPlotId(user_id));
                        e.onComplete();
                    } catch (Exception ex){
                        e.onError(ex);
                        Toast.makeText(getApplicationContext(),"Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //Toast.makeText(getApplicationContext(), user_id + " " + s, Toast.LENGTH_LONG).show();
                            try {
                                Intent intent = new Intent("com.kd3developers.norem.MessageActivity");

                                intent.putExtra("ID",user_id);
                                intent.putExtra("PLOT_ID",s);

                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else if (id == R.id.nav_about) {
            try {
                startActivity(new Intent("com.kd3developers.norem.AboutActivity"));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
