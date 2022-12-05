package com.takecarefridge;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import io.grpc.okhttp.internal.framed.FrameReader;

public class LoadingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        moveMain(2);
    }
    private void moveMain(int sec) {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //new Intent(현재 context, 이동할 activity)
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);

                startActivity(intent);	//intent 에 명시된 액티비티로 이동

                finish();	//현재 액티비티 종료
            }
        }, 1000 * sec); // sec초 정도 딜레이를 준 후 시작
    }
}