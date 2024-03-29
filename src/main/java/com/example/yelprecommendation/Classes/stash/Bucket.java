package com.example.yelprecommendation.Classes.stash;

import java.io.*;
import java.nio.ByteBuffer;

class Bucket {
    static final int SLEN = 32; // max string length for keys and vals
    static final int BLOCKSIZE = 4096;
    static final int ENTRYWIDTH = SLEN + SLEN;
    static final int MAX_COUNT = 63;
    static final int LONG_WIDTH = 8;
    static final int INT_WIDTH = 4;
    long pos;
    int mask;
    int count;
    String[] keys = new String[MAX_COUNT];
    String[] vals = new String[MAX_COUNT];
    static final int POS_INDEX = 0;
    static final int MASK_INDEX = POS_INDEX + LONG_WIDTH;
    static final int COUNT_INDEX = MASK_INDEX + INT_WIDTH;
    static final int FIRST_ENTRY_INDEX = COUNT_INDEX + INT_WIDTH;
    static int keyIndex(int i) { return FIRST_ENTRY_INDEX + i * ENTRYWIDTH; }
    static int valIndex(int i) { return keyIndex(i) + SLEN; }

    void read(ByteBuffer b) throws UnsupportedEncodingException {
        pos = b.getLong();
        mask = b.getInt();
        count = b.getInt();
        for (int i = 0; i < MAX_COUNT; ++i) {
            byte[] kb = new byte[SLEN], vb = new byte[SLEN];
            b.get(kb, 0, SLEN);
            keys[i] = new String(kb, "UTF-8");
            b.get(vb, 0, SLEN);
            vals[i] = new String(vb, "UTF-8");
        }
    }

    String get(String key) {
        for (int j = 0; j < count; ++j) {
            if (key.equals(keys[j]))
                return vals[j];
        }
        return null; // Changed from false to null for String return type
    }
}

class IndexArray implements Serializable {
    long[] index;
    int size;

    long getBucketPosition(String key) {
        return index[(key.hashCode() & (size - 1))];
    }
}

class PHT {
    static final String bucketFile = "BUCKETS";
    IndexArray indexArray;

    PHT(boolean created) throws IOException, ClassNotFoundException {
        if (created)
            indexArray = (IndexArray) new ObjectInputStream(new FileInputStream("INDEX")).readObject();
        else {
            indexArray = new IndexArray();
            indexArray.index = new long[1000]; // Initial size
        }
    }

    void put(String key, String value) throws IOException, ClassNotFoundException {
        long bucketPosition = indexArray.getBucketPosition(key);
        ByteBuffer buffer = ByteBuffer.allocate(Bucket.BLOCKSIZE);
        try (RandomAccessFile raf = new RandomAccessFile(bucketFile, "rw")) {
            raf.seek(bucketPosition);
            raf.getChannel().read(buffer);
            buffer.flip();
            Bucket bucket = new Bucket();
            bucket.read(buffer);
            // Check if the key already exists in the bucket
            for (int i = 0; i < bucket.count; i++) {
                if (bucket.keys[i].equals(key)) {
                    bucket.vals[i] = value;
                    buffer.clear();
                    buffer.putLong(bucket.pos);
                    buffer.putInt(bucket.mask);
                    buffer.putInt(bucket.count);
                    for (int j = 0; j < Bucket.MAX_COUNT; j++) {
                        buffer.put(bucket.keys[j].getBytes("UTF-8"));
                        buffer.put(bucket.vals[j].getBytes("UTF-8"));
                    }
                    buffer.flip();
                    raf.seek(bucketPosition);
                    raf.getChannel().write(buffer);
                    return;
                }
            }
            // If the key doesn't exist, add it to the bucket
            if (bucket.count < Bucket.MAX_COUNT) {
                bucket.keys[bucket.count] = key;
                bucket.vals[bucket.count] = value;
                bucket.count++;
                buffer.clear();
                buffer.putLong(bucket.pos);
                buffer.putInt(bucket.mask);
                buffer.putInt(bucket.count);
                for (int j = 0; j < Bucket.MAX_COUNT; j++) {
                    buffer.put(bucket.keys[j].getBytes("UTF-8"));
                    buffer.put(bucket.vals[j].getBytes("UTF-8"));
                }
                buffer.flip();
                raf.seek(bucketPosition);
                raf.getChannel().write(buffer);
                return;
            } else {
                // Handle bucket overflow - Not implemented in this example
            }
        }
    }
}
