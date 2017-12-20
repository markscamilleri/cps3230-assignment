package util;

import java.time.Instant;
import java.util.Collection;

/**
 * @param <C> The Collection (with T)
 * @param <T> The Timeoutable type that is being held by C
 */
public abstract class TimeoutContainer<C extends Collection<T>, T extends Timeoutable> {
    
    protected C container;
    
    protected TimeoutContainer(C container) {
        this.container = container;
    }
    
    /**
     * Checks if there are any objects that timed out.
     * This must be called before any operation on the data structure
     * storing timeoutable objects
     *
     * @return the number of objects that timed out.
     */
    protected int checkAndDelete() {
        int count = 0;
        for (T t : container) {
            if (Instant.now().isAfter(t.getTimeout())){
                container.remove(t);
                count++;
            }
        }
        
        return count;
        
    }
}
