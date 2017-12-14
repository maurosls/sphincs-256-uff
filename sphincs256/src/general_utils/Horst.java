package general_utils;

public class Horst {
    
    private static final int SPHINCS256_HASH_LENGTH = 32;
    private static final int HORST_LOGT = 16;
    public static final int HORST_T = (1<<HORST_LOGT);
    public static final int HORST_K = 32;
    private static final int HORST_SKBYTES = 32;
    public static final int HORST_SIGBYTES = (64* SPHINCS256_HASH_LENGTH+((
                                       (HORST_LOGT-6)* SPHINCS256_HASH_LENGTH)+
                                        HORST_SKBYTES)*HORST_K);

    public static final int N_MASKS = (2*(Horst.HORST_LOGT));

    private static void expand_seed(byte[] outseeds, byte[] inseed)
    {
        Seed.prg(outseeds, 0, HORST_T * HORST_SKBYTES, inseed, 0);
    }

    public static int horst_sign(Hash hs,
                          byte[] sig, int sigOff, byte[] pk,
                          byte[] seed,
                          byte[] masks,
                          byte[] m_hash)
    {
        byte[] sk = new byte[ HORST_T * HORST_SKBYTES];
        int idx;
        int i, j, k;
        int sigpos = sigOff;

        byte[] tree = new byte[(2 * HORST_T - 1) * SPHINCS256_HASH_LENGTH];

        expand_seed(sk, seed);

        for (i = 0; i < HORST_T; i++)
        {
            hs.hash_n_n(tree, (HORST_T - 1 + i) * SPHINCS256_HASH_LENGTH, sk, i * HORST_SKBYTES);
        }

        long offset_in, offset_out;
        for (i = 0; i < HORST_LOGT; i++)
        {
            offset_in = (1 << (HORST_LOGT - i)) - 1;
            offset_out = (1 << (HORST_LOGT - i - 1)) - 1;
            for (j = 0; j < (1 << (HORST_LOGT - i - 1)); j++)
            {
                hs.hash_2n_n_mask(tree, (int)((offset_out + j) * SPHINCS256_HASH_LENGTH),
                                               tree, (int)((offset_in + 2 * j) * 
                                                      SPHINCS256_HASH_LENGTH), 
                                               masks, 2 * i * SPHINCS256_HASH_LENGTH);
            }
        }

        for (j = 63 * SPHINCS256_HASH_LENGTH; j < 127 * SPHINCS256_HASH_LENGTH; j++)
        {
            sig[sigpos++] = tree[j];
        }

        for (i = 0; i < HORST_K; i++)
        {
            idx = (m_hash[2 * i] & 0xff) + ((m_hash[2 * i + 1] & 0xff) << 8);

            for (k = 0; k < HORST_SKBYTES; k++)
                sig[sigpos++] = sk[idx * HORST_SKBYTES + k];

            idx += (HORST_T - 1);
            for (j = 0; j < HORST_LOGT - 6; j++)
            {
                idx = ((idx & 1) != 0) ? idx + 1 : idx - 1;
                for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                    sig[sigpos++] = tree[idx * SPHINCS256_HASH_LENGTH + k];
                idx = (idx - 1) / 2;
            }
        }

        for (i = 0; i < SPHINCS256_HASH_LENGTH; i++)
        {
            pk[i] = tree[i];
        }

        return HORST_SIGBYTES;
    }

    public static int horst_verify(Hash hs, byte[] pk, byte[] sig, int sigOff, byte[] masks, byte[] m_hash)
    {
        byte[] buffer = new byte[ 32 * SPHINCS256_HASH_LENGTH];

        int idx;
        int i, j, k;

        int sigOffset = sigOff + 64 * SPHINCS256_HASH_LENGTH;

        for (i = 0; i < HORST_K; i++)
        {
            idx = (m_hash[2 * i] & 0xff) + ((m_hash[2 * i + 1] & 0xff) << 8);

            if ((idx & 1) == 0)
            {
                hs.hash_n_n(buffer, 0, sig, sigOffset);
                for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                    buffer[SPHINCS256_HASH_LENGTH + k] = sig[sigOffset + HORST_SKBYTES + k];
            }
            else
            {
                hs.hash_n_n(buffer, SPHINCS256_HASH_LENGTH, sig, sigOffset);
                for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                    buffer[k] = sig[sigOffset + HORST_SKBYTES + k];
            }
            sigOffset += HORST_SKBYTES + SPHINCS256_HASH_LENGTH;

            for (j = 1; j < HORST_LOGT - 6; j++)
            {
                idx = idx >>> 1;

                if ((idx & 1) == 0)
                {
                    hs.hash_2n_n_mask(buffer, 0, buffer, 0, masks, 2 * (j - 1) * 
                                      SPHINCS256_HASH_LENGTH);
                    for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                        buffer[SPHINCS256_HASH_LENGTH + k] = sig[sigOffset + k];
                }
                else
                {

                    hs.hash_2n_n_mask(buffer, SPHINCS256_HASH_LENGTH, buffer, 0, 
                                      masks, 2 * (j - 1) * SPHINCS256_HASH_LENGTH);
                    for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                        buffer[k] = sig[sigOffset + k];
                }
                sigOffset += SPHINCS256_HASH_LENGTH;
            }

            idx = idx >>> 1;
            hs.hash_2n_n_mask(buffer, 0, buffer, 0, masks, 2 * 
                              (HORST_LOGT - 7) * SPHINCS256_HASH_LENGTH);

            for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                if (sig[sigOff + idx * SPHINCS256_HASH_LENGTH + k] != buffer[k])
                {
                    for (k = 0; k < SPHINCS256_HASH_LENGTH; k++)
                        pk[k] = 0;
                    return -1;
                }
        }

        for (j = 0; j < 32; j++)
        {
            hs.hash_2n_n_mask(buffer, j * SPHINCS256_HASH_LENGTH, sig, sigOff + 
                              2 * j * SPHINCS256_HASH_LENGTH, masks, 2 * 
                              (HORST_LOGT - 6) * SPHINCS256_HASH_LENGTH);
        }

        for (j = 0; j < 16; j++)
        {
            hs.hash_2n_n_mask(buffer, j * SPHINCS256_HASH_LENGTH, buffer, 2 * j * 
                              SPHINCS256_HASH_LENGTH, masks, 2 * (HORST_LOGT - 5) * 
                              SPHINCS256_HASH_LENGTH);
        }

        for (j = 0; j < 8; j++)
        {
            hs.hash_2n_n_mask(buffer, j * SPHINCS256_HASH_LENGTH, buffer, 2 * j * 
                              SPHINCS256_HASH_LENGTH, masks, 2 * (HORST_LOGT - 4) * 
                              SPHINCS256_HASH_LENGTH);
        }

        for (j = 0; j < 4; j++)
        {
            hs.hash_2n_n_mask(buffer, j * SPHINCS256_HASH_LENGTH, buffer, 2 * j * 
                              SPHINCS256_HASH_LENGTH, masks, 2 * (HORST_LOGT - 3) * 
                              SPHINCS256_HASH_LENGTH);
        }

        for (j = 0; j < 2; j++)
        {
            hs.hash_2n_n_mask(buffer, j * SPHINCS256_HASH_LENGTH, buffer, 2 * j * 
                              SPHINCS256_HASH_LENGTH, masks, 2 * (HORST_LOGT - 2) * 
                              SPHINCS256_HASH_LENGTH);
        }

        hs.hash_2n_n_mask(pk, 0, buffer, 0, masks, 2 * 
                          (HORST_LOGT - 1) * SPHINCS256_HASH_LENGTH);

        return 0;
    }
}