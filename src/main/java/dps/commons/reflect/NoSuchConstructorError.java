package dps.commons.reflect;

/**
 *
 * @author ferenci84
 */
public class NoSuchConstructorError extends RuntimeException {

    /**
     * Creates a new instance of <code>NoSuchConstcutorException</code> without
     * detail message.
     */
    public NoSuchConstructorError() {
    }

    /**
     * Constructs an instance of <code>NoSuchConstcutorException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NoSuchConstructorError(String msg) {
        super(msg);
    }

    public NoSuchConstructorError(String msg, Throwable cause) {
        super(msg,cause);
    }
}
