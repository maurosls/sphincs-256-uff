package sphincs;

import general_utils.Hash;
import general_utils.Horst;
import general_utils.Seed;
import general_utils.Tree;
import general_utils.WotsPlus;
import org.apache.commons.codec.digest.DigestUtils;

public class Sphincs256 {
    
    private final Hash hashFunctions;

    private byte[] keyData;

    //n-digest produz 32 bytes de saída (SHA-256) e é usado na construção da árvore
    //2n-digest produz 64 bytes de saída (SHA-512) e é usado para o hashing da
    //mensagem, das chaves e da semente do gerador aleatório
    public Sphincs256(DigestUtils nDigest, DigestUtils twoNDigest)
    {
        this.hashFunctions = new Hash(nDigest, twoNDigest);
    }

    public void init(boolean forSigning, SphincsKeysParams param)
    {
         if (forSigning)
         {
             keyData = param.getSecKeyData();
         }
         else
         {
             keyData = param.getPubKeyData();
         }
    }

    public byte[] sign(byte[] message)
    {
        return crypto_sign(hashFunctions, message, keyData);
    }

    public boolean verifySign(byte[] message, byte[] signature)
    {
        return verify(hashFunctions, message, signature, keyData);
    }

    //Verifica se o caminho de autorização baseado na árvore, é válido
    private static void validate_authpath(Hash hs, byte[] root, byte[] leaf, int leafIndex, 
                                  byte[] authpath, int authOffset, byte[] masks, 
                                  int height)
    {
        int i, j;
        byte[] buffer = new byte[2 * SphincsParams.HASH_BYTES];

        if ((leafIndex & 1) != 0)
        {
            for (j = 0; j < SphincsParams.HASH_BYTES; j++)
            {
                buffer[SphincsParams.HASH_BYTES + j] = leaf[j];
            }
            for (j = 0; j < SphincsParams.HASH_BYTES; j++)
            {
                buffer[j] = authpath[authOffset + j];
            }
        }
        else
        {
            for (j = 0; j < SphincsParams.HASH_BYTES; j++)
            {
                buffer[j] = leaf[j];
            }
            for (j = 0; j < SphincsParams.HASH_BYTES; j++)
            {
                buffer[SphincsParams.HASH_BYTES + j] = authpath[authOffset + j];
            }
        }
        int authOff = authOffset + SphincsParams.HASH_BYTES;

        for (i = 0; i < height - 1; i++)
        {
            leafIndex >>>= 1;
            if ((leafIndex & 1) != 0)
            {
                hs.hash_2n_n_mask(buffer, SphincsParams.HASH_BYTES, buffer, 0, 
                                  masks, 2 * (WotsPlus.WOTS_LOG_L + i) * 
                                  SphincsParams.HASH_BYTES);
                for (j = 0; j < SphincsParams.HASH_BYTES; j++)
                {
                    buffer[j] = authpath[authOff + j];
                }
            }
            else
            {
                hs.hash_2n_n_mask(buffer, 0, buffer, 0, masks, 2 * (WotsPlus.WOTS_LOG_L + i) 
                                  * SphincsParams.HASH_BYTES);
                for (j = 0; j < SphincsParams.HASH_BYTES; j++)
                {
                    buffer[j + SphincsParams.HASH_BYTES] = authpath[authOff + j];
                }
            }
            authOff += SphincsParams.HASH_BYTES;
        }
        hs.hash_2n_n_mask(root, 0, buffer, 0, masks, 2 * (WotsPlus.WOTS_LOG_L + height - 1) 
                          * SphincsParams.HASH_BYTES);
    }

