package org.kari.call.event;

import org.kari.call.CallType;

/**
 * Server received call and will attempt to execute it
 *
 * @author kari
 */
public final class AckCallReceived extends AckResult {
    public static final AckCallReceived INSTANCE = new AckCallReceived();

    public AckCallReceived() {
        super();
    }

    @Override
    public CallType getType() {
        return CallType.ACK_CALL_RECEIVED;
    }

}
