package com.example.vinid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class KhamBenh extends AppCompatActivity {
    ArrayList<Benh> listBenh = new ArrayList<Benh>();
    ArrayList<String> listKhoa = new ArrayList<String>();
    Intent itentQueue;
    String name = "", id = "";
    String stt;
    String sessionId;
    SocketIO socketIO;
    TextView tv_test;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kham_benh);
        getSupportActionBar().hide();
        init();

    }

    private void init() {
        getListBenh();
        itentQueue = new Intent(this, ShowQueue.class);
        sessionId = getIntent().getStringExtra("EXTRA_SESSION_ID");
        final ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listKhoa);
        listView.setAdapter(arrayAdapter);
        socketIO = new SocketIO(onNewMessage, onNewConnect, onNewNotify);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                try {
                    final String idBenh = listBenh.get(position).getId();
                    final String url = "http://203.162.13.40/sick_submit?sick_id=" + idBenh + "&user_id=" + sessionId;
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONObject jsonRoom = jsonObject.getJSONObject("Room");
                                        JSONObject jsonFaculty = jsonObject.getJSONObject("Faculty");
                                        JSONObject jsonNumber = jsonObject.getJSONObject("number_queue");
                                        itentQueue.putExtra("ROOM", jsonRoom.getString("Number"));
                                        itentQueue.putExtra("FACULTY", jsonFaculty.getString("Name"));
                                        itentQueue.putExtra("NUMBER", jsonNumber.getString("count"));


                                        SharedPreferences sharedPreferences = getSharedPreferences("Profile", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("IDQUEUE", jsonObject.getString("id"));
                                        editor.commit();
                                        startActivity(itentQueue);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                  //  Toast.makeText(KhamBenh.this, response, Toast.LENGTH_LONG).show();

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(KhamBenh.this, "Didn't work", Toast.LENGTH_LONG).show();

                        }
                    });

                    DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    stringRequest.setRetryPolicy(retryPolicy);
                    requestQueue.add(stringRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    //switch (){
                    //Toast.makeText(SocketIO.this, data.toString(), Toast.LENGTH_LONG).show();
                    Log.d("Respon", data.toString());
                }
            });
        }
    };
    public Emitter.Listener onNewConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    socketIO.detectUser(sessionId);
                }
            });
        }
    };

    public Emitter.Listener onNewNotify = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Toast.makeText(KhamBenh.this, data.toString(), Toast.LENGTH_LONG).show();
                    Log.d("NewNotif", data.toString());
                    try {
                        stt = data.getString("stt");
                        final String CHANNEL_ID = "channel_id";
                        NotificationHelper notificationHelper = new NotificationHelper(KhamBenh.this);
                        notificationHelper.createNotification("BỆNH VIỆN THÔNG BÁO", "Còn + " + stt + " người nữa là tới lượt bạn");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private void getListBenh() {
        {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpPost = new HttpGet("http://203.162.13.40/get_sick");
                        HttpResponse response = httpClient.execute(httpPost);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                        String response_body = reader.readLine();
                        JSONArray jsonArray = new JSONArray(response_body);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            name = jsonObject.getString("name");
                            id = jsonObject.getString("id");
                            listKhoa.add(name);
                            Benh benh = new Benh(id, name);
                            listBenh.add(benh);
                        }
                    } catch (IOException e) {
                        System.out.println(e);
                    } catch (NetworkOnMainThreadException e) {
                        System.out.println(e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void Viberation() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }
}
