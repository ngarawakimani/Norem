package com.kd3developers.norem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://192.168.137.1/android/norem/messages.php";
    List<MsgItems> list;
    RecyclerView recyclerView;
    MsgRecyclerAdapter adapter;
    Observable<List<MsgItems>> observable;

    String userId;
    String plotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = this.getIntent();

        userId = intent.getExtras().getString("ID").toString();
        plotId = intent.getExtras().getString("PLOT_ID").toString();

        recyclerView = (RecyclerView)findViewById(R.id.message_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        observable = Observable.create(new ObservableOnSubscribe<List<MsgItems>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MsgItems>> e) throws Exception {
                try {
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
                .subscribe(new Consumer<List<MsgItems>>() {
                    @Override
                    public void accept(List<MsgItems> itemsLists) throws Exception {
                        adapter = new MsgRecyclerAdapter(getApplicationContext(),itemsLists);
                        recyclerView.setAdapter(adapter);
                    }
                });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {

                MsgRowHolder holder = new MsgRowHolder(view);

                try {
                    openDetailActivity(holder.message.getText().toString());
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_LONG);
                }

            }
        }));


    }

    public List<MsgItems> getOkData(String url) throws IOException, JSONException {

        OkHttpClient client = new OkHttpClient();

        MsgItems items;
        RequestBody body = new FormBody.Builder()
                .add("id",userId)
                .add("plot",plotId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        String result = response.body().string();

        JSONArray posts = new JSONArray(result);
        JSONObject object;

        list = new ArrayList<>();


        for (int i = 0; i < posts.length(); i++) {
            object = posts.getJSONObject(i);

                items = new MsgItems(object.getString("headline"), object.getString("topic"), object.getString("date"));

                list.add(items);
            }

        return list;
    }
    public void openDetailActivity(String message){

        Intent intent = new Intent(this,MsgDetailsActivity.class);

        intent.putExtra("MESSAGE",message);

        startActivity(intent);
    }
}
