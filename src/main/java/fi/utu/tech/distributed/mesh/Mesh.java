package fi.utu.tech.distributed.mesh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * TODO: comment the class
 */
public class Mesh extends Thread {
	
	private ServerSocket serverSocket;
	
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
	            new Handler(serverSocket.accept(), this).start();
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
			Socket socket = new Socket(addr, port);
			
			// Datan lähetys testi
			InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(out);
            DataInputStream dataIn = new DataInputStream(in);
            
            dataOut.writeInt(10);
            dataOut.flush();
            dataOut.writeInt(5);
			
			
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
			System.out.println("New connection from "+socket.getInetAddress()+"/"+socket.getPort()+"!");
			this.socket = socket;
			this.mesh = mesh;
		}
		
		@Override
		public void run() {
            try {
				InputStream iS = socket.getInputStream();
	            OutputStream oS = socket.getOutputStream();
	            DataOutputStream oOut = new DataOutputStream(oS);
	            DataInputStream oIn = new DataInputStream(iS);
	            
	            while(true) {
	            	// täällä luetaan tietoa
	            	int a = oIn.readInt();
	            	System.out.println(a);
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