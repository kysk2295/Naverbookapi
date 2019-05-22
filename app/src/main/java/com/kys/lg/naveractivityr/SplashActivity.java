package com.kys.lg.naveractivityr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SplashActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    int cnt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        linearLayout=findViewById(R.id.linear);

        bhandler.sendEmptyMessage(0);


    }
   Handler bhandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            bhandler.sendEmptyMessageDelayed(0,1000);
            cnt++;
            if(cnt==3){
                bhandler.removeMessages(0);
                linearLayout.setBackgroundColor(Color.parseColor("#ae5f2a"));
                Intent i = new Intent(SplashActivity.this,NaverActivity.class);
                startActivity(i);
                SplashActivity.this.finish();
            }


        }
    };

}