    //Constrói o caminho de autorização a partir da WOTS+
    private static void compute_authpath_wots(Hash hs, byte[] root, byte[] authpath, 
                                      int authOff, Tree.leafaddr a, byte[] sk, 
                                      byte[] masks, int height)
    {
        int i, idx, j;
        Tree.leafaddr ta = new Tree.leafaddr(a);

        byte[] tree = new byte[2 * (1 << SphincsParams.SUBTREE_HEIGHT) * SphincsParams.HASH_BYTES];
        byte[] seed = new byte[(1 << SphincsParams.SUBTREE_HEIGHT) * SphincsParams.SEED_BYTES];
        byte[] pk = new byte[(1 << SphincsParams.SUBTREE_HEIGHT) * WotsPlus.WOTS_L 
                             * SphincsParams.HASH_BYTES];

        for (ta.subleaf = 0; ta.subleaf < (1 << SphincsParams.SUBTREE_HEIGHT); ta.subleaf++)
        {
            Seed.get_seed(hs, seed, (int)(ta.subleaf * SphincsParams.SEED_BYTES), sk, ta);
        }

        WotsPlus w = new WotsPlus();

        for (ta.subleaf = 0; ta.subleaf < (1 << SphincsParams.SUBTREE_HEIGHT); ta.subleaf++)
        {
            w.wots_pkgen(hs, pk, (int)(ta.subleaf * WotsPlus.WOTS_L * SphincsParams.HASH_BYTES),
                         seed, (int)(ta.subleaf * SphincsParams.SEED_BYTES), masks, 0);
        }

        for (ta.subleaf = 0; ta.subleaf < (1 << SphincsParams.SUBTREE_HEIGHT); ta.subleaf++)
        {
            Tree.l_tree(hs, tree, (int)((1 << SphincsParams.SUBTREE_HEIGHT) * 
                                        SphincsParams.HASH_BYTES + ta.subleaf * 
                                        SphincsParams.HASH_BYTES),
                pk, (int)(ta.subleaf * WotsPlus.WOTS_L * SphincsParams.HASH_BYTES), masks, 0);
        }

        int level = 0;

        for (i = (1 << SphincsParams.SUBTREE_HEIGHT); i > 0; i >>>= 1)
        {
            for (j = 0; j < i; j += 2)
            {
                hs.hash_2n_n_mask(tree, (i >>> 1) * SphincsParams.HASH_BYTES + (j >>> 1) 
                                  * SphincsParams.HASH_BYTES,
                    tree, i * SphincsParams.HASH_BYTES + j * SphincsParams.HASH_BYTES,
                    masks, 2 * (WotsPlus.WOTS_LOG_L + level) * SphincsParams.HASH_BYTES);
            }

            level++;
        }


        idx = (int)a.subleaf;

        for (i = 0; i < height; i++)
        {
            System.arraycopy(tree, ((1 << SphincsParams.SUBTREE_HEIGHT) >>> i) * 
                                    SphincsParams.HASH_BYTES + ((idx >>> i) ^ 1) * 
                                    SphincsParams.HASH_BYTES, authpath, authOff + i * 
                                    SphincsParams.HASH_BYTES, SphincsParams.HASH_BYTES);
        }

        System.arraycopy(tree, SphincsParams.HASH_BYTES, root, 0,  SphincsParams.HASH_BYTES);
    }

