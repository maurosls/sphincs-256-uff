/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general_utils;

import org.apache.commons.codec.digest.DigestUtils;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_512;

/**
 *
 * @author Eu
 */
public class Hash {
    
    private static final byte[] hashc = StringsUtils.stringToByteArray
                                        ("expand 32-byte to 64-byte state!");
    private final boolean dig256;
    private final boolean dig512;
    private final ChaCha permutation12 = new ChaCha();

    Hash(boolean dig256)
    {
        this(dig256, false);
    }

    Hash(boolean dig256, boolean dig512)
    {
        this.dig256 = dig256;
        this.dig512 = dig512;
    }

    int varlen_hash(byte[] out, int outOff, byte[] in, int inLen)
    {
//        dig256.update(in, 0, inLen);
//
//        dig256.doFinal(out, outOff);
        out = new DigestUtils(SHA_256).digest(in);

        return 0;
    }

    Digest getMessageHash()
    {
        return dig512;
    }

    int hash_2n_n(byte[] out, int outOff, byte[] in, int inOff)
    {
        byte[] x = new byte[64];
        int i;
        for (i = 0; i < 32; i++)
        {
            x[i] = in[inOff + i];
            x[i + 32] = hashc[i];
        }
        permutation12.chachaPermute(x, x);
        for (i = 0; i < 32; i++)
        {
            x[i] = (byte)(x[i] ^ in[inOff + i + 32]);
        }
        permutation12.chachaPermute(x, x);
        for (i = 0; i < 32; i++)
        {
            out[outOff + i] = x[i];
        }

        return 0;
    }

    int hash_2n_n_mask(byte[] out, int outOff, byte[] in, int inOff, byte[] mask, int maskOff)
    {
        byte[] buf = new byte[2 * 32];
        int i;
        for (i = 0; i < 2 * 32; i++)
        {
            buf[i] = (byte)(in[inOff + i] ^ mask[maskOff + i]);
        }

        int rv = hash_2n_n(out, outOff, buf, 0);

        return rv;
    }

    int hash_n_n(byte[] out, int outOff, byte[] in, int inOff)
    {

        byte[] x = new byte[64];
        int i;

        for (i = 0; i < 32; i++)
        {
            x[i] = in[inOff + i];
            x[i + 32] = hashc[i];
        }
        permutation12.chachaPermute(x, x);
        for (i = 0; i < 32; i++)
        {
            out[outOff + i] = x[i];
        }

        return 0;
    }

    int hash_n_n_mask(byte[] out, int outOff, byte[] in, int inOff,  byte[] mask, int maskOff)
    {
        byte[] buf = new byte[32];
        int i;
        for (i = 0; i < 32; i++)
        {
            buf[i] = (byte)(in[inOff + i] ^ mask[maskOff + i]);
        }
        return hash_n_n(out, outOff, buf, 0);
    }
}