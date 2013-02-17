package org.kari.io;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.concurrent.ConcurrentHashMap;

public final class CompactObjectInputStream
    extends ObjectInputStream
{
    static final class TypeKey {
        private int mCode;
        private String mName;
        
        public TypeKey(int pCode, String pName) {
            mCode = pCode;
            mName = pName;
        }

        @Override
        public int hashCode() {
            return mCode ^ mName.hashCode();
        }

        @Override
        public boolean equals(Object pObj) {
            return pObj != null
                && pObj.getClass() == getClass()
                && mCode == ((TypeKey)pObj).mCode
                && mName.equals( ((TypeKey)pObj).mName );
        }
    }
    
    private static final TIntObjectHashMap<Class> TYPES = CompactObjectOutputStream.TYPES;
    private static final TIntObjectHashMap<String> PREFIX_VALUES = CompactObjectOutputStream.PREFIX_VALUES;
    private static final ConcurrentHashMap<TypeKey, Class> CACHED_TYPES = new ConcurrentHashMap<TypeKey, Class>();

    private final TypeKey mKey = new TypeKey(0, "");
    
    public CompactObjectInputStream()
        throws IOException,
            SecurityException
    {
        super();
    }

    public CompactObjectInputStream(InputStream pIn)
        throws IOException
    {
        super(pIn);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor()
        throws IOException,
            ClassNotFoundException
    {
        final byte id = readByte();

        Class cls;
        if (id == CompactObjectOutputStream.TYPE_ARRAY) {
            cls = CompactObjectOutputStream.ARRAY_CLASS;
        } else if (id == CompactObjectOutputStream.TYPE_CLASS_ID) {
            int classId = readInt();
            cls = TYPES.get(classId);
        } else {
            String suffix = readUTF();
            String prefix = null;

            mKey.mCode = id;
            mKey.mName = suffix;

            cls = CACHED_TYPES.get(mKey);

            if (cls == null) {
                for (int prefixId : PREFIX_VALUES.keys()) {
                    if (id == prefixId) {
                        prefix = PREFIX_VALUES.get(prefixId);
                        break;
                    }
                }
    
                cls = Class.forName(prefix != null ? prefix + suffix : suffix);
                CACHED_TYPES.putIfAbsent(new TypeKey(id, suffix), cls);
            }
        }
        
        return ObjectStreamClass.lookup(cls);
    }
}
