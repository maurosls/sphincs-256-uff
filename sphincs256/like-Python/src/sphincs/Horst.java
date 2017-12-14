/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sphincs;

import java.math.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Eu
 */
public class Horst {
    
    private int bitsHashes;
    private int bitsMsgHash;
    private int lenSecKeyRevealed;
    private int treeLayers;
    private int t;
    private int x;
    
    public Horst(int lenHashes, int lenMsgHash, int secretKeyRevealed, int treeLayers){
        assert secretKeyRevealed*treeLayers == lenMsgHash;
        this.bitsHashes = lenHashes;
        this.bitsMsgHash = lenMsgHash;
        this.lenSecKeyRevealed = secretKeyRevealed;
        this.treeLayers = treeLayers;
        this.t = 1<<treeLayers;
        //this.x = Math.max();
    }
    
    public List<Integer> genMsgIdx(/*message*/){
        List<Integer> msgIdx = new ArrayList<>();
        
        return msgIdx;
    }
    
    public /*verificar tipo de retorno*/ void keygen (/*seed, masks*/){
        
    }
    
    public /*verificar tipo de retorno*/ void sign(/*message, seed, masks*/){
        
    }

    public /*verificar tipo de retorno*/ void verify(/*message, signature, masks*/){
        
    }
    
}
