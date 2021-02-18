import java.util.*;

/**
 * Messages class
 */
public class Messages {
    private ArrayList < String > messages;

    /**
     * Messages class constructor 
     */
    public Messages() {
        messages = new ArrayList < String > ();
    }

    /**
     * Adds a message to the list
     */
    public void add(String message) {
        messages.add(message);
    }

    /**
     * All messages in one String
     * 
     * @return all  messages
     */
    public String getMessages() {
        StringBuffer b = new StringBuffer();
        for (String message: messages)
            b.append(message);
        return b.toString();
    }
}
