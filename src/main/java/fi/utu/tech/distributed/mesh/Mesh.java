package fi.utu.tech.distributed.mesh;

import java.io.Serializable;
import java.net.InetAddress;

public class Mesh {
    /**
     * Luo Mesh-palvelininstanssi
     * @param port Portti, jossa uusien vertaisten liittymispyyntöjä kuunnellaan
     */
    public Mesh(int port) {
	}
  
    /**
     *  Käynnistä uusien vertaisten kuuntelusäie
     */
    public void run() {
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
	class NodeConnector {
		
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