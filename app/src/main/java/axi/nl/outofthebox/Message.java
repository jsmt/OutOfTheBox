package axi.nl.outofthebox;

/**
 * Created by rdkl on 16-6-2016.
 */
public class Message {

    private String message;
    private MessageActivity.MessageState state = MessageActivity.MessageState.NEW;

    public Message(String message, MessageActivity.MessageState state) {
        this.message = message;
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public MessageActivity.MessageState getState() {
        return state;
    }

    public void setState(MessageActivity.MessageState state) {
        this.state = state;
    }

}
