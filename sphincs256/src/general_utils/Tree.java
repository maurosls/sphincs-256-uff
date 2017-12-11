/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general_utils;

import java.util.ArrayList;
import java.util.List;
import java.math.*;

/**
 *
 * @author Eu
 */
public class Tree {
    
    public /*verificar tipo de retorno*/void hashTree(List<byte[]> leafs){
        assert (leafs.size() & leafs.size()-1)==0;
        return lTree(leafs);
    }
    
    public /*verificar tipo de retorno*/void lTree(List<byte[]> leafs){
        List<byte[]> layer = new ArrayList<>(leafs);
        //yield layer
        for (int i = 0; i < (int)(Math.ceil(Math.log(leafs.size())/Math.log(2))); i++) {
            List<byte[]> nextLayer = new ArrayList<>();
            
        }
    }
}
