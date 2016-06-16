package axi.nl.outofthebox;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageActivity extends AppCompatActivity {

    private static List<Message> messages = new ArrayList<Message>();

    public enum MessageState {
        NEW, PENDING, CLOSED
    }

    private RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;
    private static NotificationManager mNotifyMgr;
    private final static AtomicInteger c = new AtomicInteger(0);

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getStringExtra("command");
            if (cmd.equals("ADD")) {
                addMessage(intent.getStringExtra("label"), intent.getIntExtra("id", 0));
            }
            else if (cmd.equals("REMOVE")) {
                removeMessage(intent.getIntExtra("id", 0));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent locationService = new Intent(this, LocationService.class);
        ResultReceiver receiver = new ResultReceiver(new Handler()) {
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                fillView();
            }
        };
        locationService.putExtra("receiver", receiver);
        startService(locationService);

        Intent socketService = new Intent(this, WebSocketService.class);
        startService(socketService);

        setContentView(R.layout.activity_message);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fillView();
            }
        });
    }

    @Override
    protected void onResume() {
        fillView();
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(WebSocketService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    public void fillView() {
        recyclerView.removeAllViews();
        RecyclerAdapter adapter = new RecyclerAdapter(this, messages, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setRefreshing(false);
    }

    private void showNotification(String message) {
        Context context = getApplicationContext();
        Intent myIntent = new Intent(context, MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification not = new Notification.Builder(context)
                .setContentTitle("HULP GEVRAAGD")
                .setContentText(message)
                .setSmallIcon(R.drawable.question_16_white)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        // Builds the notification and issues it.
        mNotifyMgr.notify(c.incrementAndGet(), not);
    }

    public void addMessage (String message, int id) {
        Message msg = new Message(message, MessageActivity.MessageState.NEW, id);

        if (!messages.contains(msg)) {
            messages.add(msg);
            showNotification(message);
        }
    }

    public void removeMessage (int id) {
        for (int i = 0; i < messages.size(); i++) {
            Message msg = (Message)messages.get(i);
            if (msg.getId() == id) {
                messages.remove(i);
                fillView();
                break;
            }
        }
    }
}
