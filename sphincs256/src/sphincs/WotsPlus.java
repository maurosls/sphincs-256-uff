/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sphincs;

import java.math.*;
import java.util.List;
/**
 *
 * @author Eu
 */
public class WotsPlus {
    
    private int bitsHashes;
    private int paramWinternitz;
    private int l1;//não sei o que é
    private int l2;//não sei o que é
    private int l;//não sei o que é
    
    public WotsPlus (int bitsHashes, int paramWinternitz){
        this.bitsHashes = bitsHashes;
        this.paramWinternitz = paramWinternitz;
        this.l1 = (int)(Math.ceil(bitsHashes/
                                  (int)(Math.log(paramWinternitz)/Math.log(2))));
        this.l2 = (int)(Math.floor((int)(Math.log(this.l1*(paramWinternitz-1))/Math.log(2))/
                                   (int)(Math.log(paramWinternitz)/Math.log(2))))+1;
        this.l = this.l1+this.l2;
    }
    
    public /*tem que descobrir o tipo de retorno*/ void chains(/*verificar parametros*/){
    }

    public /*tem que descobrir o tipo de retorno*/ void int_to_basew(/*verificar parametros*/){
    }

    public /*tem que descobrir o tipo de retorno*/ void chainlengths(/*verificar parametros*/){
    }

    public /*tem que descobrir o tipo de retorno*/ void keygen(byte[] seed, List<byte[]> masks){
    }

    public /*tem que descobrir o tipo de retorno*/ void sign(/*verificar parametros*/){
    }

    public /*tem que descobrir o tipo de retorno*/ void verify(/*verificar parametros*/){
    }

    public int getL() {
        return l;
    }
    
}
