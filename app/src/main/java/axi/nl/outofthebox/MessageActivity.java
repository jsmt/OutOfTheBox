package axi.nl.outofthebox;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class MessageActivity extends AppCompatActivity {

    public enum MessageState {
        NEW, PENDING, CLOSED
    }

    private RecyclerView recyclerView;
    public SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent service = new Intent(this, LocationService.class);
        startService(service);

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
    }


    public void fillView() {
        recyclerView.removeAllViews();
        RecyclerAdapter adapter = new RecyclerAdapter(this, LocationService.getMessages(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swipeRefreshLayout.setRefreshing(false);
    }
}
