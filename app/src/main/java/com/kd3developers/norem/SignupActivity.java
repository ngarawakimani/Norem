package com.kd3developers.norem;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    private static final int PICK_FROM_FILE = 2;
    Uri image_uri;

    public final String SERVER_URL = "http://192.168.137.1/android/norem/register.php";

    OkHttpClient client = new OkHttpClient();
    Observable<String> observable;

    Button btn_photo;
    Button btn_signup;

    ImageView user_image;
    EditText firstname;
    EditText secondname;
    EditText id;
    EditText email;
    EditText phone;
    RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    EditText password;
    EditText password2;

    TextView signup_response;

    String mfirstname;
    String msecondname;
    String mid;
    String memail;
    String mphone;
    String gender;
    String mpassword;
    String mpassword2;

    String userPhoto;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //assert getSupportActionBar() != null;
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        user_image = (ImageView)findViewById(R.id.user_image);

        firstname = (EditText)findViewById(R.id.input_fname);
        secondname = (EditText)findViewById(R.id.input_sname);
        id = (EditText)findViewById(R.id.input_idnumber);
        email = (EditText)findViewById(R.id.input_email);
        phone = (EditText)findViewById(R.id.input_mobile);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);
        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               // find which radio button is selected
               if(checkedId == R.id.radioMale) {
                   gender = "Male";

                } else if(checkedId == R.id.radioFemale) {
                   gender="Female";

                }
            }

        });
        //radioSexButton = (RadioButton) findViewById(radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()));
        password = (EditText)findViewById(R.id.input_password);
        password2 = (EditText)findViewById(R.id.input_reEnterPassword);

        signup_response = (TextView)findViewById(R.id.signup_response);

        btn_photo = (Button)findViewById(R.id.btn_photo);
        btn_signup = (Button)findViewById(R.id.btn_signup);

        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
                try {
                    startActivityForResult(Intent.createChooser(intent, "Complete Action Using"), PICK_FROM_FILE);
                }catch(Exception e){
                    Toast.makeText(getBaseContext(), "unnable to open gallery", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    attemptRegistration();
                }catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Sorry....Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        String path = "";
        if (requestCode == PICK_FROM_FILE){
            image_uri = data.getData();
            path = getRealPathFromURI(image_uri);
            if (path == null){
                path = image_uri.getPath();
            }else{
                bitmap = BitmapFactory.decodeFile(path);
            }
        }

        user_image.setImageBitmap(bitmap);
    }

    private String getRealPathFromURI(Uri content_uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(content_uri,proj,null,null,null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void attemptRegistration() {

        // Reset errors.
        firstname.setError(null);
        secondname.setError(null);
        id.setError(null);
        email.setError(null);
        phone.setError(null);
        password.setError(null);
        password2.setError(null);

        // Store values at the time of the login attempt.
        mfirstname = firstname.getText().toString();
        msecondname = secondname.getText().toString();
        mid = id.getText().toString();
        memail = email.getText().toString();
        mphone = phone.getText().toString();
        //gender = radioSexButton.getText().toString();
        mpassword = password.getText().toString();
        mpassword2 = password2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(mfirstname) || !isnameValid(mfirstname)) {
            firstname.setError("Invalid name");
            focusView = firstname;
            cancel = true;
        }
        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(msecondname) || !isnameValid(msecondname)) {
            secondname.setError("Invalid name");
            focusView = secondname;
            cancel = true;
        }
        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(mid) || !isidValid(mid)) {
            id.setError("Invalid id number");
            focusView = id;
            cancel = true;
        }
        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(memail) || !isemailValid(memail) || !android.util.Patterns.EMAIL_ADDRESS.matcher(memail).matches()) {
            email.setError("Invalid email");
            focusView = email;
            cancel = true;
        }
        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(mphone) || !isphoneValid(mphone)) {
            phone.setError("Invalid phone number");
            focusView = phone;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mpassword) || !isPasswordValid(mpassword)) {
            password.setError("Invalid Password");
            focusView = password;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mpassword2) || !isPasswordValid(mpassword2)) {
            password2.setError("Invalid Password");
            focusView = password2;
            cancel = true;
        }
        //check if passwords do match
        if (!mpassword2.equals(mpassword)){
            password2.setError("Passwords do not match");
            focusView = password2;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //Toast.makeText(getBaseContext(),mfirstname+" "+msecondname+" "+ mid+" "+ memail +" "+mphone +" "+gender+" "+ mpassword+" "+ mpassword2,Toast.LENGTH_LONG).show();
            registerUser();

        }
    }

    private boolean isnameValid(String username) {
        return username.length() >4;
    }
    private boolean isidValid(String id) {
        return id.length() == 8;
    }
    private boolean isemailValid(String email) {
        return email.length() > 6;
    }
    private boolean isphoneValid(String phone) {
        return phone.length() == 10;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    private void registerUser() {
        observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    e.onNext(postRegisterDetails(SERVER_URL));
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
                        signup_response.setText(s);
                        Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
                    }
                });
    }

    public String postRegisterDetails(String url) throws IOException {


        final MediaType MEDIA_TYPE = MediaType.parse("image/*");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] array = stream.toByteArray();
        userPhoto = Base64.encodeToString(array,0);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file",mfirstname+msecondname+".png",RequestBody.create(MEDIA_TYPE, userPhoto))
                .addFormDataPart("firstname", mfirstname)
                .addFormDataPart("secondname", msecondname)
                .addFormDataPart("id", mid)
                .addFormDataPart("email", memail)
                .addFormDataPart("phone", mphone)
                .addFormDataPart("gender", gender)
                .addFormDataPart("password", mpassword)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()){
            throw new IOException("Unexpected code " + response);
        }


        return response.body().string();
    }


}
