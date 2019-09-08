package com.example.vinid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    String outp = "";
    String id, user;
    Button btn_profile, btn_kham, btn_booking, btn_payment;
    TextView tv_queue, tv_hello;
    String idFinal = "", idBenh = "";
    Intent intentKhamBenh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri uri = intent.getData();
        String id, name;
        if (uri != null) {
            id = uri.getQueryParameter("user_id");
            name = uri.getQueryParameter("name");
            saveProfile(id);

        }

        loadProfile();
        getQueue();
        tv_hello.setText("Bạn chưa có số");
    }

    private void getQueue() {
        try {
            String url = "http://203.162.13.40/sick_submit?queue_id=" + idBenh + "&user_id=" + idFinal;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonNumber = jsonObject.getJSONObject("number_queue");
                                JSONObject jsonRoom = jsonObject.getJSONObject("Room");
                                JSONObject jsonFaculty = jsonObject.getJSONObject("Faculty");
                                Log.d("Respon:", response);
                                outp = jsonNumber.getString("count");
                                tv_queue.setText(outp + "/Phòng:" + jsonRoom.getString("Number") + " Khoa" + jsonFaculty.getString("Name"));
                                if (!outp.isEmpty()) {
                                    tv_hello.setText("Số của bạn là:");
                                }
                                Log.d("Respon:", outp);

                                //tv_queue.setText(outp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Didn't work", Toast.LENGTH_LONG).show();

                }
            });

            DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            requestQueue.add(stringRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveProfile(String id) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences == null) {
            editor.putString("ID", id);
        } else {
            String preID = sharedPreferences.getString("ID", "");
            if (preID.equals(id) == false || preID.length() == 0) {
                editor.putString("ID", id);
            }
        }
        editor.commit();

    }

    private void loadProfile() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Profile", Context.MODE_PRIVATE);
        idFinal = sharedPreferences.getString("ID", "");
        idBenh = sharedPreferences.getString("IDQUEUE", "");
        tv_queue.setText("");
    }

    private void init() {
        btn_booking = findViewById(R.id.btn_booking);
        btn_kham = findViewById(R.id.btn_kham);
        btn_payment = findViewById(R.id.btn_payment);
        btn_profile = findViewById(R.id.btn_profile);
        tv_queue = findViewById(R.id.tv_queue);
        tv_hello = findViewById(R.id.tv_hello);

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentProfile = new Intent(MainActivity.this, Profile.class);
                intentProfile.putExtra("EXTRA_SESSION_ID", idFinal);
                startActivity(intentProfile);
            }
        });

        btn_kham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentKhamBenh = new Intent(MainActivity.this, KhamBenh.class);
                intentKhamBenh.putExtra("EXTRA_SESSION_ID", idFinal);
                startActivity(intentKhamBenh);
            }
        });
    }


}
