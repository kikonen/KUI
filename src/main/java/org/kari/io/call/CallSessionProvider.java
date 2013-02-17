package org.kari.io.call;


/**
 * Provides session info for calls from current thread context
 *
 * @author kari
 */
public interface CallSessionProvider {
    Object getSessionId();
}
