package axi.nl.outofthebox;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WebSocketService extends IntentService {

    private static WebSocketClient mWebSocketClient;

    public WebSocketService() {
        super("WebSocketService");
    }

    @Override
    public void onCreate() {
        connectWebSocket();

        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }



    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://172.24.1.1:3000/rick");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;

                try {

                    Log.d("MainActivity", "Message received: " + message);

                    JSONObject json = new JSONObject(message);

                    if (json.has("req")) {
                        String req = json.getString("req");
                        int pos = json.getInt("pos");
                        String label = json.getString("label");

                        LocationService.addMessage(label, pos);
                    }
                    else if (json.has("cmd")) {
                        String cmd = json.getString("cmd");

                        if (cmd.equals("drop-assist")) {
                            int pos = json.getInt("pos");
                            String label = json.getString("label");

                            LocationService.removeMessage(pos);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public static void sendBeaconDistance(Double b1, Double b2, Double b3) {
        try {
            JSONObject message = new JSONObject();
            message.put("cmd", "pos");

            JSONObject beacons = new JSONObject();
            beacons.put("b1", b1);
            beacons.put("b2", b2);
            beacons.put("b3", b3);

            message.put("value", beacons);


            if (mWebSocketClient != null) {
                Log.d("MainActivity", "Sending message: " + message.toString());
                mWebSocketClient.send(message.toString());
            }
            else
            {
                Log.d("MainActivity", "Server down");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void acceptRequest(int pos) {
        try {
            JSONObject message = new JSONObject();
            message.put("cmd", "assist");
            message.put("pos", pos);
            message.put("value", true);

            if (mWebSocketClient != null) {
                Log.d("MainActivity", "acceptRequest - Sending message: " + message.toString());
                mWebSocketClient.send(message.toString());
            }
            else
            {
                Log.d("MainActivity", "Server down");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void denyRequest(int pos) {
        try {
            JSONObject message = new JSONObject();
            message.put("cmd", "assist");
            message.put("pos", pos);
            message.put("value", false);

            if (mWebSocketClient != null) {
                Log.d("MainActivity", "denyRequest - Sending message: " + message.toString());
                mWebSocketClient.send(message.toString());
            }
            else
            {
                Log.d("MainActivity", "Server down");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void closeRequest(int pos) {
        try {
            JSONObject message = new JSONObject();
            message.put("cmd", "arrived");
            message.put("pos", pos);

            if (mWebSocketClient != null) {
                Log.d("MainActivity", "closeRequest - Sending message: " + message.toString());
                mWebSocketClient.send(message.toString());
            }
            else
            {
                Log.d("MainActivity", "Server down");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}