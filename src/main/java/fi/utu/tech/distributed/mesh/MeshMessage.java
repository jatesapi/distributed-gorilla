package fi.utu.tech.distributed.mesh;

import java.io.Serializable;
import java.util.List;

import fi.utu.tech.distributed.gorilla.logic.GameConfiguration;
import fi.utu.tech.distributed.gorilla.logic.GameState;
import fi.utu.tech.distributed.gorilla.logic.Player;

/**
 * Serializes parts of the GameState object.
 */
public class MeshMessage implements Serializable {
	
	private GameConfiguration configuration;
	private List<Player> players;
	
	public MeshMessage(GameConfiguration configuration, List<Player> players) {
		this.configuration = configuration;
		this.players = players;
	}
	
	public static GameState getGameState(MeshMessage msg, Player me) {
		return new GameState(msg.configuration, msg.players, me);
	}
	
	public static MeshMessage buildMessage(GameState state) {
		return new MeshMessage(state.getConfiguration(), state.getPlayers());
	}
	
}
