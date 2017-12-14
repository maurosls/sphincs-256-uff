/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general_utils;

/**
 *
 * @author Eu
 */
public class StringsUtils {
    
    public static byte[] stringToByteArray(String s){
        
        byte[] bytes = new byte[s.length()];

        for (int i = 0; i != bytes.length; i++)
        {
            char ch = s.charAt(i);

            bytes[i] = (byte)ch;
        }

        return bytes;
    }
}
