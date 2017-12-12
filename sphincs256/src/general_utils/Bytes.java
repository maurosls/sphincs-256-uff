/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general_utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eu
 */
public class Bytes {
    
    public byte[] xor(byte[] bts1, byte[] bts2){
        byte [] result = null;
        if(bts1.length == bts2.length){ 
            result = new byte[bts1.length];
            for (int i = 0; i<bts1.length; i++)
                result[i] =  (byte) (bts1[i] ^ bts2[i]);
            }
        return result;
    }

    public List<byte[]> chunkBytes(byte[] bts, int sizeOfChunk){
        List<byte[]> result = new ArrayList<>();
        byte[] chunk = null;
        for (int i = 0; i < bts.length; i+=sizeOfChunk) {
            System.arraycopy(bts, i, chunk, i, i+sizeOfChunk - i);
            result.add(chunk);
        }
        return result;
    }

//    public List<Integer> intsFromFourBytes(byte[] bts){
//        List<Integer> ints = new ArrayList<>();
//        List<byte[]> chunkInBytes = chunkBytes(bts, 4);
//        for (int i = 0; i < chunkInBytes.size(); i++) {
//            int current = ByteBuffer.wrap(chunkInBytes.get(i)).
//                          order(ByteOrder.LITTLE_ENDIAN).getInt();
//            ints.add(current);
//        }
//        return ints;
//    }
//
//    public byte[] intsToFourBytes(int num){
//        byte[] result = null;
//        return result;
//    }
}
