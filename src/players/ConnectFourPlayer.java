package players;

/**
 * A tiny interface that all AI players must implement
 */
public interface ConnectFourPlayer {
	int getNextPlay(byte[][] rack);
}
