package fi.utu.tech.distributed.mesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import fi.utu.tech.distributed.gorilla.logic.ChatMessage;

/**
 * TODO: comment the class
 */
public class Mesh extends Thread {
	
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectOutputStream objOut;
	
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
    		if(port != 1234) {
    			System.out.println("[INFO] Finding an available port...");
    			try {
					serverSocket = new ServerSocket(0);
				} catch (IOException e2) {
					System.exit(-1);
				}
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
    			new Handler(s, this).start();
	            checkSocket(s);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
	}
    
    private void checkSocket(Socket s) {
    	if(socket == null) {
    		System.out.println("Sokettia ei installoitu");
    		connect(s.getInetAddress().getHostAddress(), s.getPort()-1);
    	}
    	
    }
    
	/**
	 * Yhdistä tämä vertainen olemassaolevaan Mesh-verkkoon
	 * @param addr Solmun ip-osoite, johon yhdistetään
	 * @param port Portti, jota vastapuolinen solmu kuuntelee
	 */
	public void connect(String addr, int port) {
		try {
			
			System.out.println(addr);
			System.out.println(port);
			
			socket = new Socket(addr, port);
			
			// Datan lähetys testi
			InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            objOut = new ObjectOutputStream(out);
            ObjectInputStream objIn = new ObjectInputStream(in);
			
			
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
    	
    	try {
    		objOut.writeObject(o);
    	} catch (IOException io) {
    		io.printStackTrace();
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
		private Mesh mesh;
		
		public Handler(Socket socket, Mesh mesh) {
			System.out.println("New connection from "+socket.getRemoteSocketAddress());
			this.socket = socket;
			this.mesh = mesh;
		}
		
		@Override
		public void run() {
            try {
				InputStream iS = socket.getInputStream();
	            OutputStream oS = socket.getOutputStream();
	            ObjectOutputStream oOut = new ObjectOutputStream(oS);
	            ObjectInputStream oIn = new ObjectInputStream(iS);
	            
	            while(true) {
	            	try {
	            		ChatMessage msg = (ChatMessage) oIn.readObject();
	                    System.out.printf("Joku sanoo: %s%n", msg.contents);
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
		}
		
		/**
		 * Tarkista, onko viestitunniste jo olemassa
		 * Määreenä private, koska tätä käyttävä luokka on sisäluokka (inner class)
		 * Jos et käytä sisäluokkaa, pitää olla public
		 * @param token Viestitunniste 
		 */
		private boolean tokenExists(long token) {
			return false;
		}
		
    }

}