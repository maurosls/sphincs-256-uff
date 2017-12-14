package sphincs;

import java.security.SecureRandom;
import org.apache.commons.codec.digest.DigestUtils;

public class SphincsKeysParams {
    
    private byte[] secKeyData;
    private byte[] pubKeyData;
    private DigestUtils treeDigest;
    private SecureRandom random;
    
    public SphincsKeysParams(byte[] keyData, String typeKey){
        if(typeKey.equals(SphincsParams.PUB_KEY_ID))
            System.arraycopy(keyData, 0, this.pubKeyData, 0, keyData.length);
        else if(typeKey.equals(SphincsParams.SEC_KEY_ID))
            System.arraycopy(keyData, 0, this.secKeyData, 0, keyData.length);
    }
    
    public SphincsKeysParams(SecureRandom random, DigestUtils treeDigest){
        this.random = random;
        this.treeDigest = treeDigest;
    }

    public byte[] getSecKeyData() {
        byte[] copy = null;
        System.arraycopy(this.secKeyData, 0, copy, 0, this.secKeyData.length);
        return copy;
    }

    public byte[] getPubKeyData() {
        byte[] copy = null;
        System.arraycopy(this.pubKeyData, 0, copy, 0, this.pubKeyData.length);
        return copy;
    }

    public DigestUtils getTreeDigest() {
        return this.treeDigest;
    }

    public SecureRandom getRandom() {
        return this.random;
    }
}
