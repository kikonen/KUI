package org.kari.call.event;

import org.kari.call.CallType;

/**
 * First event from client, to differentiate between
 * async vs. normal call
 *
 * @author kari
 */
public final class Register extends AckResult {
    public static final Register INSTANCE = new Register();

    public Register() {
        super();
    }

    @Override
    public CallType getType() {
        return CallType.REGISTER;
    }

}
