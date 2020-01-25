package fi.utu.tech.distributed.mesh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Mesh extends Thread {
	
	private ServerSocket serverSocket;
	
    /**
     * Luo Mesh-palvelininstanssi
     * @param port Portti, jossa uusien vertaisten liittymispyyntöjä kuunnellaan
     */
    public Mesh(int port) {
    	try {
    		serverSocket = new ServerSocket(port);
    		System.out.println("Starting the server...");
    		System.out.println("Listening to port " + serverSocket.getLocalPort() + " at " + serverSocket.getInetAddress());
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
	}
  
    /**
     *  Käynnistä uusien vertaisten kuuntelusäie
     */
    public void run() {
    	
    	while(true) {
    		try {
	            new Handler(serverSocket.accept(), this).start();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
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
			this.socket = socket;
			this.mesh = mesh;
		}
		
		@Override
		public void run() {
            try {
				InputStream iS = socket.getInputStream();
	            OutputStream oS = socket.getOutputStream();
	            
	            
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
		
		/**
		 * Yhdistä tämä vertainen olemassaolevaan Mesh-verkkoon
		 * @param addr Solmun ip-osoite, johon yhdistetään
		 * @param port Portti, jota vastapuolinen solmu kuuntelee
		 */
		public void connect(InetAddress addr, int port) {
		}
    }

}