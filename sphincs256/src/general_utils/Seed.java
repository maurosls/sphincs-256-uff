/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general_utils;

/**
 *
 * @author Eu
 */
public class Seed {
    
    static final int SPHINCS256_SEED_LENGTH = 32;
    
    static void get_seed(Hash hs, byte[] seed, int seedOff, byte[] sk, Tree.leafaddr a)
    {
        byte[] buffer = new byte[SPHINCS256_SEED_LENGTH + 8];
        long t;
        int i;

        for (i = 0; i < SPHINCS256_SEED_LENGTH; i++)
        {
            buffer[i] = sk[i];
        }

        t = a.level;
        t |= a.subtree << 4;
        t |= a.subleaf << 59;

        //Efetua conversão de long para formato little endian
        int k = SPHINCS256_SEED_LENGTH;
        buffer[k] = (byte) ((int)(t & 0xffffffffL));
        buffer[++k] = (byte) ((int)(t & 0xffffffffL) >>> 8);
        buffer[++k] = (byte) ((int)(t & 0xffffffffL) >>> 16);
        buffer[++k] = (byte) ((int)(t & 0xffffffffL) >>> 24);
        
        k = SPHINCS256_SEED_LENGTH+4;
        buffer[k] = (byte) ((int)(t >>> 32));
        buffer[++k] = (byte) ((int)(t >>> 32) >>> 8);
        buffer[++k] = (byte) ((int)(t >>> 32) >>> 16);
        buffer[++k] = (byte) ((int)(t >>> 32) >>> 24);

        hs.varlen_hash(seed, seedOff, buffer, buffer.length);
    }



    static void prg(byte[] r, int rOff, long rlen, byte[] key, int keyOff)
    {
        byte[]  nonce = new byte[8];

        StreamCipher cipher = new ChaChaEngine(12);

        cipher.init(true, new ParametersWithIV(new KeyParameter(key, keyOff, 32), nonce));

        cipher.processBytes(r, rOff, (int)rlen, r, rOff);

        //crypto_stream_chacha12(r, rlen, nonce, key);
    }
}
