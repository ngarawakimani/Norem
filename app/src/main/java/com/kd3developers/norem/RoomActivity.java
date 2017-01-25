package com.kd3developers.norem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

public class RoomActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://192.168.137.1/android/norem/room.php";
    OkHttpClient client = new OkHttpClient();
    Observable<String> observable;

    TextView roomName;
    TextView roomRent;
    TextView roomDetails;
    TextView roomCondition;
    TextView roomCapacity;

    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Intent intent = this.getIntent();

        user_id = intent.getExtras().getString("ID").toString();
        //Toast.makeText(getBaseContext(),user_id,Toast.LENGTH_LONG).show();

        roomName = (TextView)findViewById(R.id.name);
        roomRent = (TextView)findViewById(R.id.rent);
        roomDetails = (TextView)findViewById(R.id.details);
        roomCondition = (TextView)findViewById(R.id.condition);
        roomCapacity = (TextView)findViewById(R.id.capacity);

        observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    e.onNext(postId(SERVER_URL,user_id));
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
                        //Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public String postId(String url,String id) throws IOException {

        String status = "";

        RequestBody body = new FormBody.Builder()
                .add("id",id)
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

                roomName.setText(object.getString("roomName"));
                roomRent.setText(object.getString("rent"));
                roomDetails.setText(object.getString("details"));
                roomCondition.setText(object.getString("Conditin"));
                roomCapacity.setText(object.getString("capacity"));


            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return status;
    }
}
