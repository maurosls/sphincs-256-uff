/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sphincs;

import general_utils.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Omar
 */
public class Sphincs {

    protected static final int bitsHash = 256;
    protected static final int bitsMsgHash = 512;
    protected static final int heightHyperTree = 60;
    protected static final int layersHyperTree = 12;
    protected static final int paramWinternitz = 16;
    protected static final int layersHorstTree = 16;
    protected static final int elemRevealedSecKey = 32;
    protected static WotsPlus wotsP;
    protected static Horst horst;
    
    private class KeysComponents{
        public byte[] secKey1;
        public byte[] secKey2;
        public byte[] pubKey1;
        public List<byte[]> q;
        
        public KeysComponents(byte[] secKey1, byte[] secKey2, 
                              byte[] pubKey1, List<byte[]> q){
            this.secKey1 = secKey1;
            this.secKey2 = secKey2;
            this.pubKey1 = pubKey1;
            this.q = q;
        }
    }
        
    private byte[] address(int level, int subTree, int leaf){
        int toConvert = level | (subTree<<4) | (leaf<<59);
        final ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(toConvert);
        return bb.array();
    }

    private /*verificar tipo de retorno*/ void wots_leaf(byte[] address, 
                                                         byte[] secKey1, 
                                                         List<byte[]> masks){
        byte[] merged = (byte[])Array.newInstance(address.getClass().getComponentType(),
                         address.length+secKey1.length);
        System.arraycopy(address, 0, merged, 0, address.length);
        System.arraycopy(secKey1, 0, merged, address.length, secKey1.length);
        byte[] seed = HashFunctions.digestSHA256(merged);
        
        byte[] pubKeyA = wotsP.keygen(seed,masks);
        
        //FUNÇÃO H
        
        //return root(l_tree(H, pk_A)); 
    }

    private /*verificar tipo de retorno*/ void wots_path(Map<String,Integer> a, 
                                                         byte[] secKey1,
                                                         List<byte[]> q,
                                                         int subh){
        Map<String,Integer> ta = new HashMap<>(a);
        List</*retorno do wots_leaf*/> leafs = new ArrayList<>();
        
        for (int i = 0; i < (1<<subh); i++) {
            ta.replace("leaf", i);
            leafs.add(wots_leaf(address(ta.get("level"),ta.get("subtree"),ta.get("leaf")),secKey1, Q));
        }
        
        List<byte[]> qTree = new ArrayList<>();
        int firstElement = (int) (2*Math.ceil(Math.log(wotsP.getL())/Math.log(2)));
        for (int i = firstElement; i < q.size(); i++) {
            qTree.add(q.get(i));
        }
        
        //FUNÇÃO H
        //TREE
        //return auth_path(tree, a['leaf']), root(tree);
    }

    private KeysComponents keygen(){
        SecureRandom secRndm = new SecureRandom();
        
        byte[] secKey1 = new byte[Math.floorDiv(bitsHash, 8)];
        secRndm.nextBytes(secKey1);
        
        byte[] secKey2 = new byte[Math.floorDiv(bitsHash, 8)];
        secRndm.nextBytes(secKey2);
        
        int p = (int) Math.max(paramWinternitz-1, 2*(heightHyperTree+Math.ceil(
                                               (int)(Math.log(wotsP.getL())/Math.log(2)))));
        List<byte[]> q = new ArrayList<>();
        for (int i = 0; i < p; i++) {
            byte[] current = new byte[Math.floorDiv(bitsHash, 8)];
            secRndm.nextBytes(current);
            q.add(current);
        }
        
        byte[] pubKey1 = keygen_pub(secKey1,q);
        
        KeysComponents keys = new KeysComponents(secKey1, secKey2, pubKey1, q);
        
        return keys;
    }

    private byte[] keygen_pub(byte[] secKey1, List<byte[]> q){
        List<byte[]> addresses = new ArrayList<>();
        for (int i = 0; i < (1<<Math.floorDiv(heightHyperTree,layersHyperTree)); i++) {
            addresses.add(address(layersHyperTree-1,0,i));
        }
        
        List</*retorno do wots_leaf*/> leafs = new ArrayList<>();
        for (byte[] addrs : addresses) {
            leafs.add(wots_leaf(addrs, secKey1, q));
        }
        
        List<byte[]> qTree = new ArrayList<>();
        int firstElement = (int) (2*Math.ceil(Math.log(wotsP.getL())/Math.log(2)));
        for (int i = firstElement; i < q.size(); i++) {
            qTree.add(q.get(i));
        }
        
        //FUNÇÃO H
        
        //byte[] pubKey1 = root(hash_tree(H, leafs));
        //return pubKey1;
    }

    private /*verificar tipo de retorno*/ void sign(/*verificar parametros*/){
    }

    private /*verificar tipo de retorno*/ void verify(/*verificar parametros*/){
    }

    private /*verificar tipo de retorno*/ void pack(/*verificar parametros*/){
    }

    private /*verificar tipo de retorno*/ void unpack(/*verificar parametros*/){
    }
    
    public static void main(String[] args) {
        
        //FALTA IMPLEMENTAR FUNÇÕES DO CHACHA
        
        wotsP = new WotsPlus(bitsHash, paramWinternitz);
        horst = new Horst(bitsHash, bitsMsgHash, elemRevealedSecKey, layersHorstTree);
    }
    
}
