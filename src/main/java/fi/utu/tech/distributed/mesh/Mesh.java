package fi.utu.tech.distributed.mesh;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fi.utu.tech.distributed.gorilla.logic.ChatMessage;
import fi.utu.tech.distributed.gorilla.logic.GameConfiguration;
import fi.utu.tech.distributed.gorilla.logic.GameMode;
import fi.utu.tech.distributed.gorilla.logic.GorillaLogic;
import javafx.application.Platform;

/**
 * Mesh network of equal nodes that act as both client and server,
 * connecting to and listening connections from other nodes.
 */
public class Mesh extends Thread {
	
	// Current game instance
	private GorillaLogic gameInstance;
	
	// Server and client sockets of the node
	private ServerSocket serverSocket;
	private Socket socket;
	
	// List of the nodes that this instance is connected to
	// "Upstream" (parent) nodes: 0-1
	// "Downstream" (child) nodes: 0-n
	private Set<ObjectOutputStream> nodes = new HashSet<>();
	
	// List of tokens that identify all the messages received by this node
	private ArrayList<Long> tokens;
	
    /**
     * Mesh-server instance that creates a server socket bound to the given port.
     * If the port is already in use, the socket is bound to any available port.
     * @param gameInstance - instance of the current game
     * @param port - the port number where other instances can connect
     * @param player 
     */
    public Mesh(GorillaLogic gameInstance, int port) {
    	try {
    		this.gameInstance = gameInstance;
    		
    		serverSocket = new ServerSocket(port);
    		System.out.println("Starting the server...");
    		
    	} catch (IOException e1) {
    		// If the port is already in use, find any available port to start listening for connections
    		System.out.println("[ERROR] Port " +port+ " already has something running!");
			System.out.println("[INFO] Finding an available port...");
			
			try {
				serverSocket = new ServerSocket(0);
			} catch (IOException e2) {
				// Shutdown if finding an available port fails
				System.exit(-1);
			}
    	}
    	System.out.println("Server started.");
	}
    
    
    /**
     *  Listens to upcoming connections from other game instances.
     */
    @Override
    public void run() {
    	while(true) {
    		try {
    			System.out.println("Waiting for connections...");
	            Socket s = serverSocket.accept();
    			System.out.println("New node connected!");
    			new Handler(s).start();
    			
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
	}
    
    
	/**
	 * Connects this node to an existing mesh network.
	 * @param addr - IP of the server
	 * @param port - port of the server
	 */
	public void connect(String addr, int port) throws IOException, UnknownHostException {	
		try {
			socket = new Socket(addr, port);
			System.out.println("Succesfully connected to existing mesh.");
			new Handler(socket).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Broadcasts a chat message to all child and parent nodes.
     */
    public void broadcast(ChatMessage msg) {  	
    	for(ObjectOutputStream node : nodes) {
    		try {
    			// Save the token so we don't receive the message again from child nodes
    			tokens.add(msg.token);
    			
    			// Send the message
    			node.writeObject(msg);
    			node.flush();
    			
    		} catch (IOException io) {
    			io.printStackTrace();
    		}
    	}
	}
    
    /**
     * Send online player info to other nodes.
     * @param player - online player instance
     */
    public void sendPlayerInfo(OnlinePlayer player) {
    	try {
    		// Wait for streams to be up...
    		sleep(1000); 
    		
	    	for(ObjectOutputStream node : nodes) {
    			node.writeObject(player);
    			node.flush();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
   		}
    }

    
    /**
     * Closes this mesh node and all the connections.
     * TODO: Implement closing procedures
     */
    public void close() {
	}
    
    /**
     * Sends a game update to other players.
     * @param conf
     */
	public void sendGameUpdate(GameConfiguration conf) {	
    	try {
	    	for(ObjectOutputStream node : nodes) {
	    		node.writeObject(conf);
	    		node.flush();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
   		}
	}
	
	/**
	 * Sends game mode change to nodes.
	 * (Someone has started the multiplayer).
	 * TODO: Implement more modes (quit etc.)
	 * @param mode - 1=multiplayer,
	 */
	public void sendGameMode(int mode) {
    	try {
	    	for(ObjectOutputStream node : nodes) {
	    		node.writeObject(mode);
	    		node.flush();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
   		}
	}
    
    
    /**
     * Handler class that listens to other nodes in the mesh network.
     * TODO: Handle chat message printing in the JavaFX thread
     * TODO: Currently a lot of duplicate code, could be optimized
     */
	class Handler extends Thread {
		
		private Socket socket;
		
		public Handler(Socket socket) {
			this.socket = socket;
			tokens = new ArrayList<>();
		}
		
		@Override
		public void run() {
            try {
				InputStream iS = socket.getInputStream();
	            OutputStream oS = socket.getOutputStream();
	            ObjectOutputStream oOut = new ObjectOutputStream(oS);
	            ObjectInputStream oIn = new ObjectInputStream(iS);
	            
	            // All the childs get added to the same broadcast list
	            nodes.add(oOut);
	            
	            // Listening the socket...
	            while(true) {
	            	try {
	            		
	            		// Read the data
	            		Object data = oIn.readObject();
	            		
	            		// Check the object type of the received data
	            		// Integer input = game mode changes
	            		if(data instanceof Integer) {
	            			int action = (Integer) data;
	            			handleMode(action);
	            		}
	            		
	            		// OnlinePlayer = new player joining
	            		else if(data instanceof OnlinePlayer) {
	            			OnlinePlayer player = (OnlinePlayer) data;
	            			if(!gameInstance.playerAlreadyJoined(player)) {
		            			gameInstance.joinGame(player);
		            			// If the connected node was new, send current node forward
		            			sendPlayerInfo(gameInstance.getOnlinePlayer());
	            			}
	            			
	            		// ChatMessage = chat message to be broadcasted	
	            		} else if (data instanceof ChatMessage) {
	            			ChatMessage msg = (ChatMessage) data;
		            		handleChatMessage(msg);
	            		
		            	// GameConfiguration = game state update
	            		} else if (data instanceof GameConfiguration) {
	            			GameConfiguration conf = (GameConfiguration) data;
	            			handleGameStateUpdate(conf);
	            		}
	            		
	            	} catch (ClassNotFoundException e) {
	            		e.printStackTrace();
	            	}
	            }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * Handles receiving updates to the game state.
		 * @param conf - new configuration used in building new game state
		 */
		private void handleGameStateUpdate(GameConfiguration conf) {
			// Prevents interrupting the JavaFX thread
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					gameInstance.setGameState(conf);
				}
			});
			
		}

		/** 
		 * Handles integer input (game mode changes).
		 * @param action - game action (1=multiplayer, ...)
		 */
		private void handleMode(int action) {
			switch(action) {
			
			// The sender asks to start a multiplayer
			case 1:
				// Prevents interrupting the JavaFX thread
				Platform.runLater(new Runnable() {
    				@Override
    				public void run() {
        				gameInstance.joinMultiplayer();
    				}
    			});
				break;
			
			// quit
			case 0:
				break;
    		}
		}

		/**
		 * Handles receiving a chat message object.
		 * @param msg - received chat message
		 */
		private void handleChatMessage(ChatMessage msg) {
			long token = msg.token;
    		if(tokenExists(token)) {
    			// Old message received, don't do anything
    			;
    		} else {
    			// New message received, add the token to the list and broadcast if further
    			addToken(token);
    			broadcast(msg);
                System.out.printf("%s sanoo: %s%n", msg.sender, msg.contents);
    		}
		}

		/**
		 * Adds a new token to the list.
		 * Identifies which messages has already been received.
		 * @param token - unique message identifier 
		 */
		private void addToken(long token) {
			tokens.add(token);
		}
		
		/**
		 * Checks if the token already exists in the list.
		 * @param token - unique message identifier
		 */
		private boolean tokenExists(long token) {
			if(tokens.contains(token)) {
				return true;
			}
			return false;
		}
    }

}