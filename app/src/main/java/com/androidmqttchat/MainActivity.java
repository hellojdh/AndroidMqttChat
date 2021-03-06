package com.androidmqttchat;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import com.androidmqttchat.adapter.ChatAdapter;
import com.androidmqttchat.dto.ChatItem;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    static final String TOPIC = "topic";

    private ChatAdapter chatAdapter;
    private MqttClient mqttClient;

    @BindView(R.id.chatList)
    ListView chatListView;

    @BindView(R.id.chatEditText)
    EditText chatEditText;

    @OnClick(R.id.chatSendButton)
    public void sendChat(){
        String id = "ExId";
        String content = chatEditText.getText().toString();
        if(content.equals("")){ }
        else{
            JSONObject json = new JSONObject();
            try{
                json.put("id",id);
                json.put("content",content);
                mqttClient.publish(TOPIC,new MqttMessage(json.toString().getBytes()));
            }catch (Exception e){}
            chatEditText.setText("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        chatAdapter = new ChatAdapter();
        chatListView.setAdapter(chatAdapter);
        try{connectMqtt();}catch(Exception e){
            Log.d(TAG,"MqttConnect Error");
        }
    }

    private void connectMqtt() throws Exception{
        mqttClient = new MqttClient("tcp://MQTT Broker Address:1883", MqttClient.generateClientId(), null);
        mqttClient.connect();
        mqttClient.subscribe(TOPIC);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG,"Mqtt ReConnect");
                try{connectMqtt();}catch(Exception e){Log.d(TAG,"MqttReConnect Error");}
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                    JSONObject json = new JSONObject(new String(message.getPayload(), "UTF-8"));
                    chatAdapter.add(new ChatItem(json.getString("id"), json.getString("content")));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
