package fi.utu.tech.distributed.mesh;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fi.utu.tech.distributed.gorilla.logic.ChatMessage;
import fi.utu.tech.distributed.gorilla.logic.GameMode;
import fi.utu.tech.distributed.gorilla.logic.GameState;
import fi.utu.tech.distributed.gorilla.logic.GorillaLogic;
import fi.utu.tech.distributed.gorilla.logic.Player;
import javafx.application.Platform;

/**
 * TODO: comment the class
 */
public class Mesh extends Thread {
	
	// Current game instance
	private GorillaLogic gameInstance;
	private ServerSocket serverSocket;
	private Socket socket;
	private Set<ObjectOutputStream> nodes = new HashSet<>();
	private ArrayList<Long> tokens;
	
    /**
     * Mesh-server instance that creates a server socket bound to the given port.
     * If the port is already in use, the socket is bound to any available port.
     * @param gameInstance 
     * @param port - the port number where other instances can connect
     */
    public Mesh(GorillaLogic gameInstance, int port) {
    	try {
    		this.gameInstance = gameInstance;
    		//gameInstance.
    		serverSocket = new ServerSocket(port);
    		System.out.println("Starting the server...");
    	} catch (IOException e1) {
    		System.out.println("[ERROR] Port " +port+ " already has something running!");
			System.out.println("[INFO] Finding an available port...");
			try {
				serverSocket = new ServerSocket(0);
			} catch (IOException e2) {
				System.exit(-1);
			}
    	}
    	System.out.println("Server started.");
	}
    
    /**
     *  Listens to upcoming connections from other game instances.
     *  TODO: Improve commenting
     */
    @Override
    public void run() {
    	while(true) {
    		try {
    			System.out.println("Listening to port " + serverSocket.getLocalPort() + " at " + serverSocket.getInetAddress() + "...");
	            Socket s = serverSocket.accept();
    			new Handler(s).start();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
	}
    
	/**
	 * Yhdistä tämä vertainen olemassaolevaan Mesh-verkkoon
	 * @param addr Solmun ip-osoite, johon yhdistetään
	 * @param port Portti, jota vastapuolinen solmu kuuntelee
	 */
	public void connect(String addr, int port) throws IOException, UnknownHostException {	
		try {
			socket = new Socket(addr, port);
			new Handler(socket).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Lähetä hyötykuorma kaikille vastaanottajille
     * @param o Lähetettävä hyötykuorma
     */
    public void broadcast(Serializable o) {  	
    	for(ObjectOutputStream node : nodes) {
    		try {
    			// Tallennetaan solmun itsensä lähettämä viestin token
    			ChatMessage localMsg = (ChatMessage) o;
    			tokens.add(localMsg.token);
    			
    			// Lähetetään viesti tähän solmuun yhdistäneelle solmulle
    			node.writeObject(o);
    			node.flush();
    		} catch (IOException io) {
    			io.printStackTrace();
    		}
    	}
	}
    
    /**
     * Send player info for other nodes
     * @param player - Player name
     */
    public void sendPlayerInfo(Player player) {
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
     * Lähetä hyötykuorma valitulle vertaiselle
     * @param o Lähetettävä hyötykuorma
     * @param recipient Vastaanottavan vertaisen tunnus
     */
    public void send(Serializable o, long recipient) {
	}

    
    /**
     * Sulje mesh-palvelin ja kaikki sen yhteydet 
     */
    public void close() {
	}
    
	public void sendGameChange(GameState state) {
		
		MeshMessage msg = MeshMessage.buildMessage(state);
		
    	try {
	    	for(ObjectOutputStream node : nodes) {
	    		node.writeObject(msg);
	    		node.flush();
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
   		}
	}
	
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
     * Sisäluokka, joka huolehtii solmujen vertaiskommunikaatiosta
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
	            nodes.add(oOut);
	            
	            while(true) {
	            	try {
	            		
	            		Object data = oIn.readObject();
	            		
	            		if(data instanceof Integer) {
	            			int action = (Integer) data;
	            			switch(action) {
	            			// start multiplayer
	            			case 1:
	            				Platform.runLater(new Runnable() {
		            				@Override
		            				public void run() {
			            				gameInstance.setMultiplayerMode(GameMode.Game);
		            				}
		            			});
	            				break;
	            			// quit
	            			case 0:
	            				break;
	            			}
	            		}
	            		
	            		else if(data instanceof Player) {
	            			Player player = (Player) data;
	            			if(!gameInstance.playerExists(player)) {
		            			gameInstance.joinGame(player);
		            			sendPlayerInfo(gameInstance.getLocalPlayer());
	            			}
	            		} else if (data instanceof ChatMessage) {
	            			ChatMessage msg = (ChatMessage) data;
		            		handleChatMessage(msg);
	            		
	            		} else if (data instanceof MeshMessage) {
	            			MeshMessage msg = (MeshMessage) data;
	            			Platform.runLater(new Runnable() {
	            				@Override
	            				public void run() {
	            					gameInstance.setGameState(MeshMessage.getGameState(msg));
	            				}
	            			});
	            		}
	            		
	            	
	            		
	            	} catch (ClassNotFoundException e) {
	            		e.printStackTrace();
	            	}
	            }
	            
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		private void handleChatMessage(ChatMessage msg) {
			long token = msg.token;
    		if(tokenExists(token)) {
    			;
    		} else {
    			addToken(token);
    			broadcast(msg);
                System.out.printf("%s sanoo: %s%n", msg.sender, msg.contents);
    		}
		}

		/**
		 * Lisää token, eli "viestitunniste"
		 * Käytännössä merkkaa viestin tällä tunnisteella luetuksi
		 * Määreenä private, koska tätä käyttävä luokka on sisäluokka (inner class)
		 * Jos et käytä sisäluokkaa, pitää olla public
		 * @param token Viestitunniste 
		 */
		private void addToken(long token) {
			tokens.add(token);
		}
		
		/**
		 * Tarkista, onko viestitunniste jo olemassa
		 * Määreenä private, koska tätä käyttävä luokka on sisäluokka (inner class)
		 * Jos et käytä sisäluokkaa, pitää olla public
		 * @param token Viestitunniste 
		 */
		private boolean tokenExists(long token) {
			if(tokens.contains(token)) {
				return true;
			}
			return false;
		}
		
    }

}