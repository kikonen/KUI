package org.kari.call;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Connection in server side
 *
 * @author kari
 */
public final class ConnectionPool {
    private final Map<ClientKey, List<ServerHandler>> mConnections = new THashMap<ClientKey, List<ServerHandler>>();


    synchronized void register(ServerHandler pConn) {
        release(pConn);
    }

    synchronized void unregister(ServerHandler pConn) {
        ClientKey key = pConn.getClientKey();
        if (key != null) {
            reserve(key);
        }
    }

    /**
     * Reserve connection for pKey. If none are available then connection
     * with client has completely failed
     *
     * @return handler, null if no connections available
     */
    synchronized ServerHandler reserve(ClientKey pKey) {
        ServerHandler conn = null;

        List<ServerHandler> connections = mConnections.get(pKey);
        if (connections != null) {
            while (conn == null && !connections.isEmpty()) {
                conn = connections.get(connections.size() - 1);
                if (!conn.isRunning()) {
                    conn.kill();
                    conn.free();
                    conn = null;
                }
            }
        }

        return conn;
    }

    /**
     * Release connection back to pool after use
     */
    synchronized void release(ServerHandler pConn) {
        if (pConn.isRunning()) {
            List<ServerHandler> handlers = mConnections.get(pConn.getClientKey());
            if (handlers == null) {
                handlers = new ArrayList<ServerHandler>();
                mConnections.put(pConn.getClientKey(), handlers);
            }
            handlers.add(pConn);
        } else {
            pConn.kill();
            pConn.free();
        }
    }
}
