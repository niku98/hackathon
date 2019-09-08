package com.example.vinid;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class SocketIO extends AppCompatActivity{
    private final String URL_SERVER = "http://203.162.13.40:80";
    private Socket mSocket;

    public SocketIO(Emitter.Listener onNewMessage, Emitter.Listener onNewConnect, Emitter.Listener onNewNotify) {
        try {
            mSocket = IO.socket(URL_SERVER);
            mSocket.on("created_queue", onNewMessage);
            mSocket.on("connect", onNewConnect);
            mSocket.on("notifying", onNewNotify);
            mSocket.connect();
        } catch (URISyntaxException e) {}
    }

    public void sendMessage(String message){
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mSocket.emit("send_message", message);
    }

    public void detectUser(String message){
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mSocket.emit("detect_user", message);
    }

    public void getNotify(String message){
        if (TextUtils.isEmpty(message)) {
            return;
        }

        mSocket.emit("getNotify", message);
    }
}
