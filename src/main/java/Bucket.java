//import java.io.*;
//import java.nio.ByteBuffer;
//
//// sketches for persistent hash tables from (bounded) strings to strings
//public class Bucket {
//    static final int SLEN = 32; // max string length for keys and vals
//    static final int BLOCKSIZE = 4096;
//    static final int ENTRYWIDTH = SLEN + SLEN;
//    static final int MAX_COUNT = 63;
//    static final int LONG_WIDTH = 8;
//    static final int INT_WIDTH = 4;
//    long pos;
//    int mask;
//    int count;
//    String[] keys = new String[MAX_COUNT];
//    String[] vals = new String[MAX_COUNT];
//    static final int POS_INDEX = 0;
//    static final int MASK_INDEX = POS_INDEX + LONG_WIDTH;
//    static final int COUNT_INDEX = MASK_INDEX + INT_WIDTH;
//    static final int FIRST_ENTRY_INDEX = COUNT_INDEX + INT_WIDTH;
//    static int keyIndex(int i) { return FIRST_ENTRY_INDEX + i * ENTRYWIDTH; }
//    static int valIndex(int i) { return keyIndex(i) + SLEN; }
//    void read(ByteBuffer b) {  // for variety, uses relative positioning
//        pos = b.getLong();
//        mask = b.getInt();
//        count = b.getInt();
//        for (int i = 0; i < MAX_COUNT; ++i) {
//            byte[] kb = new byte[SLEN], vb = new byte[SLEN];
//            keys[i] = new String(b.get(kb, 0, SLEN));
//            vals[i] = new String(b.get(kb, 0, SLEN));
//        }
//    }
//    String get(String key) {
//        for (int j = 0; j < count; ++j) {
//            if (key.equals(keys[j]))
//                return vals[j];
//        }
//        return false;
//    }
//}
//class IndexArray implements Serializable {
//    long[] index;
//    int size;
//    // ...
//    long getBucketPosition(String key) {
//        return index[(key.hash & (size - 1)));
//    }
//
//}
//class PHT {
//    static final String bucketFile = "BUCKETS";
//    IndexArray indexArray;
//    PHT(boolean created) throws IOException, ClassNotFoundException {
//        if (created)
//            indexArray = (IndexArray)
//                    new ObjectInputStream(new FileInputStream("INDEX")).readObject();
//        else ; // ...
//    }
//    // ...
//}
//
//
//
//