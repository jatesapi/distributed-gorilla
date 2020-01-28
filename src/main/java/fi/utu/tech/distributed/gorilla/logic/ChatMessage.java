package fi.utu.tech.distributed.gorilla.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * TODO: make compatible with network play
 */
public final class ChatMessage implements Serializable {
    public final String sender;
    public final String recipient;
    public final String contents;
    public final long token;
    
    private static ArrayList<Long> tokens = new ArrayList<>();

    public ChatMessage(String sender, String recipient, String contents) {
        this.sender = sender;
        this.recipient = recipient;
        this.contents = contents;
        token = generateToken();
    }
    
    /**
     * Generates random token to identify unique messages.
     * @return
     */
    private static long generateToken() {
    	Random generator = new Random();
    	long value = generator.nextLong();
    	while(tokens.contains(value)) {
    		value = generator.nextLong();
    	}
		tokens.add(value);
  		return value;
    }
}
