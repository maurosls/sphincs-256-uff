package general_utils;

import org.apache.commons.codec.digest.DigestUtils;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_256;
import static org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_512;

public class Hash {
    
    private static final byte[] HASHC = StringsUtils.stringToByteArray
                                        ("expand 32-byte to 64-byte state!");
    private final DigestUtils dig256;
    private final DigestUtils dig512;
    private final ChaCha permutation12 = new ChaCha();

    public Hash(DigestUtils dig256)
    {
        this(dig256, null);
    }

    public Hash(DigestUtils dig256, DigestUtils dig512)
    {
        this.dig256 = dig256;
        this.dig512 = dig512;
    }

    int varlen_hash(byte[] out, int outOffset, byte[] in, int inLength)
    {
        out = new DigestUtils(SHA_256).digest(in);
        return 0;
    }

    public DigestUtils getMessageHash()
    {
        return new DigestUtils(SHA_512);
    }

    private int hash_2n_n(byte[] out, int outOffset, byte[] in, int inOffset)
    {
        byte[] x = new byte[64];
        int i;
        for (i = 0; i < 32; i++)
        {
            x[i] = in[inOffset + i];
            x[i + 32] = HASHC[i];
        }
        permutation12.chachaPermute(x, x);
        for (i = 0; i < 32; i++)
        {
            x[i] = (byte)(x[i] ^ in[inOffset + i + 32]);
        }
        permutation12.chachaPermute(x, x);
        for (i = 0; i < 32; i++)
        {
            out[outOffset + i] = x[i];
        }

        return 0;
    }

    public int hash_2n_n_mask(byte[] out, int outOffset, byte[] in, int inOffset, 
                       byte[] mask, int maskOffset)
    {
        byte[] buf = new byte[2 * 32];
        int i;
        for (i = 0; i < 2 * 32; i++)
        {
            buf[i] = (byte)(in[inOffset + i] ^ mask[maskOffset + i]);
        }

        int rv = hash_2n_n(out, outOffset, buf, 0);

        return rv;
    }

    int hash_n_n(byte[] out, int outOffset, byte[] in, int inOffset)
    {

        byte[] x = new byte[64];
        int i;

        for (i = 0; i < 32; i++)
        {
            x[i] = in[inOffset + i];
            x[i + 32] = HASHC[i];
        }
        permutation12.chachaPermute(x, x);
        for (i = 0; i < 32; i++)
        {
            out[outOffset + i] = x[i];
        }

        return 0;
    }

    int hash_n_n_mask(byte[] out, int outOffset, byte[] in, int inOffset , 
                      byte[] mask, int maskOffset)
    {
        byte[] buf = new byte[32];
        int i;
        for (i = 0; i < 32; i++)
        {
            buf[i] = (byte)(in[inOffset + i] ^ mask[maskOffset + i]);
        }
        return hash_n_n(out, outOffset, buf, 0);
    }
}