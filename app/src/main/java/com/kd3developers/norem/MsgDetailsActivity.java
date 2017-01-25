package com.kd3developers.norem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MsgDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_details);

        TextView show_message = (TextView)findViewById(R.id.message_detail);
        Intent intent = this.getIntent();

        String message = intent.getExtras().getString("MESSAGE");
        show_message.setText(message);
    }
}
