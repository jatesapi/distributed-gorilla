package fi.utu.tech.distributed.mesh;

import java.io.Serializable;
import java.util.List;

import fi.utu.tech.distributed.gorilla.logic.GameConfiguration;
import fi.utu.tech.distributed.gorilla.logic.GameState;
import fi.utu.tech.distributed.gorilla.logic.Player;

public class MeshMessage implements Serializable {
	
	private GameConfiguration configuration;
	private List<Player> players;
	private Player me;
	
	public MeshMessage(GameConfiguration configuration, List<Player> players, Player me) {
		this.configuration = configuration;
		this.players = players;
		this.me = me;
	}
	
	public static GameState getGameState(MeshMessage msg) {
		return new GameState(msg.configuration, msg.players, msg.me);
	}
	
	public static MeshMessage buildMessage(GameState state) {
		return new MeshMessage(state.getConfiguration(), state.getPlayers(), state.getLocalPlayer());
	}
	
}
