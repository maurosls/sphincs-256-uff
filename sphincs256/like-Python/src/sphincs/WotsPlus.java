package sphincs;

import java.math.*;
import java.util.List;

/**
 * @author Bernardino
 */

public class WotsPlus
{
    // tamanho dos hash
    private int bitsHashes;
    
    // parametro de winternitz para criação da estrutrua
    private int paramWinternitz;
    
    // wots+ proposto por bernstein (pagina 7 do artigo WOTS+)
    private int l1;
    private int l2;
    
    // quantidade de chaves pseudo-aleatorias que serão geradas; sphincs-256 ==> L = 67 = L1 + L2 = 64 + 3
    private int l;
    
    public WotsPlus (int bitsHashes, int paramWinternitz)
    {
        this.bitsHashes = bitsHashes;
        
        this.paramWinternitz = paramWinternitz;
        
        this.l1 = (int)(Math.ceil(bitsHashes/(int)(Math.log(paramWinternitz)/Math.log(2))));
        this.l2 = (int)(Math.floor((int)(Math.log(this.l1*(paramWinternitz-1))/Math.log(2))/(int)(Math.log(paramWinternitz)/Math.log(2))))+1;
        
        this.l = this.l1 + this.l2;
    }
    
    // Chaining function descrita na página 7 do artigo
    private byte[] chains(byte[] x, byte[] masks)
    {
        // 1. Para cada round Li ate L, faça
        // 1.1. Calcule recursivamente, c[i-1] XOR r[i]
        // 1.2. Aplique a função de hash F e atribua a c[i]
        // 2. Retorne c
    }

    // Não sei se precisa disso
    public /*verificar tipo de retorno*/ void int_to_basew(/*x, base*/){}

    // Nao sei se precisa disso
    public /*verificar tipo de retorno*/ void chainSizes(/*message*/){}
    
    // Gera a chave publica referente a arvore. (talvez possa retornar uma String Hexadecimal, caso seja mais facil)
    public byte[][] keygen(byte[] seed, List<byte[]> masks)
    {
        // 1. Gera a chave secreta {SK} utilizando a seed. (em bytes)
        // 2. Divide a chave secreta {SK} em pedaços de 8 bytes. (em bytes)
        // 3. Para cada chave publica PKi (de 1 ate L) sera gerada uma chave correspondente a aplicacao da funcao {chains} com os parametros {SKi} e a mascara de bits {masks}.
        // 3.1. observacao: pk = lista de PKi's, onde cada PKi = chains(SKi, r)
        // 3.2. por exemplo: PK1 = chains(SK1, r)
    }

    // Assina as mensagens usando a arvore wots+
    // (*) a mensagem pode ser interpretada como bytes
    public byte[] void sign(String message, byte[] seed, List<byte[]> masks)
    {
        // 1. Gera a chave secreta {SK} utilizando a seed. (em bytes)
        // 2. Divide a chave secreta {SK} em pedaços de 8 bytes. (em bytes)
        // 3. Calcula-se a representação w-ária C (no máximo L2), fazendo, C = somatorio(w - 1 - Mi), onde Mi é um pedaço da mensagem.
        // 4. Calcula-se B sendo a concatenação da representação de M e C. ( B = M concatenado com C)
        // 5. Para cada Bi em B, aplique a função {chain} com os parametros {SKi} e {masks}
    }

    // Verifica se a assinatura eh valida. Pagina 8 do artigo (Verification algorithm).
    public boolean void verify(/*message, signature, */List<byte[]> masks)
    {
        // 1. Calcule B seguindo os passos de {sign} e faça
        // 2. De Bi até B_w-1 faça, {chain} com os parametros {signature} e {masks} (e guarde essa informação em PK_)
        // 3. Retorne verdadeiro caso a assinatura seja valida, e falso caso contrário. (comparando a chave publica PK_ com PK, se necessario)
    }

    public int getL()
    {
        return this.l;
    }
}
