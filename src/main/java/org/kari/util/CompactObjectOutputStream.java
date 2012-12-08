package org.kari.util;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.rmi.dgc.VMID;
import java.rmi.server.UID;

import ca.odell.glazedlists.impl.adt.gnutrove.TIntArrayList;

/**
 * Outputstream with more compact format
 * 
 * @author kari
 */
public class CompactObjectOutputStream
    extends ObjectOutputStream
{
    public static final byte TYPE_PLAIN = 0;
    public static final byte TYPE_CLASS_ID = 1;
    public static final byte TYPE_ARRAY = 'A';

    public static final TIntObjectHashMap<String> TYPES = new TIntObjectHashMap<String>();

    public static final TIntObjectHashMap<String> PREFIX_VALUES = new TIntObjectHashMap<String>();
    public static final TIntArrayList PREFIX_IDS = new TIntArrayList();
    static {
        PREFIX_VALUES.put('K', "org.kari.");
        PREFIX_VALUES.put('R', "java.rmi.");
        PREFIX_VALUES.put('L', "java.lang.");
        PREFIX_VALUES.put('J', "java.");
        PREFIX_IDS.add('K');
        PREFIX_IDS.add('R');
        PREFIX_IDS.add('L');
        PREFIX_IDS.add('J');
        
        TYPES.put(1, UID.class.getName());
        TYPES.put(2, VMID.class.getName());
    }

    public CompactObjectOutputStream()
        throws IOException,
            SecurityException
    {
        super();
    }

    public CompactObjectOutputStream(OutputStream pOut)
        throws IOException
    {
        super(pOut);
    }

    @Override
    protected void writeClassDescriptor(ObjectStreamClass desc)
        throws IOException
    {
        byte id = TYPE_PLAIN;
        String name =  desc.getName();
        int classId = 0;
        
        if (name.equals("[Ljava.lang.Object;")) {
            id = TYPE_ARRAY;
            name = null;
        } else {
            for (int clsId : TYPES.keys()) {
                String className = TYPES.get(clsId);
                if (className.equals(name)) {
                    id = TYPE_CLASS_ID;
                    classId = clsId;
                    break;
                }
            }
        
            if (classId == 0) {
                for (int prefixId : PREFIX_IDS.toNativeArray()) {
                    String prefix = PREFIX_VALUES.get(prefixId);
                    if (name.startsWith(prefix)) {
                        id = (byte)prefixId;
                        name = name.substring(prefix.length());
                        break;
                    }
                }
            }
        }
        
        writeByte(id);
        if (classId != 0) {
            writeInt(classId);
        } else if (name != null) {
            writeUTF(name);
        }
    }
}
