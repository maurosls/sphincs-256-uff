package sphincs;

import general_utils.StringsUtils;
import javax.swing.JOptionPane;
import org.apache.commons.codec.digest.DigestUtils;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_512;

public class Sphincs256Signer {

    public static void main(String[] args) {
        byte[] msg = new byte[0];
        while(msg.length==0){
            byte[] msgAux = null;
            String msgText = JOptionPane.showInputDialog("Digite a mensagem:");
            msgAux = StringsUtils.stringToByteArray(msgText);
            if(msgAux.length<=64)
                msg = msgAux;
        }
        
        DigestUtils nDigest = new DigestUtils(SHA_256);
        DigestUtils twoNDigest = new DigestUtils(SHA_512);
        Sphincs256 sphcs = new Sphincs256(nDigest, twoNDigest);
        byte[] signature = sphcs.sign(msg);
        sphcs.verifySign(msg, signature);
    }
    
}
