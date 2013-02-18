package org.kari.call;


/**
 * Provides session info for calls from current thread context
 *
 * @author kari
 */
public interface CallSessionProvider {
    /**
     * <p>NOTE KI It's important to return *SAME* sessionId instance always
     * unless it changes. Based into this strict identity equality framework
     * is doing optimization communication to avoid passing sessionId redundantly
     * over network.
     * 
     * @return sessionId, can be null if no session used
     */
    Object getSessionId();
}
