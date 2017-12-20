package util;

public abstract class Timeout {
    /**
     * Checks if there are any objects that timed out.
     * This must be called before any operation on the data structure
     * storing timeoutable objects
     * @return the number of objects that timed out.
     */
     protected abstract int checkAndDelete();
    
}
