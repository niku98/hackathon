package com.example.vinid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowQueue extends AppCompatActivity {
    TextView tv_room, tv_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_queue);
        getSupportActionBar().hide();
        init();
    }

    private void init() {
        tv_room = findViewById(R.id.tv_room);
        tv_number = findViewById(R.id.tv_number);
        String room = getIntent().getStringExtra("ROOM");
        String farculty = getIntent().getStringExtra("FACULTY");
        String number = getIntent().getStringExtra("NUMBER");
        tv_room.setText("Phòng " + room + " - Khoa: " + farculty);
        tv_number.setText(number);
    }
    @Override
    public void onBackPressed() {
        Intent intentHome = new Intent(ShowQueue.this, MainActivity.class);
        startActivity(intentHome);
    }
}
