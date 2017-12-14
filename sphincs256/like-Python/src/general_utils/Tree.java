package general_utils;

import java.util.ArrayList;
import java.util.List;
import java.math.*;

/**
 * @author Bernardino
 */
public class Tree
{
    NohArvore root;
    
    public Tree(NohArvore r)
    {
        this.root = r;
    }
    
    // Checa se a árvore é completa e é uma Binary Hash Tree (caso especial da L-Tree)
    public void hashTree(List<byte[]> leafs)
    {
        return (leafs.size() & leafs.size() - 1) == 0? LTree(leafs) : null;
    }
    
    // Acho que isso teria que ser feito em alguma outra classe.
    public /*verificar tipo de retorno*/void lTree(List<byte[]> leafs)
    {
        List<byte[]> layer = new ArrayList<>(leafs);
        //yield layer ==> retorna a camada atual e depois continua a execução a partir desse ponto
        for (int i = 0; i < (int)(Math.ceil(Math.log(leafs.size())/Math.log(2))); i++) {
            List<byte[]> nextLayer = new ArrayList<>();
        }
    }
    
    // Retorna o caminho da autenticação, ou seja, a lista dos nos que compoe a autenticacao
    public NohArvore[] void authPath(/*tree, index*/)
    {
        
    }
    
    // Define a raiz da arvore
    public void defineRoot(NohArvore r)
    {
        this.root = r;
    }
    
    // Constroi a arvore de baixo para cima, começando por um noh {leaf} e com indice indicado por {i}
    public Tree construct(NohArvore[] auth_path, NohArvore leaf, int i)
    {
        // 1. define que o inicio sera a partir do NohArvore leaf
        // 2. Para cada NohArvore na lista de autenticacao {auth_path, id_noh} faça
        // 2.1. Se o noh corrente é par, calcule o proximo nó será o hash(noh, vizinho, id_noh). Senão, o noh será hash(vizinho, noh, id_noh).
        // 2.2 Pegue o pai do noh, ou seja, o id_noh = floor(id_corrente / 2)
        // 3. Retorne o noh
    }
    
    // Retorna a raiz da arvore
    public NohArvore getRoot()
    {
        return this.root;
    }
}
