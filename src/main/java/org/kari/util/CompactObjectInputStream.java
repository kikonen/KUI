package org.kari.util;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class CompactObjectInputStream
    extends ObjectInputStream
{

    private static final TIntObjectHashMap<String> TYPES = CompactObjectOutputStream.TYPES;
    private static final TIntObjectHashMap<String> PREFIX_VALUES = CompactObjectOutputStream.PREFIX_VALUES;

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
        byte id = readByte();

        String name;
        if (id == CompactObjectOutputStream.TYPE_ARRAY) {
            name = "[Ljava.lang.Object;";
        } else if (id == CompactObjectOutputStream.TYPE_CLASS_ID) {
            int classId = readInt();
            name = TYPES.get(classId);
        } else {
            name = readUTF();
            String prefix = null;

            for (int prefixId : PREFIX_VALUES.keys()) {
                if (id == prefixId) {
                    prefix = PREFIX_VALUES.get(prefixId);
                    break;
                }
            }

            if (prefix != null) {
                name = prefix + name;
            }
        }
        return ObjectStreamClass.lookup(Class.forName(name));
    }
}
