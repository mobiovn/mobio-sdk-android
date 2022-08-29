package com.mobio.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SendMoneyActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout llIn;
    private ImageView imvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_send_money);
        init();
    }

    public void init(){
        llIn = findViewById(R.id.ll_in);
        imvBack = findViewById(R.id.imv_back);

        imvBack.setOnClickListener(this);

        llIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imv_back) {
            finish();
        } else if (id == R.id.ll_in) {
            startActivity(new Intent(SendMoneyActivity.this, SendMoneyInActivity.class));
        }
    }
}