package general_utils;

public class WotsPlus {
    
    private static final int SPHINCS256_HASH_LENGTH = 32;
    private static final int WOTS_LOGW = 4;
    private static final int WOTS_W = (1 << WOTS_LOGW);
    private static final int WOTS_L1 = ((256 + WOTS_LOGW - 1) / WOTS_LOGW);
    public static final int WOTS_L = 67;
    public static final int WOTS_LOG_L = 7;
    public static final int WOTS_SIGBYTES = (WOTS_L * SPHINCS256_HASH_LENGTH);
    
    private static void expand_seed(byte[] outseeds, int outOff, byte[] inseed, int inOff)
    {
        clear(outseeds, outOff, WOTS_L * SPHINCS256_HASH_LENGTH);

        Seed.prg(outseeds, outOff, WOTS_L * SPHINCS256_HASH_LENGTH, inseed, inOff);
    }

    private static void clear(byte[] bytes, int offSet, int length)
    {
        for (int i = 0; i != length; i++)
        {
            bytes[i + offSet] = 0;
        }
    }

    private static void gen_chain(Hash hs, byte[] out, int outOff, byte[] seed, int seedOff, 
                          byte[] masks, int masksOff, int chainlen)
    {
        int i, j;
        for (j = 0; j < SPHINCS256_HASH_LENGTH; j++)
            out[j + outOff] = seed[j + seedOff];

        for (i = 0; i < chainlen && i < WOTS_W; i++)
            hs.hash_n_n_mask(out, outOff, out, outOff, masks, masksOff + 
                             (i * SPHINCS256_HASH_LENGTH));
    }


    public void wots_pkgen(Hash hs, byte[] pk, int pkOff, byte[] sk, int skOff, byte[] masks, int masksOff)
    {
        int i;
        expand_seed(pk, pkOff, sk, skOff);
        for (i = 0; i < WOTS_L; i++)
            gen_chain(hs, pk, pkOff + i * SPHINCS256_HASH_LENGTH, pk, pkOff + 
                      i * SPHINCS256_HASH_LENGTH, masks, masksOff, WOTS_W - 1);
    }


    public void wots_sign(Hash hs, byte[] sig, int sigOff, byte[] msg, byte[] sk, byte[] masks)
    {
        int[] basew = new int[WOTS_L];
        int i, c = 0;

        for (i = 0; i < WOTS_L1; i += 2)
        {
            basew[i] = msg[i / 2] & 0xf;
            basew[i + 1] = (msg[i / 2] & 0xff) >>> 4;
            c += WOTS_W - 1 - basew[i];
            c += WOTS_W - 1 - basew[i + 1];
        }

        for (; i < WOTS_L; i++)
        {
            basew[i] = c & 0xf;
            c >>>= 4;
        }

        expand_seed(sig, sigOff, sk, 0);

        for (i = 0; i < WOTS_L; i++)
            gen_chain(hs, sig, sigOff + i * SPHINCS256_HASH_LENGTH, 
                      sig, sigOff + i * SPHINCS256_HASH_LENGTH, masks, 0, basew[i]);
    }

    public void wots_verify(Hash hs, byte[] pk, byte[] sig, int sigOff, byte[] msg, byte[] masks)
    {
        int[] basew = new int[WOTS_L];
        int i, c = 0;

        for (i = 0; i < WOTS_L1; i += 2)
        {
            basew[i] = msg[i / 2] & 0xf;
            basew[i + 1] = (msg[i / 2] & 0xff) >>> 4;
            c += WOTS_W - 1 - basew[i];
            c += WOTS_W - 1 - basew[i + 1];
        }

        for (; i < WOTS_L; i++)
        {
            basew[i] = c & 0xf;
            c >>>= 4;
        }

        for (i = 0; i < WOTS_L; i++)
            gen_chain(hs, pk, i * SPHINCS256_HASH_LENGTH, sig, sigOff + 
                      i * SPHINCS256_HASH_LENGTH, masks, 
                      (basew[i] * SPHINCS256_HASH_LENGTH), WOTS_W - 1 - basew[i]);
    }
}