    //Gera a assinatura da mensagem
    private byte[] crypto_sign(Hash hs, byte[] m, byte[] sk)
    {
        byte[] sm = new byte[SphincsParams.CRYPTO_BYTES];

        int i;
        long leafidx;
        byte[] R = new byte[SphincsParams.MESSAGE_HASH_SEED_BYTES];
        byte[] m_h = new byte[SphincsParams.MSGHASH_BYTES];
        long[] rnd = new long[8];

        byte[] root = new byte[SphincsParams.HASH_BYTES];
        byte[] seed = new byte[SphincsParams.SEED_BYTES];
        byte[] masks = new byte[Horst.N_MASKS * SphincsParams.HASH_BYTES];
        int pk;
        byte[] tsk = new byte[SphincsParams.CRYPTO_SECRETKEYBYTES];

        for (i = 0; i < SphincsParams.CRYPTO_SECRETKEYBYTES; i++)
        {
            tsk[i] = sk[i];
        }

        //Cria o índice da folha deterministicamente
        {
            int scratch = SphincsParams.CRYPTO_BYTES - SphincsParams.SK_RAND_SEED_BYTES;

            System.arraycopy(tsk, SphincsParams.CRYPTO_SECRETKEYBYTES - 
                             SphincsParams.SK_RAND_SEED_BYTES, sm, scratch, 
                             SphincsParams.SK_RAND_SEED_BYTES);

            DigestUtils d = hs.getMessageHash();
            byte[] bRnd = new byte[64];

            //EXECUTAR SHA-512 EM UM TRECHO DA MENSAGEM
//            d.update(sm, scratch, SphincsParams.SK_RAND_SEED_BYTES);
//
//            d.update(m, 0, m.length);
//
//            d.doFinal(bRnd, 0);

            zerobytes(sm, scratch, SphincsParams.SK_RAND_SEED_BYTES);

            for (int j = 0; j != rnd.length; j++)
            {
                //Executa conversão do formato little endian to long
                int k = j*8;
                int lo = bRnd[k] & 0xff;
                lo |= (bRnd[++k] & 0xff) << 8;
                lo |= (bRnd[++k] & 0xff) << 16;
                lo |= bRnd[++k] << 24;
                
                k +=4;
                int hi = bRnd[k] & 0xff;
                lo |= (bRnd[++k] & 0xff) << 8;
                lo |= (bRnd[++k] & 0xff) << 16;
                lo |= bRnd[++k] << 24;
                
                rnd[j] = ((long)(hi & 0xffffffffL) << 32) | (long)(lo & 0xffffffffL);
            }
            leafidx = rnd[0] & 0xfffffffffffffffL;

            System.arraycopy(bRnd, 16, R, 0, SphincsParams.MESSAGE_HASH_SEED_BYTES);

            scratch = SphincsParams.CRYPTO_BYTES - SphincsParams.MESSAGE_HASH_SEED_BYTES 
                      - SphincsParams.CRYPTO_PUBLICKEYBYTES;

            System.arraycopy(R, 0, sm, scratch, SphincsParams.MESSAGE_HASH_SEED_BYTES);

            Tree.leafaddr b = new Tree.leafaddr();
            b.level = SphincsParams.N_LEVELS - 1;
            b.subtree = 0;
            b.subleaf = 0;

            pk = scratch + SphincsParams.MESSAGE_HASH_SEED_BYTES;

            System.arraycopy(tsk, SphincsParams.SEED_BYTES, sm, pk, Horst.N_MASKS 
                             * SphincsParams.HASH_BYTES);

            Tree.treehash(hs, sm, pk + (Horst.N_MASKS * SphincsParams.HASH_BYTES), 
                          SphincsParams.SUBTREE_HEIGHT, tsk, b, sm, pk);

            d = hs.getMessageHash();

            //EXECUTAR SHA-512 NUM TRECHO DA MENSAGEM
//            d.update(sm, scratch, SphincsParams.MESSAGE_HASH_SEED_BYTES + 
//                     SphincsParams.CRYPTO_PUBLICKEYBYTES);
//            d.update(m, 0, m.length);
//            d.doFinal(m_h, 0);
        }

        Tree.leafaddr a = new Tree.leafaddr();

        a.level = SphincsParams.N_LEVELS;
        a.subleaf = (int)(leafidx & ((1 << SphincsParams.SUBTREE_HEIGHT) - 1));
        a.subtree = leafidx >>> SphincsParams.SUBTREE_HEIGHT;

        for (i = 0; i < SphincsParams.MESSAGE_HASH_SEED_BYTES; i++)
        {
            sm[i] = R[i];
        }

        int smOff = SphincsParams.MESSAGE_HASH_SEED_BYTES;

        System.arraycopy(tsk, SphincsParams.SEED_BYTES, masks, 0, Horst.N_MASKS 
                         * SphincsParams.HASH_BYTES);
        for (i = 0; i < (SphincsParams.TOTALTREE_HEIGHT + 7) / 8; i++)
        {
            sm[smOff + i] = (byte)((leafidx >>> 8 * i) & 0xff);
        }

        smOff += (SphincsParams.TOTALTREE_HEIGHT + 7) / 8;

        Seed.get_seed(hs, seed, 0, tsk, a);
        Horst ht = new Horst();

        int horst_sigbytes = ht.horst_sign(hs, sm, smOff, root, seed, masks, m_h);

        smOff += horst_sigbytes;

        WotsPlus w = new WotsPlus();

        for (i = 0; i < SphincsParams.N_LEVELS; i++)
        {
            a.level = i;

            Seed.get_seed(hs, seed, 0, tsk, a);

            w.wots_sign(hs, sm, smOff, root, seed, masks);

            smOff += WotsPlus.WOTS_SIGBYTES;

            compute_authpath_wots(hs, root, sm, smOff, a, tsk, masks, SphincsParams.SUBTREE_HEIGHT);
            smOff += SphincsParams.SUBTREE_HEIGHT * SphincsParams.HASH_BYTES;

            a.subleaf = (int)(a.subtree & ((1 << SphincsParams.SUBTREE_HEIGHT) - 1));
            a.subtree >>>= SphincsParams.SUBTREE_HEIGHT;
        }

        zerobytes(tsk, 0, SphincsParams.CRYPTO_SECRETKEYBYTES);

        return sm;
    }

