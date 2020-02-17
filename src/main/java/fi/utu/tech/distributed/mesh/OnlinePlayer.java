package fi.utu.tech.distributed.mesh;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class that represents online player that has a name and unique id.
 * Basically represents one node in the mesh network.
 */
public class OnlinePlayer implements Serializable {
	
	public String name;	// Online name of the player
	private String id;		// Online id of the player
	
	public OnlinePlayer(String name) {
		this.name = name;
		id = UUID.randomUUID().toString();
	}

}
