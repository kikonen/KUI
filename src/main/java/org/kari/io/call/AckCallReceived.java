package org.kari.io.call;

/**
 * Server received call and will attempt to execute it
 *
 * @author kari
 */
public final class AckCallReceived extends AckResult {
    public static final AckCallReceived INSTANCE = new AckCallReceived();

    public AckCallReceived() {
        super(CallType.ACK_CALL_RECEIVED);
    }

}
