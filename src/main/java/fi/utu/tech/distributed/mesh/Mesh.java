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

/**
 * TODO: comment the class
 */
public class Mesh extends Thread {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private Set<ObjectOutputStream> nodes = new HashSet<>();
	
    /**
     * Mesh-server instance that creates a server socket bound to the given port.
     * If the port is already in use, the socket is bound to any available port.
     * @param port - the port number where other instances can connect
     */
    public Mesh(int port) {
    	try {
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
	public void connect(String addr, int port) {
		try {	
			socket = new Socket(addr, port);
			new Handler(socket).start();
		} catch (UnknownHostException e) {
			System.out.println("[ERROR] Unknown server IP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Lähetä hyötykuorma kaikille vastaanottajille
     * @param o Lähetettävä hyötykuorma
     */
    public void broadcast(Serializable o) {  	
    	int laskuri = 1;
    	for(ObjectOutputStream node : nodes) {
    		try {
    			node.writeObject(o);
    			node.flush();
    			laskuri++;
    		} catch (IOException io) {
    			io.printStackTrace();
    		}
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
	
    
    
    
    /**
     * Sisäluokka, joka huolehtii solmujen vertaiskommunikaatiosta
     */
	class Handler extends Thread {
		
		private Socket socket;
		private ArrayList<Long> tokens;
		
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
	            		ChatMessage msg = (ChatMessage) oIn.readObject();
	            		
	            		long token = msg.token;
	            		if(tokenExists(token)) {
	            			;
	            		} else {
	            			addToken(token);
	            			broadcast(msg);
		                    System.out.printf("Joku sanoo: %s%n", msg.contents);
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