package util;

import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Mark Said Camilleri
 * @version 16/12/17.
 */
public class Timeout {
    /**
     * Keeps an instance of this object -> singleton
     */
    private static Timeout instance = new Timeout();
    /**
     * The queue of objects that are "waiting to be deleted"
     */
    private volatile Queue<Timeoutable> timeoutableObjects = new PriorityBlockingQueue<>();
    
    /**
     * Creates a new thread for this singleton object. The thread is run but is put to sleep
     */
    private Timeout() {
    }
    
    /**
     * Returns the singleton instance of this Timeout
     *
     * @return a singleton instance of Timeout
     */
    public static Timeout getInstance() {
        return instance;
    }
    
    /**
     * Registers an object to be timed out
     *
     * @param timeoutable the object to time out
     * @return true if successfully added, false othewise;
     */
    public synchronized boolean register(Timeoutable timeoutable) {
        return timeoutableObjects.add(timeoutable);
    }
    
    /**
     * Checks if there are any objects that timed out.
     * @return the number of objects that timed out.
     */
    public synchronized int checkAndDelete() {
        int count = 0;
        while (!isEmpty() && !(timeoutableObjects.peek().getTimeout().isAfter(Instant.now()))) {
            timeoutableObjects.poll().delete();
            count++;
        }
        
        return count;
    }
    
    /**
     * Checks whether there are any objects that will timeout
     * @return true if there are, false if there aren't
     */
    public synchronized boolean isEmpty() {
        return timeoutableObjects.isEmpty();
    }
}
