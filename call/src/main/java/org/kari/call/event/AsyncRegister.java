package org.kari.call.event;

import org.kari.call.CallType;
import org.kari.call.ClientKey;

/**
 * Register asynchronous result received to server
 *
 * @author kari
 */
public final class AsyncRegister extends BufferResult {
    public AsyncRegister() {
        super();
    }

    public AsyncRegister(ClientKey pClientKey) {
        super(pClientKey);
    }

    @Override
    public ClientKey getResult() {
        ClientKey result = null;
        try {
            result = (ClientKey)super.getResult();
        } catch (Throwable e) {
            // NOTE KI impossible
        }
        return result;
    }

    @Override
    public CallType getType() {
        return CallType.ASYNC_REGISTER;
    }

}
