package cs576.sound.playWave;

/**
 * @author Giulio
 */
public class PlayWaveException extends Exception {
    private static final long serialVersionUID = -4568593581916175189L;
    public PlayWaveException(String message) {
	super(message);
    }

    public PlayWaveException(Throwable cause) {
	super(cause);
    }

    public PlayWaveException(String message, Throwable cause) {
	super(message, cause);
    }

}
