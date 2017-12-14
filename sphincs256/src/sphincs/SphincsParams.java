package sphincs;

import general_utils.Horst;
import general_utils.WotsPlus;

public class SphincsParams {
    
    //Parâmetros do SPHINCS-256 segundo artigo Daniel J. Bernstein
    static final int SUBTREE_HEIGHT = 5;
    static final int TOTALTREE_HEIGHT = 60; //Altura da hyper-tree
    static final int N_LEVELS = (TOTALTREE_HEIGHT / SUBTREE_HEIGHT); //Qtd. de níveis da hyper-tree = 12
    static final int SEED_BYTES = 32;

    static final int SK_RAND_SEED_BYTES = 32;
    static final int MESSAGE_HASH_SEED_BYTES = 32;

    static final int HASH_BYTES = 32; // log2(HORST_T)*HORST_K/8
    static final int MSGHASH_BYTES = 64;

    //Tamanho da assinatura = 1056 bytes
    static final int CRYPTO_PUBLICKEYBYTES = ((Horst.N_MASKS + 1) * HASH_BYTES); 
    //Tamanho da assinatura = 1088 bytes
    static final int CRYPTO_SECRETKEYBYTES = (SEED_BYTES + CRYPTO_PUBLICKEYBYTES - 
                                              HASH_BYTES + SK_RAND_SEED_BYTES);
    //Tamanho da assinatura = 41000 bytes
    static final int CRYPTO_BYTES = (MESSAGE_HASH_SEED_BYTES + (TOTALTREE_HEIGHT + 7) / 8 
                                    + Horst.HORST_SIGBYTES + (TOTALTREE_HEIGHT / SUBTREE_HEIGHT) 
                                    * WotsPlus.WOTS_SIGBYTES + TOTALTREE_HEIGHT * HASH_BYTES);
    /////////////////////
    static final String SEC_KEY_ID = "SK";
    static final String PUB_KEY_ID = "PK";
}
