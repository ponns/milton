package com.ettrema.ldap;

import java.io.IOException;

/**
 * Allows applications to wrap LDAP operations in a transaction
 *
 * @author brad
 */
public interface LdapTransactionManager {
    /**
     * Execute the given action (syncronously) in a transaction
     * 
     * @param r 
     */
    void tx(Runnable r) throws IOException;
    
}
