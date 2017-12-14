package sphincs;

import java.security.SecureRandom;
import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;

import general_utils.Tree;
import general_utils.Horst;
import general_utils.Hash;

public class SphincsKeyPair {
    
    private SecureRandom random;
    private DigestUtils treeDigest;
    
    public void init (SphincsKeysParams params){
        this.random = params.getRandom();
        this.treeDigest = params.getTreeDigest();
    }
    
    public Pair<byte[],byte[]> generateKeyPair(){
        Tree.leafaddr a = new Tree.leafaddr();

        byte[] sk = new byte[SphincsParams.CRYPTO_SECRETKEYBYTES];

        random.nextBytes(sk);

        byte[] pk = new byte[SphincsParams.CRYPTO_PUBLICKEYBYTES];

        System.arraycopy(sk, SphincsParams.SEED_BYTES, pk, 0, Horst.N_MASKS * 
                         SphincsParams.HASH_BYTES);

        a.level = SphincsParams.N_LEVELS - 1;
        a.subtree = 0;
        a.subleaf = 0;

        Hash hs = new Hash(treeDigest);

        // Formato publicKey: [|N_MASKS*params.HASH_BYTES| Bitmasks || root]
        Tree.treehash(hs, pk, (Horst.N_MASKS * SphincsParams.HASH_BYTES), 
                      SphincsParams.SUBTREE_HEIGHT, sk, a, pk, 0);
        
        Pair<byte[],byte[]> keys = new Pair(pk,sk);

        return keys;
    }
}
