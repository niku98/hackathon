package com.example.vinid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Profile extends AppCompatActivity {
    private TextView tv_id;
    private TextView tv_name;
    private TextView tv_phone;
    private TextView tv_address;
    private String user_id = "";
    private BenhNhan benhNhan = new BenhNhan();

    private void getUserInfomation(){
        try{
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        String str = "http://203.162.13.40/get_user?user_id="+user_id;
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpPost = new HttpGet(str);
                        HttpResponse response = httpClient.execute(httpPost);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                        String response_body = reader.readLine();
                        JSONObject jsonObject = new JSONObject(response_body);
                        benhNhan = new BenhNhan(user_id, jsonObject.getString("Name"), jsonObject.getString("Address"), jsonObject.getString("Phone"));
                    } catch (IOException e) {
                        System.out.println(e);
                    } catch (NetworkOnMainThreadException e) {
                        System.out.println(e);
                    } catch(JSONException e){
                        System.out.println(e);
                    }
                }
            });
            t.start();
            t.join();
        } catch(InterruptedException e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    private void init() {
        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_address = findViewById(R.id.tv_address);
        getSupportActionBar().hide();
        user_id = getIntent().getStringExtra("EXTRA_SESSION_ID");
        getUserInfomation();
        try{
            tv_id.setText(user_id);
            tv_name.setText(benhNhan.getName());
            tv_phone.setText(benhNhan.getPhone());
            tv_address.setText(benhNhan.getAddress());
        } catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}
