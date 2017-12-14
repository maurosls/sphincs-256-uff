package general_utils;

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
