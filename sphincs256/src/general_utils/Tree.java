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
public class Tree {
    
    static final int SPHINCS256_HASH_LENGTH = 32;
    static final int SPHINCS256_SEED_LENGTH = 32;
    
    static class leafaddr
    {
        int level;
        long subtree;
        long subleaf;

        public leafaddr()
        {

        }

        public leafaddr(leafaddr leafaddr)
        {
            this.level = leafaddr.level;
            this.subtree = leafaddr.subtree;
            this.subleaf = leafaddr.subleaf;
        }
    }

    static void l_tree(Hash hs, byte[] leaf, int leafOff, byte[] wots_pk, int pkOff, 
                       byte[] masks, int masksOff)
    {
        int l = WotsPlus.WOTS_L;
        int i, j = 0;
        for (i = 0; i < WotsPlus.WOTS_LOG_L; i++)
        {
            for (j = 0; j < (l >>> 1); j++)
            {
                hs.hash_2n_n_mask(wots_pk, pkOff + j * SPHINCS256_HASH_LENGTH, 
                                  wots_pk, pkOff + j * 2 * SPHINCS256_HASH_LENGTH, 
                                  masks, masksOff + i * 2 * SPHINCS256_HASH_LENGTH);
            }

            if ((l & 1) != 0)
            {
                System.arraycopy(wots_pk, pkOff + (l - 1) * SPHINCS256_HASH_LENGTH,
                                 wots_pk, pkOff + (l >>> 1) * SPHINCS256_HASH_LENGTH, 
                                 SPHINCS256_HASH_LENGTH);
                l = (l >>> 1) + 1;
            }
            else
            {
                l = (l >>> 1);
            }
        }
        System.arraycopy(wots_pk, pkOff, leaf, leafOff, SPHINCS256_HASH_LENGTH);
    }

    static void treehash(Hash hs, byte[] node, int nodeOff, int height, byte[] sk, 
                         leafaddr leaf, byte[] masks, int masksOff)
    {
        leafaddr a = new leafaddr(leaf);
        int lastnode, i;
        byte[] stack = new byte[(height + 1) * SPHINCS256_HASH_LENGTH];
        int[] stacklevels = new int[height + 1];
        int stackoffset = 0;

        lastnode = (int)(a.subleaf + (1 << height));

        for (; a.subleaf < lastnode; a.subleaf++)
        {
            gen_leaf_wots(hs, stack, stackoffset * SPHINCS256_HASH_LENGTH, masks, 
                          masksOff, sk, a);
            stacklevels[stackoffset] = 0;
            stackoffset++;
            while (stackoffset > 1 && stacklevels[stackoffset - 1] == stacklevels[stackoffset - 2])
            {
                //MASKS
                int maskoffset = 2 * (stacklevels[stackoffset - 1] + 
                                      WotsPlus.WOTS_LOG_L) * SPHINCS256_HASH_LENGTH;

                hs.hash_2n_n_mask(stack, (stackoffset - 2) * SPHINCS256_HASH_LENGTH,
                                  stack, (stackoffset - 2) * SPHINCS256_HASH_LENGTH,
                    masks, masksOff + maskoffset);
                stacklevels[stackoffset - 2]++;
                stackoffset--;
            }
        }
        for (i = 0; i < SPHINCS256_HASH_LENGTH; i++)
        {
            node[nodeOff + i] = stack[i];
        }
    }

    static void gen_leaf_wots(Hash hs, byte[] leaf, int leafOff, byte[] masks, 
                              int masksOff, byte[] sk, leafaddr a)
    {
        byte[] seed = new byte[SPHINCS256_SEED_LENGTH];
        byte[] pk = new byte[WotsPlus.WOTS_L * SPHINCS256_HASH_LENGTH];

        WotsPlus w = new WotsPlus();

        Seed.get_seed(hs, seed, 0, sk, a);

        w.wots_pkgen(hs, pk, 0, seed, 0, masks, masksOff);

        l_tree(hs, leaf, leafOff, pk, 0, masks, masksOff);
    }
}