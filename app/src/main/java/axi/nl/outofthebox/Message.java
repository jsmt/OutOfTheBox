package axi.nl.outofthebox;

/**
 * Created by rdkl on 16-6-2016.
 */
public class Message {

    private String message;

    private int id;
    private MessageActivity.MessageState state = MessageActivity.MessageState.NEW;

    public Message(String message, MessageActivity.MessageState state, int id) {
        this.message = message;
        this.state = state;
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public MessageActivity.MessageState getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public void setState(MessageActivity.MessageState state) {
        this.state = state;
    }

}
