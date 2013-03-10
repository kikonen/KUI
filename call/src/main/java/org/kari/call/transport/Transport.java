package org.kari.call.transport;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TShortObjectHashMap;

import java.rmi.RemoteException;
import java.util.Map;

import org.kari.call.CallBase;
import org.kari.call.event.Call;
import org.kari.call.event.Result;

/**
 * Abstract layer for sending calls and getting results back from them.
 *
 * @author kari
 */
public final class Transport {
    /**
     * Determines on which state call is
     *
     * @author kari
     */
    enum State {
        NONE,
        /**
         * Put into transmission queue
         */
        QUEUE,
        /**
         * Transmission is started
         */
        SENDING,
        /**
         * Sent, waiting for ACK for reception
         */
        WAIT_SEND_ACK,
        /**
         * Server ACKed reception of call
         */
        RECEIVED_SEND_ACK,
        /**
         * Sent ACK back to server to let server know
         * ack was received
         */
        ACKED_SEND_ACK,
        RECEIVED;
    }

    static final class CallState {
        private final int mCallId;
        private final TransportKey mKey;
        private volatile State mState = State.NONE;
        private volatile Result mResult;

        CallState(int pCallId, TransportKey pKey) {
            mCallId = pCallId;
            mKey = pKey;
        }

        int getCallId() {
            return mCallId;
        }

        void setState(State pState) {
            mState = pState;
        }
        void setResult(Result pResult) {
            mResult = pResult;
            mState = State.RECEIVED;
        }

        public TransportKey getKey() {
            return mKey;
        }

        /**
         * Poll current state of call
         */
        public State getState() {
            return mState;
        }

        /**
         * @return Result, available on if State.RECEIVED
         */
        public Result getResult() {
            return mResult;
        }
    }

    private final CallBase mBase;
    private final Map<TransportKey, SendConnection> mSenders = new THashMap<TransportKey, SendConnection>();
    private final Map<TransportKey, ReceiveConnection> mReceivers = new THashMap<TransportKey, ReceiveConnection>();

    /**
     * All currently active calls
     */
    private final TShortObjectMap<CallState> mPendingCalls = new TShortObjectHashMap<CallState>();

    private short mCallIdBase;


    public Transport(CallBase pBase) {
        mBase = pBase;
    }

    private short nextCallId() {
        if (mCallIdBase == Short.MAX_VALUE) {
            mCallIdBase = 0;
        }

        short callID = mCallIdBase++;
        while (!mPendingCalls.containsKey(callID)) {
            callID = mCallIdBase++;
        }

        return callID;
    }

    /**
     * @param pBatch If true caller doesn't want to wait for call sending,
     * but get immediately back. This allows call batching via worker thread.
     * Batching causes always "batching latency" in call to allow system to
     * gather together multiple calls occurrence during "batching interval".
     *
     * @return state for call, allows receiving result for call
     */
    public synchronized CallState send(
            TransportKey pKey,
            Call pCall,
            boolean pBatch)
        throws RemoteException
    {
        CallState state = new CallState(nextCallId(), pKey);
        // TODO KI could be
        // a) synchronous
        // b) asynhronous
        return state;
    }

    /**
     * Get result back for pCallID
     */
    public synchronized Result receive(CallState pState)
        throws RemoteException
    {
        Result result = null;
        return result;
    }

}