    private void zerobytes(byte[] tsk, int off, int cryptoSecretkeybytes)
    {
        for (int i = 0; i != cryptoSecretkeybytes; i++)
        {
            tsk[off + i] = 0;
        }
    }

    //Verifica se assinatura criada é válida
    private boolean verify(Hash hs, byte[] m, byte[] sm, byte[] pk)
    {
        int i;
        int smlen = sm.length;
        long leafidx = 0;
        byte[] wots_pk = new byte[ WotsPlus.WOTS_L * SphincsParams.HASH_BYTES];
        byte[] pkhash = new byte[ SphincsParams.HASH_BYTES];
        byte[] root = new byte[ SphincsParams.HASH_BYTES];
        byte[] sig = new byte[ SphincsParams.CRYPTO_BYTES];
        int sigp;
        byte[] tpk = new byte[ SphincsParams.CRYPTO_PUBLICKEYBYTES];

        if (smlen != SphincsParams.CRYPTO_BYTES)
        {
            throw new IllegalArgumentException("signature wrong size");
        }

        byte[] m_h = new byte[ SphincsParams.MSGHASH_BYTES];

        for (i = 0; i < SphincsParams.CRYPTO_PUBLICKEYBYTES; i++)
            tpk[i] = pk[i];

        //Cria o hash da mensagem
        {
            byte[] R = new byte[ SphincsParams.MESSAGE_HASH_SEED_BYTES];

            for (i = 0; i < SphincsParams.MESSAGE_HASH_SEED_BYTES; i++)
                R[i] = sm[i];

            System.arraycopy(sm, 0, sig, 0, SphincsParams.CRYPTO_BYTES);

            DigestUtils mHash = hs.getMessageHash();
            
            //EXECUTAR SHA-512 NUM TRECHO DA MENSAGEM
//            mHash.update(R, 0, SphincsParams.MESSAGE_HASH_SEED_BYTES);
//
//            mHash.update(tpk, 0, SphincsParams.CRYPTO_PUBLICKEYBYTES);
//
//            mHash.update(m, 0, m.length);
//
//            mHash.doFinal(m_h, 0);
        }

        sigp = 0;

        sigp += SphincsParams.MESSAGE_HASH_SEED_BYTES;
        smlen -= SphincsParams.MESSAGE_HASH_SEED_BYTES;


        for (i = 0; i < (SphincsParams.TOTALTREE_HEIGHT + 7) / 8; i++)
        {
            leafidx ^= ((long)(sig[sigp + i] & 0xff) << (8 * i));
        }


        new Horst().horst_verify(hs, root, sig, sigp + (SphincsParams.TOTALTREE_HEIGHT + 7) / 8,
            tpk, m_h);

        sigp += (SphincsParams.TOTALTREE_HEIGHT + 7) / 8;
        smlen -= (SphincsParams.TOTALTREE_HEIGHT + 7) / 8;

        sigp += Horst.HORST_SIGBYTES;
        smlen -= Horst.HORST_SIGBYTES;

        WotsPlus w = new WotsPlus();

        for (i = 0; i < SphincsParams.N_LEVELS; i++)
        {
            w.wots_verify(hs, wots_pk, sig, sigp, root, tpk);

            sigp += WotsPlus.WOTS_SIGBYTES;
            smlen -= WotsPlus.WOTS_SIGBYTES;

            Tree.l_tree(hs, pkhash, 0, wots_pk, 0, tpk, 0);
            validate_authpath(hs, root, pkhash, (int)(leafidx & 0x1f), sig, sigp,
                              tpk, SphincsParams.SUBTREE_HEIGHT);
            leafidx >>= 5;

            sigp += SphincsParams.SUBTREE_HEIGHT * SphincsParams.HASH_BYTES;
            smlen -= SphincsParams.SUBTREE_HEIGHT * SphincsParams.HASH_BYTES;
        }

        boolean verified = true;
        for (i = 0; i < SphincsParams.HASH_BYTES; i++)
        {
            if (root[i] != tpk[i + Horst.N_MASKS * SphincsParams.HASH_BYTES])
            {
                verified = false;
            }
        }

        return verified;
    }
    
}
