package util;

/**
 * @author Mark Said Camilleri
 * @version 16/12/17.
 */
public abstract class Deletable {
    private boolean deleted = false;
    
    
    /**
     * Hiding the constructor
     */
    protected Deletable(){}
    
    /**
     * Flags this object as deleted
     * @return true if successful, false otherwise.
     */
    public boolean delete() {
        this.deleted = true;
        return this.deleted;
    }
    
    /**
     * Returns whether this object is deleted or not
     * @return true if it is flagged as deleted, false otherwise
     */
    public boolean isDeleted() {
        return this.deleted;
    }
}
