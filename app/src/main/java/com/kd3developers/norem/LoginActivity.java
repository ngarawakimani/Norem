package com.kd3developers.norem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginActivity extends AppCompatActivity{


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private EditText mIDView;
    private EditText mPasswordView;
    private TextView sign_up;
    private View mLoginFormView;

    String id;
    String password;

    public final String SERVER_URL = "http://192.168.137.1/android/norem/login.php";

    OkHttpClient client = new OkHttpClient();
    Observable<String> observable;

    Session session;

    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);

        // Set up the login form.
        mIDView = (EditText) findViewById(R.id.input_id);

        mPasswordView = (EditText) findViewById(R.id.input_password);

        Button signInButton = (Button) findViewById(R.id.btn_login);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                }catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Sorry!....Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sign_up = (TextView)findViewById(R.id.link_signup);
        sign_up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent("com.kd3developers.norem.SignupActivity"));
                }catch(Exception e){
                    Toast.makeText(getBaseContext(), "Sorry!....Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mIDView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        id = mIDView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid id no, if the user entered one.
        if (TextUtils.isEmpty(id) || !isIDValid(id)) {
            mIDView.setError("Invalid ID Number");
            focusView = mIDView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError("Invalid Password");
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            observable = Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    try {
                        e.onNext(postLoginDetails(SERVER_URL));
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

                            if (s.equals("Invalid Credentials")){
                                Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
                            }else{
                                session.createUserLoginSession(s,password);

                                // Starting MainActivity
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                // Add new Flag to start new Activity
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);

                                finish();
                            }
                        }
                    });

        }
    }

    private boolean isIDValid(String id) {
        return id.length() ==8;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    public String postLoginDetails(String url) throws IOException {

        String user_id = "";

        RequestBody body = new FormBody.Builder()
                .add("id",id)
                .add("password", password)
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

                user_id = object.getString("id");


            }
        } catch (JSONException e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return user_id;
    }

}

