package com.kd3developers.norem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.list;

public class PlotActivity extends AppCompatActivity {

    public final String SERVER_URL = "http://192.168.137.1/android/norem/request.php";

    OkHttpClient client = new OkHttpClient();
    Observable<List<String>> observable;
    Observable<String> observable1;
    List<String> list;

    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;

    Session session;

    ImageView plotImage;
    TextView plotDescription;
    TextView plotPrice;
    TextView plotBedSpaces;
    TextView rooms_status;
    TextView room_number;
    Spinner spinner_room;
    Spinner spinner;
    Button book;

    String user_id;
    String plot_id;
    String heading;
    String description;
    String price;
    String beds;
    String rent;

    String id;
    String password;
    String room;
    String duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);

        plotImage = (ImageView)findViewById(R.id.plot_image);
        plotDescription = (TextView)findViewById(R.id.container_description);
        plotPrice = (TextView)findViewById(R.id.container_price);
        plotBedSpaces = (TextView)findViewById(R.id.bed_spaces);
        spinner_room = (Spinner)findViewById(R.id.spinner_rooms);
        //rooms_status = (TextView)findViewById(R.id.rooms_status);


        Intent intent = this.getIntent();

        user_id = intent.getExtras().getString("ID").toString();
        plot_id = intent.getExtras().getString("PLOTID").toString();
        heading = intent.getExtras().getString("HEADING").toString();
        description = intent.getExtras().getString("DESCRIPTION").toString();
        price = intent.getExtras().getString("PRICE").toString();
        beds = intent.getExtras().getString("BEDS").toString();

        //Toast.makeText(getApplicationContext(),user_id + " " +plot_id+ " " + heading+" "+description+" "+price+" "+beds,Toast.LENGTH_LONG).show();

        //getDrawable(Integer.parseInt(image));
        //plotImage.setImageResource(image);
        plotDescription.setText(description);
        plotPrice.setText("Kshs "+price+" PM");
        plotBedSpaces.setText(beds);


        session = new Session(getApplicationContext());

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(heading);


        observable = Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> e) throws Exception {
                try {
                    e.onNext(getRoom(plot_id));
                    e.onComplete();
                } catch (Exception ex){
                    e.onError(ex);
                    Toast.makeText(getApplicationContext(),"Error!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>> () {
                    @Override
                    public void accept(List<String> s) throws Exception {
                        //rooms_status.setText("Rooms left -> "+s);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_items,s);
                        spinner_room.setAdapter(spinnerAdapter);
                    }
                });


        spinner = (Spinner)findViewById(R.id.spinner);
        String[] time = {"1 Month","3 Months","4 Months","9 Months","12 Months"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_items,time);
        spinner.setAdapter(spinnerAdapter);

        //room_number = (TextView)findViewById(R.id.room_number);

        book = (Button)findViewById(R.id.btn_book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                duration = spinner.getSelectedItem().toString();
                room = spinner_room.getSelectedItem().toString();
                //Toast.makeText(getBaseContext(),duration+" "+room,Toast.LENGTH_LONG).show();

                if (duration=="1 Month"){
                    duration="1";
                }else if(duration=="3 Months"){
                    duration="3";
                }else if(duration=="4 Months"){
                    duration="4";
                }else if(duration=="9 Months"){
                    duration="9";
                }else if(duration=="12 Months"){
                    duration="12";
                }
                //Toast.makeText(getBaseContext(),user_id+" "+plot_id+" "+room +" "+ duration,Toast.LENGTH_LONG).show();

                    //postDetails(SERVER_URL,user_id,plot_id,duration,room);
                    observable1 = Observable.create(new ObservableOnSubscribe<String>() {
                        @Override
                        public void subscribe(ObservableEmitter<String> e) throws Exception {
                            try {
                                e.onNext(postDetails(SERVER_URL,user_id,plot_id,room,duration));
                                e.onComplete();
                            } catch (Exception ex){
                                e.onError(ex);

                                Toast.makeText(getApplicationContext(),"Error!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    observable1.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String s) throws Exception {
                                    //sign_up.setText(s);
                                    Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
                                }
                            });
            }
        });


    }

    public List<String> getRoom(String plot) throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("plot",plot)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.137.1/android/norem/roomnumbers.php")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        String result = response.body().string();

        JSONArray posts = new JSONArray(result);
        JSONObject object;

        list = new ArrayList<>();


        for (int i = 0; i < posts.length(); i++) {
            object = posts.getJSONObject(i);

            list.add(object.getString("name"));
        }

        return list;
    }



    public String postDetails(String url,String id,String plot,String room,String duration) throws IOException {

        RequestBody body = new FormBody.Builder()
                .add("id",id)
                .add("plot",plot)
                .add("room",room)
                .add("duration",duration)
                .build();

        Request request = new Request.Builder()
                .url(url)
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

                result = object.getString("status");

            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return  result;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }


}
