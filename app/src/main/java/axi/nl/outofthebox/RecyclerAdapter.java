package axi.nl.outofthebox;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by rdkl on 2-6-2016.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {

    Context context;
    LayoutInflater inflater;
    List<Message> messages;
    MessageActivity messageActivity;

    public RecyclerAdapter(Context context, List<Message> messages, MessageActivity messageActivity) {
        this.context = context;
        this.messages = messages;
        this.messageActivity = messageActivity;
        inflater = LayoutInflater.from(context);
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.activity_message_item, parent, false);
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(v, this.context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder h = (RecyclerViewHolder) holder;

        Message msg = messages.get(position);
        h.tv2.setText(msg.getMessage());

        if(msg.getState().equals(MessageActivity.MessageState.NEW)){
            h.btnNeg.setText("Negeren");
            h.btnPos.setText("Accepteren");
        } else if(msg.getState().equals(MessageActivity.MessageState.PENDING)){
            h.btnNeg.setVisibility(View.INVISIBLE);
            h.btnPos.setText("Behandeld");

            messages.remove(position);
            messageActivity.fillView();

        } else if(msg.getState().equals(MessageActivity.MessageState.CLOSED)){
            h.tv1.setText("AFGEHANDELD");
            h.background.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCardBackground));
            h.btnNeg.setVisibility(View.INVISIBLE);
            h.btnPos.setVisibility(View.INVISIBLE);

            messages.remove(position);
            messageActivity.fillView();
        }

        h.btnPos.setOnClickListener(new PosOnClickListener(msg));
        h.btnNeg.setOnClickListener(new NegOnClickListener(msg));
    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    private class PosOnClickListener implements View.OnClickListener {

        private Message message;

        public PosOnClickListener(Message message) {
            this.message = message;
        }

        @Override
        public void onClick(View v) {
            if(message.getState().equals(MessageActivity.MessageState.NEW)){
                message.setState(MessageActivity.MessageState.PENDING);

                WebSocketService.acceptRequest(message.getId());

            } else if(message.getState().equals(MessageActivity.MessageState.PENDING)){
                message.setState(MessageActivity.MessageState.CLOSED);

                WebSocketService.closeRequest(message.getId());
            }
            RecyclerAdapter.this.messageActivity.fillView();
        }
    }

    private class NegOnClickListener implements View.OnClickListener {

        private Message message;

        public NegOnClickListener(Message message) {
            this.message = message;
        }

        @Override
        public void onClick(View v) {
            if(message.getState().equals(MessageActivity.MessageState.NEW)){
                message.setState(MessageActivity.MessageState.CLOSED);

                WebSocketService.denyRequest(message.getId());
            }
            RecyclerAdapter.this.messageActivity.fillView();
        }
    }
}