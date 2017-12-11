/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sphincs;

import org.apache.commons.codec.digest.DigestUtils;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_512;

/**
 *
 * @author Eu
 */
public class HashFunctions {
    
    
    protected static byte[] digestSHA256(byte[] data){
        
        byte[] result = new DigestUtils(SHA_256).digest(data);
        
        return result;
    }
    
    protected static byte[] digestSHA512(byte[] data){
        
        byte[] result = new DigestUtils(SHA_512).digest(data);
        
        return result;
    }
    
}
