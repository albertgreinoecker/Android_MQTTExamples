package at.ac.htlinn.androidexamples.mqttexamples;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new MqttAndroidClient(getApplicationContext(), "tcp://10.0.2.2:1883", UUID.randomUUID().toString());

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("MY_MQTT", "Connection lost");
                Log.e("MY_MQTT", "MESSAGE:" + cause.getMessage());
                Log.e("MY_MQTT", cause.getStackTrace()[0].toString());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("MY_MQTT", "messageArrived" + message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i("MY_MQTT", "deliveryComplete");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(false);
        //Hier könnte man auch noch das Passwort setzen z.B.

        try {
            //addToHistory("Connecting to " + serverUri);
            client.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MY_MQTT", "Connection established");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MY_MQTT", "Connect failed");
                    Log.e("MY_MQTT", exception.getMessage());
                    Log.e("MY_MQTT", exception.getStackTrace().toString());
                }
            });


        } catch (MqttException ex) {
            ex.printStackTrace();
        }

        TextView tvIP = findViewById(R.id.ip);
        tvIP.setText("IP :" + getIPAddress());
    }

    public void publish(View v)
    {
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload("TEST TEXT SENTQ".getBytes());
            client.publish("any/topic", message);
            Log.i("MQTT", "Message Published");
            if(!client.isConnected()){
                Log.e("MQTT", "Connection lost");
            }
        } catch (MqttException e) {
            Log.e("MQTT", "Subscribed!");
            Log.e("MQTT", e.getMessage());
            Log.e("MQTT", e.getStackTrace().toString());
        }
    }

    public void subscribe(View v)
    {
        try {
            client.subscribe("any/topic", 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("MQTT", "Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "onFailure!");
                }
            });

        } catch (MqttException ex){
            System.err.println("Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        // Boolean to check if the address is an IPv4 address
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (isIPv4)
                            return sAddr;
                    }
                }
            }
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return "";
    }

}