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
public class ChaCha {
    
    private static final int chachaRounds = 12;

    protected static int rotate(int x, int y)
    {
        return (x << y) | (x >>> -y);
    }

    public static void permute(int rounds, int[] x)
    {
//        if (x.length != 16)
//        {
//            throw new IllegalArgumentException();
//        }
//        if (rounds % 2 != 0)
//        {
//            throw new IllegalArgumentException("Number of rounds must be even");
//        }

        int x00 = x[ 0];
        int x01 = x[ 1];
        int x02 = x[ 2];
        int x03 = x[ 3];
        int x04 = x[ 4];
        int x05 = x[ 5];
        int x06 = x[ 6];
        int x07 = x[ 7];
        int x08 = x[ 8];
        int x09 = x[ 9];
        int x10 = x[10];
        int x11 = x[11];
        int x12 = x[12];
        int x13 = x[13];
        int x14 = x[14];
        int x15 = x[15];

        for (int i = rounds; i > 0; i -= 2)
        {
            x00 += x04; x12 = rotate(x12 ^ x00, 16);
            x08 += x12; x04 = rotate(x04 ^ x08, 12);
            x00 += x04; x12 = rotate(x12 ^ x00, 8);
            x08 += x12; x04 = rotate(x04 ^ x08, 7);
            x01 += x05; x13 = rotate(x13 ^ x01, 16);
            x09 += x13; x05 = rotate(x05 ^ x09, 12);
            x01 += x05; x13 = rotate(x13 ^ x01, 8);
            x09 += x13; x05 = rotate(x05 ^ x09, 7);
            x02 += x06; x14 = rotate(x14 ^ x02, 16);
            x10 += x14; x06 = rotate(x06 ^ x10, 12);
            x02 += x06; x14 = rotate(x14 ^ x02, 8);
            x10 += x14; x06 = rotate(x06 ^ x10, 7);
            x03 += x07; x15 = rotate(x15 ^ x03, 16);
            x11 += x15; x07 = rotate(x07 ^ x11, 12);
            x03 += x07; x15 = rotate(x15 ^ x03, 8);
            x11 += x15; x07 = rotate(x07 ^ x11, 7);
            x00 += x05; x15 = rotate(x15 ^ x00, 16);
            x10 += x15; x05 = rotate(x05 ^ x10, 12);
            x00 += x05; x15 = rotate(x15 ^ x00, 8);
            x10 += x15; x05 = rotate(x05 ^ x10, 7);
            x01 += x06; x12 = rotate(x12 ^ x01, 16);
            x11 += x12; x06 = rotate(x06 ^ x11, 12);
            x01 += x06; x12 = rotate(x12 ^ x01, 8);
            x11 += x12; x06 = rotate(x06 ^ x11, 7);
            x02 += x07; x13 = rotate(x13 ^ x02, 16);
            x08 += x13; x07 = rotate(x07 ^ x08, 12);
            x02 += x07; x13 = rotate(x13 ^ x02, 8);
            x08 += x13; x07 = rotate(x07 ^ x08, 7);
            x03 += x04; x14 = rotate(x14 ^ x03, 16);
            x09 += x14; x04 = rotate(x04 ^ x09, 12);
            x03 += x04; x14 = rotate(x14 ^ x03, 8);
            x09 += x14; x04 = rotate(x04 ^ x09, 7);
        }

        x[ 0] = x00;
        x[ 1] = x01;
        x[ 2] = x02;
        x[ 3] = x03;
        x[ 4] = x04;
        x[ 5] = x05;
        x[ 6] = x06;
        x[ 7] = x07;
        x[ 8] = x08;
        x[ 9] = x09;
        x[10] = x10;
        x[11] = x11;
        x[12] = x12;
        x[13] = x13;
        x[14] = x14;
        x[15] = x15;
    }

    void chachaPermute(byte[] out, byte[] in)
    {
        int i;

        int[] x = new int[16];
        for (i = 0; i < 16; i++)
        {
            //Executa conversão de formato little-endian para inteiro
            int k = 4*i;
            int n = in[k] & 0xff;
            n |= (in[++k] & 0xff) << 8;
            n |= (in[++k] & 0xff) << 16;
            n |= in[++k] << 24;
            x[i] = n;
        }

        permute(chachaRounds, x);

        for (i = 0; i < 16; ++i)
        {
            //Executa conversão de inteiro para formato little-endian
            int k = 4*i;
            out[k] = (byte) (x[i]);
            out[++k] = (byte) (x[i] >>> 8);
            out[++k] = (byte) (x[i] >>> 16);
            out[++k] = (byte) (x[i] >>> 24);
        }
    }
}